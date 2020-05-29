package ms.ralph.vcord.integration.discord

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import ms.ralph.vcord.core.manager.AudioDataManager
import ms.ralph.vcord.core.manager.TeamDataManager
import ms.ralph.vcord.core.model.PlayerSlot
import ms.ralph.vcord.core.model.UserId
import ms.ralph.vcord.integration.audio.VcordAudioClient
import ms.ralph.vcord.util.FixedNameSingleThreadFactory
import net.dv8tion.jda.api.audio.UserAudio
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class AudioProcessor(
    private val audioClient: VcordAudioClient,
    private val slot: PlayerSlot,
    private val teamManager: TeamDataManager,
    private val audioDataManager: AudioDataManager,
    threadNameSuffix: String
) {
    private val audioProcessorScheduler: Scheduler = Schedulers.from(
        Executors.newSingleThreadExecutor(FixedNameSingleThreadFactory("discord-client-audio-processor-$threadNameSuffix"))
    )

    private val audioData: Subject<UserAudio> = PublishSubject.create()

    private val backingUserId: AtomicReference<UserId> = AtomicReference()

    private val speakingStatusSubject: Subject<Boolean> = BehaviorSubject.createDefault(false)

    private var userId: UserId?
        get() = backingUserId.get()
        set(value) = backingUserId.set(value)

    @Volatile
    private var volume: Double = 1.0

    fun start() {
        audioData.observeOn(audioProcessorScheduler).subscribe {
            processInternal(it)
        }
        teamManager.getSelectedUserId(slot).subscribe {
            userId = it.orElse(null)
        }
        audioDataManager.getVolume(slot).subscribe {
            volume = it
        }
        speakingStatusSubject.throttleLatest(100, TimeUnit.MILLISECONDS)
            .subscribe { teamManager.updateSpeakingState(slot, it) }
    }

    fun handle(userAudio: UserAudio) {
        if (userAudio.user.id == userId?.value) {
            audioData.onNext(userAudio)
        }
    }

    private fun processInternal(userAudio: UserAudio) {
        val audioData = userAudio.getAudioData(volume)
        audioClient.write(slot, audioData)
        val hasNonSilentData = audioData.any { it != 0.toByte() }
        speakingStatusSubject.onNext(hasNonSilentData)
    }
}
