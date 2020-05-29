package ms.ralph.vcord.gui

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.Observables
import ms.ralph.vcord.core.manager.AudioDataManager
import ms.ralph.vcord.core.manager.TeamDataManager
import ms.ralph.vcord.core.model.AudioOutputDeviceId
import ms.ralph.vcord.core.model.GuildId
import ms.ralph.vcord.core.model.PlayerSlot
import ms.ralph.vcord.core.model.VoiceChannelId
import ms.ralph.vcord.gui.model.AudioOutputDeviceListViewData
import ms.ralph.vcord.gui.model.AudioOutputDeviceViewData
import ms.ralph.vcord.gui.model.AudioStatusViewData
import ms.ralph.vcord.gui.model.GuildListViewData
import ms.ralph.vcord.gui.model.GuildViewData
import ms.ralph.vcord.gui.model.BotStatusViewData
import ms.ralph.vcord.gui.model.VoiceChannelListViewData
import ms.ralph.vcord.gui.model.VoiceChannelViewData

class TeamViewDataController(
    private val teamDataManager: TeamDataManager,
    private val audioDataManager: AudioDataManager,
    private val playerViewDataControllerMap: Map<PlayerSlot, PlayerViewDataController>
) {

    val audioStatusViewData: Observable<AudioStatusViewData>
        get() = audioDataManager.audioStatus.map(TeamViewDataFactory::createAudioStatusViewData).distinctUntilChanged()

    val botStatusViewData: Observable<BotStatusViewData>
        get() = teamDataManager.botStatus.map(TeamViewDataFactory::createBotStatusViewData).distinctUntilChanged()

    val audioOutputDeviceListViewData: Observable<AudioOutputDeviceListViewData>
        get() = Observables.combineLatest(
            audioDataManager.audioOutputDeviceList,
            audioDataManager.selectedAudioOutputDeviceId
        ) { audioOutputDeviceList, selectedAudioOutputDeviceId ->
            TeamViewDataFactory.createAudioOutputDeviceListViewData(
                audioOutputDeviceList,
                selectedAudioOutputDeviceId.orElse(null)
            )
        }.distinctUntilChanged()

    val guildListViewData: Observable<GuildListViewData>
        get() = Observables.combineLatest(
            teamDataManager.availableGuildList,
            teamDataManager.selectedGuildId
        ) { guildList, selectedGuildId ->
            TeamViewDataFactory.createGuildListViewData(guildList, selectedGuildId.orElse(null))
        }.distinctUntilChanged()

    val voiceChannelListViewData: Observable<VoiceChannelListViewData>
        get() = Observables.combineLatest(
            teamDataManager.availableVoiceChannelList,
            teamDataManager.selectedVoiceChannelId
        ) { voiceChannelList, selectedVoiceChannelId ->
            TeamViewDataFactory.createVoiceChannelListViewData(voiceChannelList, selectedVoiceChannelId.orElse(null))
        }.distinctUntilChanged()

    fun getPlayerViewDataController(playerSlot: PlayerSlot): PlayerViewDataController =
        checkNotNull(playerViewDataControllerMap[playerSlot])

    fun updateSelectedGuild(guild: GuildViewData) = when (guild) {
        is GuildViewData.Actual -> teamDataManager.updateSelectedGuildId(GuildId(guild.id))
        GuildViewData.Invalid -> teamDataManager.updateSelectedGuildId(null)
    }

    fun updateSelectedAudioOutputDevice(audioOutputDevice: AudioOutputDeviceViewData) =
        when (audioOutputDevice) {
            is AudioOutputDeviceViewData.Actual -> audioDataManager.updateSelectedAudioOutputDeviceId(
                AudioOutputDeviceId(audioOutputDevice.id)
            )
            AudioOutputDeviceViewData.Invalid -> audioDataManager.updateSelectedAudioOutputDeviceId(null)
        }

    fun updateSelectedVoiceChannel(voiceChannel: VoiceChannelViewData): Unit = when (voiceChannel) {
        is VoiceChannelViewData.Actual -> teamDataManager.updateSelectedVoiceChannelId(VoiceChannelId(voiceChannel.id))
        VoiceChannelViewData.Invalid -> teamDataManager.updateSelectedVoiceChannelId(null)
    }
}
