package ms.ralph.vcord.integration.discord

import io.reactivex.rxjava3.schedulers.Schedulers
import ms.ralph.vcord.core.manager.AudioDataManager
import ms.ralph.vcord.core.manager.TeamDataManager
import ms.ralph.vcord.core.model.BotStatus
import ms.ralph.vcord.core.model.Guild
import ms.ralph.vcord.core.model.GuildId
import ms.ralph.vcord.core.model.PlayerSlot
import ms.ralph.vcord.core.model.User
import ms.ralph.vcord.core.model.UserId
import ms.ralph.vcord.core.model.VoiceChannel
import ms.ralph.vcord.core.model.VoiceChannelId
import ms.ralph.vcord.integration.audio.VcordAudioClient
import ms.ralph.vcord.util.FixedNameSingleThreadFactory
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.audio.AudioReceiveHandler
import net.dv8tion.jda.api.audio.UserAudio
import net.dv8tion.jda.api.audio.hooks.ConnectionListener
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.guild.GenericGuildEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference
import net.dv8tion.jda.api.entities.Guild as JdaGuild
import net.dv8tion.jda.api.entities.User as JdaUser
import net.dv8tion.jda.api.entities.VoiceChannel as JdaVoiceChannel

internal class VcordDiscordClientImpl(
    teamManager: TeamDataManager,
    audioDataManager: AudioDataManager,
    private val apiKey: String,
    audioClient: VcordAudioClient
) : VcordDiscordClient(teamManager) {

    private val executor: Executor =
        Executors.newSingleThreadExecutor(FixedNameSingleThreadFactory("discord-client-${teamManager.threadNameSuffix}"))

    private val audioProcessors: List<AudioProcessor> = listOf(
        AudioProcessor(
            audioClient,
            PlayerSlot.SLOT_1,
            teamManager,
            audioDataManager,
            "${teamManager.threadNameSuffix}-1"
        ),
        AudioProcessor(
            audioClient,
            PlayerSlot.SLOT_2,
            teamManager,
            audioDataManager,
            "${teamManager.threadNameSuffix}-2"
        ),
        AudioProcessor(
            audioClient,
            PlayerSlot.SLOT_3,
            teamManager,
            audioDataManager,
            "${teamManager.threadNameSuffix}-3"
        ),
        AudioProcessor(
            audioClient,
            PlayerSlot.SLOT_4,
            teamManager,
            audioDataManager,
            "${teamManager.threadNameSuffix}-4"
        )
    )

    private val jdaClient: AtomicReference<JDA> = AtomicReference()

    override fun init() {
        teamManager.updateBotStatus(BotStatus.CONNECTING)

        audioProcessors.forEach(AudioProcessor::start)

        val localJdaClient = JDABuilder.createDefault(apiKey)
            .addEventListeners(VcordListener())
            .build()
        jdaClient.set(localJdaClient)
        localJdaClient.status

        teamManager.selectedVoiceChannelId.observeOn(Schedulers.from(executor)).subscribe {
            updateVoiceChannel(it.orElse(null))
        }
    }

    override fun shutdown() {
        jdaClient.getAndSet(null)?.shutdownNow()
    }

    private fun updateVoiceChannel(voiceChannelId: VoiceChannelId?) {
        val localJdaClient = jdaClient.get() ?: return
        if (voiceChannelId == null) {
            logger.debug("Closing audio connection")
            localJdaClient.audioManagerCache.forEach { it.closeAudioConnection() }
            return
        }
        logger.debug("Connecting to $voiceChannelId")
        val jdaVoiceChannel = localJdaClient.getVoiceChannelById(voiceChannelId.value) ?: return
        localJdaClient.audioManagerCache
            .filter { it.guild.id != jdaVoiceChannel.guild.id }
            .forEach { it.closeAudioConnection() }

        val audioManager = jdaVoiceChannel.guild.audioManager

        if (!audioManager.isConnected) {
            teamManager.updateBotStatus(BotStatus.VOICE_CONNECTING)
        }

        audioManager.isSelfMuted = true
        audioManager.connectionListener = AudioConnectionListener()
        audioManager.receivingHandler = AudioReceiver(audioProcessors)
        audioManager.openAudioConnection(jdaVoiceChannel)
    }

    private fun JdaGuild.toGuildModel(): Guild = Guild(
        GuildId(id),
        name,
        voiceChannels.map { it.toVoiceChannelModel() }
    )

    private fun JdaVoiceChannel.toVoiceChannelModel(): VoiceChannel = VoiceChannel(
        VoiceChannelId(id),
        name,
        members.filter { !it.user.isBot }.map { it.toUserModel() }
    )

    private fun Member.toUserModel(): User = User(
        UserId(id),
        effectiveName,
        user.effectiveAvatarUrl
    )

    private inner class AudioConnectionListener : ConnectionListener {

        override fun onStatusChange(status: ConnectionStatus) {
            logger.debug("Status changed: $status")
            when (status) {
                ConnectionStatus.CONNECTING_AWAITING_AUTHENTICATION -> teamManager.updateBotStatus(BotStatus.VOICE_CONNECTING)
                ConnectionStatus.CONNECTED -> teamManager.updateBotStatus(BotStatus.VOICE_ESTABLISHED)
                ConnectionStatus.NOT_CONNECTED -> teamManager.updateBotStatus(BotStatus.READY)
                ConnectionStatus.DISCONNECTED_KICKED_FROM_CHANNEL -> teamManager.updateSelectedVoiceChannelId(null)
                else -> Unit
            }
        }

        override fun onUserSpeaking(user: JdaUser, speaking: Boolean) = Unit

        override fun onPing(ping: Long) = Unit
    }

    private inner class VcordListener : ListenerAdapter() {

        override fun onReady(event: ReadyEvent) {
            logger.debug("onReady" + event.jda.guilds)
            teamManager.updateAvailableGuildList(
                event.jda.guilds.map { it.toGuildModel() }
            )
            teamManager.updateBotStatus(BotStatus.READY)
        }

        override fun onGenericGuild(event: GenericGuildEvent) {
            logger.debug("onGenericGuild")
            teamManager.updateAvailableGuildList(
                event.jda.guilds.map { it.toGuildModel() }
            )
        }
    }

    private inner class AudioReceiver(
        private val audioProcessorList: List<AudioProcessor>
    ) : AudioReceiveHandler {

        override fun handleUserAudio(userAudio: UserAudio) {
            for (audioProcessor in audioProcessorList) {
                audioProcessor.handle(userAudio)
            }
        }

        override fun canReceiveUser(): Boolean = true
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(VcordDiscordClientImpl::class.java)
    }
}
