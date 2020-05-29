package ms.ralph.vcord.integration.audio

import io.reactivex.rxjava3.schedulers.Schedulers
import ms.ralph.vcord.core.manager.AudioDataManager
import ms.ralph.vcord.core.model.AudioOutputDevice
import ms.ralph.vcord.core.model.AudioOutputDeviceId
import ms.ralph.vcord.core.model.AudioStatus
import ms.ralph.vcord.core.model.PlayerSlot
import ms.ralph.vcord.util.FixedNameSingleThreadFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.Mixer
import javax.sound.sampled.SourceDataLine
import kotlin.concurrent.withLock

class VcordAudioClient(
    private val audioDataManager: AudioDataManager
) {
    private val executor: Executor =
        Executors.newSingleThreadExecutor(FixedNameSingleThreadFactory("audio-client-${audioDataManager.threadNameSuffix}"))

    private val currentOutputInfoLock: Lock = ReentrantLock()
    private var currentOutputInfo: OutputInfo? = null

    fun init() {
        val currentAudioOutputDeviceList = AudioSystem.getMixerInfo()
            .filter { AudioSystem.getMixer(it).isLineSupported(outputDataLineInfo) }
            .map { AudioOutputDevice(AudioOutputDeviceId(it.name), it) }

        audioDataManager.updateAvailableAudioOutputDeviceList(currentAudioOutputDeviceList)
        audioDataManager.updateAudioStatus(AudioStatus.NO_LINE)

        audioDataManager.selectedAudioOutputDevice.observeOn(Schedulers.from(executor)).subscribe {
            updateOutputDevice(it.orElse(null))
        }
    }

    private fun updateOutputDevice(audioOutputDevice: AudioOutputDevice?) {
        currentOutputInfoLock.withLock {
            val currentOutputInfoSnapshot = currentOutputInfo
            if (currentOutputInfoSnapshot?.audioOutputDevice?.id == audioOutputDevice?.id) {
                return
            }
            logger.debug("Audio device changed: ${currentOutputInfoSnapshot?.audioOutputDevice?.id} -> ${audioOutputDevice?.id}")

            if (currentOutputInfoSnapshot != null) {
                currentOutputInfoSnapshot.slot1AudioOutputLine.requestStop()
                currentOutputInfoSnapshot.slot2AudioOutputLine.requestStop()
                currentOutputInfoSnapshot.slot3AudioOutputLine.requestStop()
                currentOutputInfoSnapshot.slot4AudioOutputLine.requestStop()

                currentOutputInfoSnapshot.outputLineClosingLatch.await()
                audioDataManager.updateAudioStatus(AudioStatus.NO_LINE)
                logger.debug("All line closed")

                currentOutputInfo = null
            }

            if (audioOutputDevice == null) {
                return
            }

            val outputLineClosingLatch = CountDownLatch(4)
            val slot1AudioOutputLine = createAudioOutputLine(1, audioOutputDevice.mixerInfo, outputLineClosingLatch)
            val slot2AudioOutputLine = createAudioOutputLine(2, audioOutputDevice.mixerInfo, outputLineClosingLatch)
            val slot3AudioOutputLine = createAudioOutputLine(3, audioOutputDevice.mixerInfo, outputLineClosingLatch)
            val slot4AudioOutputLine = createAudioOutputLine(4, audioOutputDevice.mixerInfo, outputLineClosingLatch)

            currentOutputInfo = OutputInfo(
                audioOutputDevice,
                outputLineClosingLatch,
                slot1AudioOutputLine,
                slot2AudioOutputLine,
                slot3AudioOutputLine,
                slot4AudioOutputLine
            )

            slot1AudioOutputLine.start()
            slot2AudioOutputLine.start()
            slot3AudioOutputLine.start()
            slot4AudioOutputLine.start()
            audioDataManager.updateAudioStatus(AudioStatus.LINKED)
        }
    }

    fun write(slot: PlayerSlot, byteArray: ByteArray) {
        val audioOutputLine = maybeGetCurrentAudioOutputLine(slot) ?: return
        audioOutputLine.write(byteArray)
    }

    fun shutdown() = updateOutputDevice(null)

    private fun maybeGetCurrentAudioOutputLine(slot: PlayerSlot): SingleAudioOutputLine? {
        currentOutputInfoLock.withLock {
            return when (slot) {
                PlayerSlot.SLOT_1 -> currentOutputInfo?.slot1AudioOutputLine
                PlayerSlot.SLOT_2 -> currentOutputInfo?.slot2AudioOutputLine
                PlayerSlot.SLOT_3 -> currentOutputInfo?.slot3AudioOutputLine
                PlayerSlot.SLOT_4 -> currentOutputInfo?.slot4AudioOutputLine
            }
        }
    }

    private fun createAudioOutputLine(
        index: Int,
        mixerInfo: Mixer.Info,
        outputLineClosingLatch: CountDownLatch
    ): SingleAudioOutputLine = SingleAudioOutputLine(
        mixerInfo,
        format,
        outputLineClosingLatch,
        "${audioDataManager.threadNameSuffix}-$index"
    )

    private class OutputInfo(
        val audioOutputDevice: AudioOutputDevice,
        val outputLineClosingLatch: CountDownLatch,
        val slot1AudioOutputLine: SingleAudioOutputLine,
        val slot2AudioOutputLine: SingleAudioOutputLine,
        val slot3AudioOutputLine: SingleAudioOutputLine,
        val slot4AudioOutputLine: SingleAudioOutputLine
    )

    companion object {
        // JDA outputs audio with '48KHz 16bit stereo signed BigEndian PCM'
        // [net.dv8tion.jda.api.audio.AudioReceiveHandler.OUTPUT_FORMAT]
        private val format: AudioFormat = AudioFormat(48000.0f, 16, 2, true, true)

        private val outputDataLineInfo: DataLine.Info = DataLine.Info(SourceDataLine::class.java, format)

        private val logger: Logger = LoggerFactory.getLogger(VcordAudioClient::class.java)
    }
}
