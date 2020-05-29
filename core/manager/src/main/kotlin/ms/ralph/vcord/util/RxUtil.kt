package ms.ralph.vcord.util

import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.Subject

internal fun <T> serializedBehaviorSubjectOf(): Subject<T> = BehaviorSubject.create<T>().toSerialized()

internal fun <T> serializedBehaviorSubjectOf(default: T): Subject<T> =
    BehaviorSubject.createDefault(default).toSerialized()
