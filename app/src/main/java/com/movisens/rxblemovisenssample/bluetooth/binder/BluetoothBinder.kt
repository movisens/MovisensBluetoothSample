package com.movisens.rxblemovisenssample.bluetooth.binder

import android.os.Binder
import android.os.IBinder
import com.movisens.rxblemovisenssample.exceptions.MovisensExceptions
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

/**
 * Created by Robert Zetzsche on 06.06.2019.
 */
class BluetoothBinder : Binder(), IBluetoothBinder {
    private val movementObservable: BehaviorSubject<Double> = BehaviorSubject.create()
    private val errorObservable: PublishSubject<MovisensExceptions> = PublishSubject.create()

    override fun getMovementObservable(): Observable<Double> {
        return movementObservable
    }

    override fun getErrorObservable(): Observable<MovisensExceptions> {
        return errorObservable
    }

    fun pushMovementValue(movementAcceleration: Double) {
        movementObservable.onNext(movementAcceleration)
    }

    fun pushException(exception: MovisensExceptions) {
        errorObservable.onNext(exception)
    }

}

interface IBluetoothBinder : IBinder {
    fun getMovementObservable(): Observable<Double>

    fun getErrorObservable(): Observable<MovisensExceptions>
}