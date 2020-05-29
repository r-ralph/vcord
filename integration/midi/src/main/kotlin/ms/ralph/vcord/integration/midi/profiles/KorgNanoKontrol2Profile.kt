package ms.ralph.vcord.integration.midi.profiles

import ms.ralph.vcord.core.model.MidiControllerSlot
import ms.ralph.vcord.integration.midi.ControlMessage
import javax.sound.midi.MidiMessage
import javax.sound.midi.ShortMessage

class KorgNanoKontrol2Profile : MidiDeviceProfile {

    override fun convert(originalMessage: MidiMessage): ControlMessage? {
        val message = originalMessage as? ShortMessage ?: return null
        if (message.command != ShortMessage.CONTROL_CHANGE) return null
        val slot = getSlotFromRawData(message.data1) ?: return null
        val volume = getVolumeFromRawData(message.data2)
        return ControlMessage.Volume(slot, volume)
    }

    private fun getSlotFromRawData(value: Int): MidiControllerSlot? = when (value) {
        0 -> MidiControllerSlot.SLOT_1
        1 -> MidiControllerSlot.SLOT_2
        2 -> MidiControllerSlot.SLOT_3
        3 -> MidiControllerSlot.SLOT_4
        4 -> MidiControllerSlot.SLOT_5
        5 -> MidiControllerSlot.SLOT_6
        6 -> MidiControllerSlot.SLOT_7
        7 -> MidiControllerSlot.SLOT_8
        else -> null
    }

    private fun getVolumeFromRawData(value: Int): Double = value.toDouble() / (127.0 / 2)
}
