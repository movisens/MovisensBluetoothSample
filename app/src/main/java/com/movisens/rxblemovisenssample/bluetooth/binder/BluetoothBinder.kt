package com.movisens.rxblemovisenssample.bluetooth.binder

import android.os.Binder
import android.os.IBinder
import io.reactivex.Observable

/**
 * Created by Robert Zetzsche on 27.05.2019.
 */
class BluetoothBinder(val bluetoothService: BluetoothContract.Controller) : Binder(), IBluetoothBinder {
    override fun getMovementAccObservable(): Observable<Double> {
        return bluetoothService.getMovementAccObservable()
    }

    override fun stopSensor(): Observable<Boolean> {
        return bluetoothService.stopSensor()
    }
}

interface IBluetoothBinder : IBinder, BluetoothContract.Controller