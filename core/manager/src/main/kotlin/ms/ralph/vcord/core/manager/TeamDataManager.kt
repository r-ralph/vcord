package ms.ralph.vcord.core.manager

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.Observables
import ms.ralph.vcord.core.model.AudioStatus
import ms.ralph.vcord.core.model.BotStatus
import ms.ralph.vcord.core.model.Guild
import ms.ralph.vcord.core.model.GuildId
import ms.ralph.vcord.core.model.PlayerSlot
import ms.ralph.vcord.core.model.User
import ms.ralph.vcord.core.model.UserId
import ms.ralph.vcord.core.model.VoiceChannel
import ms.ralph.vcord.core.model.VoiceChannelId
import ms.ralph.vcord.util.SubjectWrapper
import ms.ralph.vcord.util.asOptional
import org.slf4j.LoggerFactory
import java.util.EnumMap
import java.util.Optional

class TeamDataManager(val threadNameSuffix: String) {
    private val botStatusSubject: SubjectWrapper<BotStatus> = SubjectWrapper.createSerialized(BotStatus.DEFAULT)

    private val availableGuildListSubject: SubjectWrapper<List<Guild>> = SubjectWrapper.createSerialized()

    private val selectedGuildIdSubject: SubjectWrapper<Optional<GuildId>> =
        SubjectWrapper.createSerialized(Optional.empty())

    private val selectedVoiceChannelIdSubject: SubjectWrapper<Optional<VoiceChannelId>> =
        SubjectWrapper.createSerialized(Optional.empty())

    private val selectedUserIdSubjectMap: EnumMap<PlayerSlot, SubjectWrapper<Optional<UserId>>> =
        EnumMap<PlayerSlot, SubjectWrapper<Optional<UserId>>>(PlayerSlot::class.java).apply {
            PlayerSlot.values().forEach {
                put(it, SubjectWrapper.createSerialized(Optional.empty()))
            }
        }

    private val speakingStatusSubjectMap: EnumMap<PlayerSlot, SubjectWrapper<Boolean>> =
        EnumMap<PlayerSlot, SubjectWrapper<Boolean>>(PlayerSlot::class.java).apply {
            PlayerSlot.values().forEach {
                put(it, SubjectWrapper.createSerialized(false))
            }
        }

    val botStatus: Observable<BotStatus> by botStatusSubject

    val availableGuildList: Observable<List<Guild>> by availableGuildListSubject

    val selectedGuildId: Observable<Optional<GuildId>> by selectedGuildIdSubject

    val selectedVoiceChannelId: Observable<Optional<VoiceChannelId>> by selectedVoiceChannelIdSubject

    val availableVoiceChannelList: Observable<List<VoiceChannel>> = Observables
        .combineLatest(availableGuildList, selectedGuildId) { guildList, guildId ->
            guildList.firstOrNull { it.id == guildId.orElse(null) }?.voiceChannels ?: emptyList()
        }
        .replay(1)
        .refCount()

    val voiceAvailableUserList: Observable<List<User>> = Observables
        .combineLatest(availableVoiceChannelList, selectedVoiceChannelId) { voiceChannelList, voiceChannelId ->
            voiceChannelList.firstOrNull { it.id == voiceChannelId.orElse(null) }?.attendedUsers ?: emptyList()
        }
        .replay(1)
        .refCount()

    init {
        selectedGuildId.subscribe {
            updateSelectedVoiceChannelId(null)
        }
        selectedVoiceChannelId.subscribe {
            updateSelectedUserId(PlayerSlot.SLOT_1, null)
            updateSelectedUserId(PlayerSlot.SLOT_2, null)
            updateSelectedUserId(PlayerSlot.SLOT_3, null)
            updateSelectedUserId(PlayerSlot.SLOT_4, null)
        }
    }

    fun getSelectedUserId(slot: PlayerSlot): Observable<Optional<UserId>> {
        val observable by checkNotNull(selectedUserIdSubjectMap[slot])
        return observable
    }

    fun getSpeakingStatus(slot: PlayerSlot): Observable<Boolean> {
        val observable by checkNotNull(speakingStatusSubjectMap[slot])
        return observable
    }

    fun updateBotStatus(botStatus: BotStatus) {
        logger.debug("Bot status changed: $botStatus")
        botStatusSubject.onNext(botStatus)
    }

    fun updateAvailableGuildList(availableGuildList: List<Guild>) = availableGuildListSubject.onNext(availableGuildList)

    fun updateSelectedGuildId(guildId: GuildId?) {
        logger.debug("Guild selected: $guildId")
        selectedGuildIdSubject.onNext(guildId.asOptional())
    }

    fun updateSelectedVoiceChannelId(voiceChannelId: VoiceChannelId?) {
        logger.debug("VC selected: $voiceChannelId")
        selectedVoiceChannelIdSubject.onNext(voiceChannelId.asOptional())
    }

    fun updateSelectedUserId(playerSlot: PlayerSlot, userId: UserId?) {
        logger.debug("Player for $playerSlot selected: $userId")
        checkNotNull(selectedUserIdSubjectMap[playerSlot]).onNext(userId.asOptional())
    }

    fun updateSpeakingState(playerSlot: PlayerSlot, isSpeaking: Boolean) {
        checkNotNull(speakingStatusSubjectMap[playerSlot]).onNext(isSpeaking)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TeamDataManager::class.java)
    }
}
