package ms.ralph.vcord.gui.model

sealed class VoiceChannelViewData {
    data class Actual(
        val id: String,
        val name: String
    ) : VoiceChannelViewData()

    object Invalid : VoiceChannelViewData()
}
