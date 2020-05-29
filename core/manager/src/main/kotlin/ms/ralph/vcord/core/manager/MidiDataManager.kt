package ms.ralph.vcord.core.manager

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.Observables
import ms.ralph.vcord.core.model.AudioStatus
import ms.ralph.vcord.core.model.MidiControllerSlot
import ms.ralph.vcord.core.model.MidiInputDevice
import ms.ralph.vcord.core.model.MidiInputDeviceId
import ms.ralph.vcord.core.model.MidiStatus
import ms.ralph.vcord.util.SubjectWrapper
import ms.ralph.vcord.util.asOptional
import org.slf4j.LoggerFactory
import java.util.EnumMap
import java.util.Optional

class MidiDataManager {
    private val midiStatusSubject: SubjectWrapper<MidiStatus> = SubjectWrapper.createSerialized(MidiStatus.DEFAULT)

    private val midiInputDeviceListSubject: SubjectWrapper<List<MidiInputDevice>> =
        SubjectWrapper.createSerialized(emptyList())

    private val selectedMidiInputDeviceIdSubject: SubjectWrapper<Optional<MidiInputDeviceId>> =
        SubjectWrapper.createSerialized(Optional.empty())

    private val volumeSubjectMap: EnumMap<MidiControllerSlot, SubjectWrapper<Double>> =
        EnumMap<MidiControllerSlot, SubjectWrapper<Double>>(MidiControllerSlot::class.java).apply {
            MidiControllerSlot.values().forEach {
                put(it, SubjectWrapper.createSerialized(0.0))
            }
        }

    val midiStatus: Observable<MidiStatus> by midiStatusSubject

    val midiInputDeviceList: Observable<List<MidiInputDevice>> by midiInputDeviceListSubject

    val selectedMidiInputDeviceId: Observable<Optional<MidiInputDeviceId>> by selectedMidiInputDeviceIdSubject

    val selectedMidiInputDevice: Observable<Optional<MidiInputDevice>> = Observables
        .combineLatest(midiInputDeviceList, selectedMidiInputDeviceId) { deviceList, deviceId ->
            deviceList.firstOrNull { it.id == deviceId.orElse(null) }.asOptional()
        }
        .replay(1)
        .refCount()

    fun getVolume(slot: MidiControllerSlot): Observable<Double> {
        val observable by checkNotNull(volumeSubjectMap[slot])
        return observable
    }

    fun updateMidiStatus(midiStatus: MidiStatus) {
        logger.debug("Midi status changed: $midiStatus")
        midiStatusSubject.onNext(midiStatus)
    }

    fun updateAvailableMidiInputDeviceList(availableMidiInputDeviceList: List<MidiInputDevice>) =
        midiInputDeviceListSubject.onNext(availableMidiInputDeviceList)

    fun updateSelectedMidiInputDeviceId(midiInputDeviceId: MidiInputDeviceId?) {
        logger.debug("Input midi device selected: $midiInputDeviceId")
        selectedMidiInputDeviceIdSubject.onNext(midiInputDeviceId.asOptional())
    }

    fun updateVolume(slot: MidiControllerSlot, volume: Double) {
        volumeSubjectMap[slot]?.onNext(volume)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(MidiDataManager::class.java)
    }
}
