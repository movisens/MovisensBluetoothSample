package com.movisens.rxblemovisenssample.bluetooth

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Created by Robert Zetzsche on 27.05.2019.
 */

class BluetoothService : Service() {

    private lateinit var bluetoothBinder: BluetoothBinderInterface

    override fun onBind(p0: Intent?): IBinder {
        return bluetoothBinder
    }

    override fun onCreate() {
        super.onCreate()
        bluetoothBinder = BluetoothBinder()
    }
}