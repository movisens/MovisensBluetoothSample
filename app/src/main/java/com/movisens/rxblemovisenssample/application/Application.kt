package com.movisens.rxblemovisenssample.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.preference.PreferenceManager
import com.polidea.rxandroidble2.exceptions.BleException
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins

/**
 * Created by Robert Zetzsche on 27.05.2019.
 */
class Application : Application() {

    override fun onCreate() {
        super.onCreate()

        // ignore BleExceptions as they were surely delivered at least once
        // Please read https://github.com/Polidea/RxAndroidBle/wiki/FAQ:-UndeliverableException
        RxJavaPlugins.setErrorHandler { error ->
            if (error is UndeliverableException && error.cause is BleException) {
                return@setErrorHandler
            } else {
                throw Exception(error)
            }
        }

        if (isSamplingRunning()) {
            val intent = Intent(this, BluetoothService::class.java)
            if (SDK_INT > Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startActivity(intent)
            }

    private fun createNotificationChannel() {
        if (SDK_INT >= O) {
            val testChannel = NotificationChannel("test", "test", IMPORTANCE_HIGH)
            (getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager).createNotificationChannel(testChannel)
        }
    }

    fun isSamplingRunning(): Boolean {
        val preference = PreferenceManager.getDefaultSharedPreferences(this)
        return preference.getBoolean("SAMPLING_RUNNING", false)
    }
}