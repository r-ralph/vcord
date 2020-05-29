package ms.ralph.vcord.integration.midi

import io.reactivex.rxjava3.schedulers.Schedulers
import ms.ralph.vcord.core.manager.MidiDataManager
import ms.ralph.vcord.core.model.MidiInputDevice
import ms.ralph.vcord.core.model.MidiInputDeviceId
import ms.ralph.vcord.core.model.MidiStatus
import ms.ralph.vcord.integration.midi.profiles.MidiDeviceProfiles
import ms.ralph.vcord.util.FixedNameSingleThreadFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem
import kotlin.concurrent.withLock

class VcordMidiClient(private val midiDataManager: MidiDataManager) {

    private val executor: Executor = Executors.newSingleThreadExecutor(FixedNameSingleThreadFactory("midi-client"))

    private val currentDeviceInfoLock: Lock = ReentrantLock()
    private var currentDeviceInfo: DeviceInfo? = null

    fun init() {
        val currentAudioOutputDeviceList = MidiSystem.getMidiDeviceInfo()
            .filter { MidiSystemKt.isTransmitterAvailable(it) }
            .map { MidiInputDevice(MidiInputDeviceId(it.name), it) }

        midiDataManager.updateAvailableMidiInputDeviceList(currentAudioOutputDeviceList)
        midiDataManager.updateMidiStatus(MidiStatus.NO_DEVICE)

        midiDataManager.selectedMidiInputDevice.observeOn(Schedulers.from(executor)).subscribe {
            updateInputDevice(it.orElse(null))
        }
    }

    fun shutdown() = updateInputDevice(null)

    private fun updateInputDevice(midiInputDevice: MidiInputDevice?) {
        currentDeviceInfoLock.withLock {
            val currentDeviceInfoSnapshot = currentDeviceInfo
            if (currentDeviceInfoSnapshot?.midiInputDeviceId == midiInputDevice?.id) {
                if (midiInputDevice?.id == null) {
                    midiDataManager.updateMidiStatus(MidiStatus.NO_DEVICE)
                }
                return
            }
            logger.debug("Midi device changed: ${currentDeviceInfoSnapshot?.midiInputDeviceId} -> ${midiInputDevice?.id}")

            if (currentDeviceInfoSnapshot != null) {
                currentDeviceInfoSnapshot.device.close()
                logger.debug("Previous device closed")
                currentDeviceInfo = null
                midiDataManager.updateMidiStatus(MidiStatus.NO_DEVICE)
            }

            if (midiInputDevice == null) {
                return
            }

            val profile = MidiDeviceProfiles.getProfile(midiInputDevice.id)
            if (profile == null) {
                logger.error("Missing device profile for `${midiInputDevice.id.value}`.")
                midiDataManager.updateMidiStatus(MidiStatus.ERROR_NO_PROFILE)
                return
            }

            val device = MidiSystem.getMidiDevice(midiInputDevice.mixerInfo)
            device.transmitter.receiver = DeviceAdaptiveMidiMessageReceiver(midiDataManager, profile)
            currentDeviceInfo = DeviceInfo(
                midiInputDevice.id,
                device
            )
            device.open()
            midiDataManager.updateMidiStatus(MidiStatus.CONNECTED)
        }
    }

    private class DeviceInfo(
        val midiInputDeviceId: MidiInputDeviceId,
        val device: MidiDevice
    )

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(VcordMidiClient::class.java)
    }
}
