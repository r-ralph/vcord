package ms.ralph.vcord.integration.midi.profiles

import ms.ralph.vcord.integration.midi.ControlMessage
import javax.sound.midi.MidiMessage

interface MidiDeviceProfile {
    fun convert(originalMessage: MidiMessage): ControlMessage?
}
