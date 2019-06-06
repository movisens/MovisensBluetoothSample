package com.movisens.rxblemovisenssample.bluetooth.binder

import android.os.Binder
import android.os.IBinder
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

/**
 * Created by Robert Zetzsche on 06.06.2019.
 */
class BluetoothBinder : Binder(), IBluetoothBinder {

    private val movementObservable: BehaviorSubject<Double> = BehaviorSubject.create()
    private val errorObservable: PublishSubject<Throwable> = PublishSubject.create()
    private val sensorIsStoppedObservable: PublishSubject<Boolean> = PublishSubject.create()

    override fun sensorIsStoppedObservable(): Observable<Boolean> {
        return sensorIsStoppedObservable
    }

    override fun getMovementObservable(): Observable<Double> {
        return movementObservable
    }

    override fun getErrorObservable(): Observable<Throwable> {
        return errorObservable
    }

    fun pushMovementValue(movementAcceleration: Double) {
        movementObservable.onNext(movementAcceleration)
    }

    fun pushSensorWasStopped(isStopped: Boolean) {
        sensorIsStoppedObservable.onNext(isStopped)
    }

    fun pushException(exception: Throwable) {
        errorObservable.onNext(exception)
    }
}

interface IBluetoothBinder : IBinder {
    fun getMovementObservable(): Observable<Double>

    fun getErrorObservable(): Observable<Throwable>

    fun sensorIsStoppedObservable(): Observable<Boolean>
}