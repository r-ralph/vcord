package ms.ralph.vcord.util

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicBoolean

class FixedNameSingleThreadFactory(private val name: String) : ThreadFactory {

    private var isThreadProvided: AtomicBoolean = AtomicBoolean(false)

    override fun newThread(r: Runnable): Thread {
        if (isThreadProvided.getAndSet(true)) {
            throw IllegalStateException("This thread factory has already provided a thread.")
        }
        val thread = Thread(r, name)
        if (thread.isDaemon) {
            thread.isDaemon = false
        }
        if (thread.priority != Thread.NORM_PRIORITY) {
            thread.priority = Thread.NORM_PRIORITY
        }
        return thread
    }
}
