package ms.ralph.vcord

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamConfig(
    @SerialName("discord_bot_api_key")
    val discordBotApiKey: String? = null,
    @SerialName("default_audio_device_id")
    val defaultAudioDeviceId: String? = null
)
