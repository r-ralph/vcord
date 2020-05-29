package ms.ralph.vcord.util

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.Subject
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class SubjectWrapper<T> private constructor(
    private val backingSubject: Subject<T>
) : ReadOnlyProperty<Any?, Observable<T>> {

    private val observable: Observable<T> =
        backingSubject.distinctUntilChanged().replay(1).refCount()

    fun onNext(item: T) = backingSubject.onNext(item)

    override fun getValue(thisRef: Any?, property: KProperty<*>): Observable<T> = observable

    companion object {

        fun <T> create(): SubjectWrapper<T> = SubjectWrapper(BehaviorSubject.create())

        fun <T> create(initialValue: T): SubjectWrapper<T> = SubjectWrapper(BehaviorSubject.createDefault(initialValue))

        fun <T> createSerialized(): SubjectWrapper<T> = SubjectWrapper(BehaviorSubject.create<T>().toSerialized())
        fun <T> createSerialized(initialValue: T): SubjectWrapper<T> =
            SubjectWrapper(BehaviorSubject.createDefault(initialValue).toSerialized())

    }
}
