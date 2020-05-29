package ms.ralph.vcord.gui.model

sealed class MidiInputDeviceViewData {
    data class Actual(
        val id: String,
        val name: String
    ) : MidiInputDeviceViewData()

    object Invalid : MidiInputDeviceViewData()
}
