package ms.ralph.vcord

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppConfig(
    @SerialName("default_midi_device_id")
    val defaultMidiDeviceId: String? = null,
    @SerialName("alpha")
    val alphaTeamConfig: TeamConfig? = null,
    @SerialName("bravo")
    val bravoTeamConfig: TeamConfig? = null
)
