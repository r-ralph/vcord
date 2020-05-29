package ms.ralph.vcord.core.manager

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.Observables
import ms.ralph.vcord.core.model.AudioOutputDevice
import ms.ralph.vcord.core.model.AudioOutputDeviceId
import ms.ralph.vcord.core.model.AudioStatus
import ms.ralph.vcord.core.model.MidiControllerSlot
import ms.ralph.vcord.core.model.PlayerSlot
import ms.ralph.vcord.util.SubjectWrapper
import ms.ralph.vcord.util.asOptional
import org.slf4j.LoggerFactory
import java.util.Optional

class AudioDataManager(
    val threadNameSuffix: String,
    private val linkedMidiControllerSlotList: List<MidiControllerSlot>
) {

    init {
        check(linkedMidiControllerSlotList.size == 4) { "`linkedMidiControllerSlotList` doesn't have 4 slots." }
    }

    private val audioStatusSubject: SubjectWrapper<AudioStatus> = SubjectWrapper.createSerialized(AudioStatus.DEFAULT)

    private val audioOutputDeviceListSubject: SubjectWrapper<List<AudioOutputDevice>> =
        SubjectWrapper.createSerialized()

    private val selectedAudioOutputDeviceIdSubject: SubjectWrapper<Optional<AudioOutputDeviceId>> =
        SubjectWrapper.createSerialized(Optional.empty())

    val audioStatus: Observable<AudioStatus> by audioStatusSubject

    val audioOutputDeviceList: Observable<List<AudioOutputDevice>> by audioOutputDeviceListSubject

    val selectedAudioOutputDeviceId: Observable<Optional<AudioOutputDeviceId>> by selectedAudioOutputDeviceIdSubject

    val selectedAudioOutputDevice: Observable<Optional<AudioOutputDevice>> = Observables
        .combineLatest(
            audioOutputDeviceList,
            selectedAudioOutputDeviceId
        ) { deviceList, deviceId -> deviceList.firstOrNull { it.id == deviceId.orElse(null) }.asOptional() }
        .distinctUntilChanged()
        .replay(1)
        .refCount()

    fun updateAudioStatus(audioStatus: AudioStatus) {
        logger.debug("Audio status changed: $audioStatus")
        audioStatusSubject.onNext(audioStatus)
    }

    fun updateAvailableAudioOutputDeviceList(availableAudioOutputDeviceList: List<AudioOutputDevice>) =
        audioOutputDeviceListSubject.onNext(availableAudioOutputDeviceList)

    fun updateSelectedAudioOutputDeviceId(audioOutputDeviceId: AudioOutputDeviceId?) {
        logger.debug("Output audio device selected: $audioOutputDeviceId")
        selectedAudioOutputDeviceIdSubject.onNext(audioOutputDeviceId.asOptional())
    }

    fun updateVolume(playerSlot: PlayerSlot, volume: Double) =
        VcordManager.midiDataManager.updateVolume(getMidiControllerSlotFromPlayerSlot(playerSlot), volume)

    fun getVolume(playerSlot: PlayerSlot): Observable<Double> =
        VcordManager.midiDataManager.getVolume(getMidiControllerSlotFromPlayerSlot(playerSlot))

    private fun getMidiControllerSlotFromPlayerSlot(playerSlot: PlayerSlot): MidiControllerSlot = when (playerSlot) {
        PlayerSlot.SLOT_1 -> linkedMidiControllerSlotList[0]
        PlayerSlot.SLOT_2 -> linkedMidiControllerSlotList[1]
        PlayerSlot.SLOT_3 -> linkedMidiControllerSlotList[2]
        PlayerSlot.SLOT_4 -> linkedMidiControllerSlotList[3]
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AudioDataManager::class.java)
    }
}
