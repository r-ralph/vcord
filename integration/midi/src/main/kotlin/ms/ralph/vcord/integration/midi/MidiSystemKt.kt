package ms.ralph.vcord.integration.midi

import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem

object MidiSystemKt {
    fun isTransmitterAvailable(midiInfo: MidiDevice.Info): Boolean =
        MidiSystem.getMidiDevice(midiInfo).maxTransmitters != 0
}
