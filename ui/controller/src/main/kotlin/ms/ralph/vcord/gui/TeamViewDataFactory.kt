package ms.ralph.vcord.gui

import ms.ralph.vcord.core.model.AudioOutputDevice
import ms.ralph.vcord.core.model.AudioOutputDeviceId
import ms.ralph.vcord.core.model.AudioStatus
import ms.ralph.vcord.core.model.BotStatus
import ms.ralph.vcord.core.model.Guild
import ms.ralph.vcord.core.model.GuildId
import ms.ralph.vcord.core.model.MidiStatus
import ms.ralph.vcord.core.model.User
import ms.ralph.vcord.core.model.UserId
import ms.ralph.vcord.core.model.VoiceChannel
import ms.ralph.vcord.core.model.VoiceChannelId
import ms.ralph.vcord.gui.model.AudioOutputDeviceListViewData
import ms.ralph.vcord.gui.model.AudioOutputDeviceViewData
import ms.ralph.vcord.gui.model.AudioStatusViewData
import ms.ralph.vcord.gui.model.BotStatusViewData
import ms.ralph.vcord.gui.model.GuildListViewData
import ms.ralph.vcord.gui.model.GuildViewData
import ms.ralph.vcord.gui.model.MidiStatusViewData
import ms.ralph.vcord.gui.model.UserListViewData
import ms.ralph.vcord.gui.model.UserViewData
import ms.ralph.vcord.gui.model.VoiceChannelListViewData
import ms.ralph.vcord.gui.model.VoiceChannelViewData

internal object TeamViewDataFactory {

    fun createMidiStatusViewData(midiStatus: MidiStatus): MidiStatusViewData =
        MidiStatusViewData(midiStatus.getDisplayText())

    fun createAudioStatusViewData(audioStatus: AudioStatus): AudioStatusViewData =
        AudioStatusViewData(audioStatus.getDisplayText())

    fun createBotStatusViewData(botStatus: BotStatus): BotStatusViewData = BotStatusViewData(botStatus.getDisplayText())

    fun createAudioOutputDeviceListViewData(
        audioOutputDeviceList: List<AudioOutputDevice>,
        selectedAudioOutputDevice: AudioOutputDeviceId?
    ): AudioOutputDeviceListViewData {
        val deviceListViewData = mutableListOf<AudioOutputDeviceViewData>(AudioOutputDeviceViewData.Invalid).apply {
            addAll(audioOutputDeviceList.map { it.toViewData() })
        }
        val selectedDeviceViewData =
            audioOutputDeviceList.firstOrNull { it.id == selectedAudioOutputDevice }?.toViewData()
                ?: AudioOutputDeviceViewData.Invalid

        return AudioOutputDeviceListViewData(deviceListViewData, selectedDeviceViewData)
    }

    private fun AudioOutputDevice.toViewData(): AudioOutputDeviceViewData = AudioOutputDeviceViewData.Actual(
        mixerInfo.name,
        mixerInfo.name
    )

    fun createGuildListViewData(guildList: List<Guild>, selectedGuildId: GuildId?): GuildListViewData {
        val guildListViewData = mutableListOf<GuildViewData>(GuildViewData.Invalid).apply {
            addAll(guildList.map { it.toViewData() })
        }
        val selectedGuildViewData =
            guildList.firstOrNull { it.id == selectedGuildId }?.toViewData() ?: GuildViewData.Invalid

        return GuildListViewData(guildListViewData, selectedGuildViewData)
    }

    fun createVoiceChannelListViewData(
        voiceChannelList: List<VoiceChannel>,
        selectedVoiceChannelId: VoiceChannelId?
    ): VoiceChannelListViewData {
        val voiceChannelListViewData = mutableListOf<VoiceChannelViewData>(VoiceChannelViewData.Invalid).apply {
            addAll(voiceChannelList.map { it.toViewData() })
        }
        val selectedVoiceChannelViewData =
            voiceChannelList.firstOrNull { it.id == selectedVoiceChannelId }?.toViewData()
                ?: VoiceChannelViewData.Invalid

        return VoiceChannelListViewData(voiceChannelListViewData, selectedVoiceChannelViewData)
    }

    fun createUserListViewData(
        userList: List<User>,
        selectedUserId: UserId?
    ): UserListViewData {
        val userListViewData = mutableListOf<UserViewData>(UserViewData.Invalid).apply {
            addAll(userList.map { it.toViewData() })
        }
        val userViewData = userList.firstOrNull { it.id == selectedUserId }?.toViewData() ?: UserViewData.Invalid

        return UserListViewData(userListViewData, userViewData)
    }

    private fun MidiStatus.getDisplayText(): String = when (this) {
        MidiStatus.DEFAULT -> ""
        MidiStatus.NO_DEVICE -> "No device"
        MidiStatus.CONNECTED -> "Connected"
        MidiStatus.ERROR_NO_PROFILE -> "Error: Unknown device"
    }

    private fun AudioStatus.getDisplayText(): String = when (this) {
        AudioStatus.DEFAULT -> ""
        AudioStatus.NO_LINE -> "No output"
        AudioStatus.LINKED -> "Linked"
    }

    private fun BotStatus.getDisplayText(): String = when (this) {
        BotStatus.DEFAULT -> ""
        BotStatus.CONNECTING -> "Connecting"
        BotStatus.READY -> "Ready"
        BotStatus.VOICE_CONNECTING -> "Connecting audio"
        BotStatus.VOICE_ESTABLISHED -> "Audio ready"
        BotStatus.ERROR_NO_API_KEY -> "Error: No API key"
    }

    private fun Guild.toViewData(): GuildViewData = GuildViewData.Actual(
        id.value,
        name
    )

    private fun VoiceChannel.toViewData(): VoiceChannelViewData = VoiceChannelViewData.Actual(
        id.value,
        "$name (${attendedUsers.size})"
    )

    private fun User.toViewData(): UserViewData = UserViewData.Actual(
        id.value,
        name,
        iconUrl
    )
}
