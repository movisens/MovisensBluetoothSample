package com.movisens.rxblemovisenssample.bluetooth.binder

import android.os.IBinder
import io.reactivex.Observable

/**
 * Created by Robert Zetzsche on 27.05.2019.
 */
interface BluetoothBinderInterface : IBinder {
    fun getMovementAccObservable(): Observable<Double>

    fun stopSensor()
}