package ms.ralph.vcord.core.model

data class Guild(
    val id: GuildId,
    val name: String,
    val voiceChannels: List<VoiceChannel>
)
