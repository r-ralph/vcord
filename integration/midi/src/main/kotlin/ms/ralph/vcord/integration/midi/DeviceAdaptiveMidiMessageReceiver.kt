package ms.ralph.vcord.integration.midi

import ms.ralph.vcord.core.manager.MidiDataManager
import ms.ralph.vcord.integration.midi.profiles.MidiDeviceProfile
import javax.sound.midi.MidiMessage
import javax.sound.midi.Receiver

class DeviceAdaptiveMidiMessageReceiver(
    private val midiDataManager: MidiDataManager,
    private val profile: MidiDeviceProfile
) : Receiver {
    override fun send(message: MidiMessage, timeStamp: Long) {
        val controlMessage = profile.convert(message) ?: return
        process(controlMessage)
    }

    private fun process(controlMessage: ControlMessage) = when (controlMessage) {
        is ControlMessage.Volume -> midiDataManager.updateVolume(controlMessage.slot, controlMessage.value)
    }

    override fun close() = Unit
}
