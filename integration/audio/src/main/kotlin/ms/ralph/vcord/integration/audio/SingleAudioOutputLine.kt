package ms.ralph.vcord.integration.audio

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Mixer

class SingleAudioOutputLine(
    mixerInfo: Mixer.Info,
    audioFormat: AudioFormat,
    closedNotifier: CountDownLatch,
    private val threadNameSuffix: String
) {
    private val queue: Queue<ByteArray> = ConcurrentLinkedQueue()

    private val writer: Writer = Writer(mixerInfo, audioFormat, queue, closedNotifier)

    fun start() {
        val thread = Thread(writer, "audio-client-output-#$threadNameSuffix")
        thread.start()
    }

    fun requestStop() {
        writer.requestClose()
    }

    fun write(byteArray: ByteArray) {
        queue.add(byteArray)
    }

    private class Writer(
        private val mixerInfo: Mixer.Info,
        private val audioFormat: AudioFormat,
        private val queue: Queue<ByteArray>,
        private val closedNotifier: CountDownLatch
    ) : Runnable {

        @Volatile
        private var isRunning: Boolean = true

        private val silentBuffer20Ms = ByteArray(audioFormat.bytesPerMillisecond * 20)

        // The minimum value of the remaining data that can obtained from SourceDataLine.
        private val emitSilentDataThresholdRemainingDataBytes = audioFormat.bytesPerMillisecond * 10

        override fun run() {
            val localLine = AudioSystem.getSourceDataLine(audioFormat, mixerInfo)
            val bufferSize = localLine.bufferSize

            localLine.open()
            localLine.start()

            logger.debug("Audio line opened for '${mixerInfo.name}'")

            var isSilentPrevOutput = false

            while (true) {
                val data: ByteArray? = queue.poll()
                if (data != null) {
                    if (isSilentPrevOutput) {
                        localLine.write(silentBuffer20Ms, 0, silentBuffer20Ms.size)
                    }
                    localLine.write(data, 0, data.size)
                    isSilentPrevOutput = false
                } else {
                    val remainingDataBytes = bufferSize - localLine.available()
                    if (remainingDataBytes <= emitSilentDataThresholdRemainingDataBytes) {
                        localLine.write(silentBuffer20Ms, 0, silentBuffer20Ms.size)
                        isSilentPrevOutput = true
                    }
                }
                Thread.sleep(1)
                if (!isRunning) {
                    break
                }
            }

            localLine.stop()
            localLine.flush()
            localLine.close()
            logger.debug("Audio line closed for '${mixerInfo.name}'")
            closedNotifier.countDown()
        }

        fun requestClose() {
            logger.debug("Requested closing audio line")
            isRunning = false
        }

        companion object {
            private val logger: Logger = LoggerFactory.getLogger(Writer::class.java)

            private val AudioFormat.bytesPerMillisecond: Int
                get() = (sampleRate * sampleSizeInBits / channels / 2000).toInt()
        }
    }
}
