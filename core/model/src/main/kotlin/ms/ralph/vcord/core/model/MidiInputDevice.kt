package ms.ralph.vcord.core.model

import javax.sound.midi.MidiDevice

class MidiInputDevice(
    val id: MidiInputDeviceId,
    val mixerInfo: MidiDevice.Info
)
