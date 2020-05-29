package ms.ralph.vcord.gui.util

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler

internal fun <T> Observable<T>.subscribeWithMainThread(onNext: (T) -> Unit): Disposable =
    observeOn(JavaFxScheduler.platform()).subscribe(onNext)
