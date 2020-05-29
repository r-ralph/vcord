package ms.ralph.vcord.gui

import ms.ralph.vcord.core.model.MidiInputDevice
import ms.ralph.vcord.core.model.MidiInputDeviceId
import ms.ralph.vcord.gui.model.MidiInputDeviceListViewData
import ms.ralph.vcord.gui.model.MidiInputDeviceViewData

internal object GeneralViewDataFactory {
    fun createMidiInputDeviceListViewData(
        midiInputDeviceList: List<MidiInputDevice>,
        selectedMidiInputDeviceId: MidiInputDeviceId?
    ): MidiInputDeviceListViewData {
        val deviceListViewData = mutableListOf<MidiInputDeviceViewData>(MidiInputDeviceViewData.Invalid).apply {
            addAll(midiInputDeviceList.map { it.toViewData() })
        }
        val selectedDeviceViewData = midiInputDeviceList.firstOrNull { it.id == selectedMidiInputDeviceId }
            ?.toViewData() ?: MidiInputDeviceViewData.Invalid
        return MidiInputDeviceListViewData(deviceListViewData, selectedDeviceViewData)
    }

    private fun MidiInputDevice.toViewData(): MidiInputDeviceViewData = MidiInputDeviceViewData.Actual(
        mixerInfo.name,
        mixerInfo.name
    )
}
