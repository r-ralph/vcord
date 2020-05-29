package ms.ralph.vcord.integration.midi.profiles

import ms.ralph.vcord.core.model.MidiInputDeviceId

object MidiDeviceProfiles {

    private val DEVICES: Map<MidiInputDeviceId, MidiDeviceProfile> = mapOf(
        MidiInputDeviceId("nanoKONTROL2") to KorgNanoKontrol2Profile()
    )

    fun getProfile(midiInputDeviceId: MidiInputDeviceId): MidiDeviceProfile? = DEVICES[midiInputDeviceId]
}
