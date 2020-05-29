package ms.ralph.vcord.core.model

data class VoiceChannel(
    val id: VoiceChannelId,
    val name: String,
    val attendedUsers: List<User>
)
