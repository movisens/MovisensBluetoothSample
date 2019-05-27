package com.movisens.rxblemovisenssample.bluetooth.binder

import android.os.Binder
import io.reactivex.Observable

/**
 * Created by Robert Zetzsche on 27.05.2019.
 */
class BluetoothBinder : Binder(), BluetoothBinderInterface {
    override fun stopSensor() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMovementAccObservable(): Observable<Double> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}