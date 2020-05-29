package ms.ralph.vcord.gui

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.Observables
import ms.ralph.vcord.core.manager.MidiDataManager
import ms.ralph.vcord.core.model.MidiInputDeviceId
import ms.ralph.vcord.gui.model.AudioStatusViewData
import ms.ralph.vcord.gui.model.MidiInputDeviceListViewData
import ms.ralph.vcord.gui.model.MidiInputDeviceViewData
import ms.ralph.vcord.gui.model.MidiStatusViewData

class GeneralViewDataController(private val midiDataManager: MidiDataManager) {

    val midiStatusViewData: Observable<MidiStatusViewData>
        get() = midiDataManager.midiStatus.map(TeamViewDataFactory::createMidiStatusViewData).distinctUntilChanged()

    val midiInputDeviceListViewData: Observable<MidiInputDeviceListViewData>
        get() = Observables.combineLatest(
            midiDataManager.midiInputDeviceList,
            midiDataManager.selectedMidiInputDeviceId
        ) { midiInputDeviceList, selectedMidiInputDeviceId ->
            GeneralViewDataFactory.createMidiInputDeviceListViewData(
                midiInputDeviceList,
                selectedMidiInputDeviceId.orElse(null)
            )
        }.distinctUntilChanged()


    fun updateSelectedMidiInputDevice(midiInputDevice: MidiInputDeviceViewData) =
        when (midiInputDevice) {
            is MidiInputDeviceViewData.Actual -> midiDataManager.updateSelectedMidiInputDeviceId(
                MidiInputDeviceId(midiInputDevice.id)
            )
            MidiInputDeviceViewData.Invalid -> midiDataManager.updateSelectedMidiInputDeviceId(null)
        }
}
