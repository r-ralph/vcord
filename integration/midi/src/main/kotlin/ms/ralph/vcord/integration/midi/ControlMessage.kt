package ms.ralph.vcord.integration.midi

import ms.ralph.vcord.core.model.MidiControllerSlot

sealed class ControlMessage {
    data class Volume(
        val slot: MidiControllerSlot,
        val value: Double
    ) : ControlMessage()
}
