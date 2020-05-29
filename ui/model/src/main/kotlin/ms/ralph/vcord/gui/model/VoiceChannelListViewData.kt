package ms.ralph.vcord.gui.model

data class VoiceChannelListViewData(
    val voiceChannelList: List<VoiceChannelViewData>,
    val selectedVoiceChannel: VoiceChannelViewData
)
