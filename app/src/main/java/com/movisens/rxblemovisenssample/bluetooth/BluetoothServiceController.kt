package com.movisens.rxblemovisenssample.bluetooth

import com.movisens.rxblemovisenssample.exceptions.ReconnectException
import com.movisens.rxblemovisenssample.exceptions.UnrecoverableException
import com.movisens.rxblemovisenssample.model.MovisensDevicesRepository
import com.polidea.rxandroidble2.exceptions.BleDisconnectedException
import com.polidea.rxandroidble2.exceptions.BleGattException
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

/**
 * Created by Robert Zetzsche on 27.05.2019.
 */
class BluetoothServiceController(
    private val movisensDevicesRepository: MovisensDevicesRepository,
    val mac: String
) {
    val errorSubject: Subject<Throwable> = PublishSubject.create()

    fun getMovementAccObservable(observable: Observable<*>): Observable<Double> {
        return movisensDevicesRepository.activateMovementAccelerationIfPossible(mac)
            .map { it.movementAcceleration }
            .retryWhen {
                it.flatMap { throwable: Throwable ->
                    if (throwable is BleDisconnectedException || throwable is BleGattException) {
                        errorSubject.onNext(ReconnectException())
                        return@flatMap observable
                    } else {
                        errorSubject.onNext(UnrecoverableException())
                        return@flatMap observable
                    }
                }
            }
    }

    fun stopSensor(): Observable<Boolean> {
        return Observable.just(false)
    }

}