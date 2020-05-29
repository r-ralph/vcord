package ms.ralph.vcord.gui.model

data class MidiInputDeviceListViewData(
    val midiInputDeviceList: List<MidiInputDeviceViewData>,
    val selectedMidiInputDevice: MidiInputDeviceViewData
)
