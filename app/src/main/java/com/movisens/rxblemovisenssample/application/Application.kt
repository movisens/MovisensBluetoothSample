package com.movisens.rxblemovisenssample.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.preference.PreferenceManager
import androidx.core.content.edit
import com.movisens.rxblemovisenssample.bluetooth.BluetoothService
import com.movisens.rxblemovisenssample.bluetooth.BluetoothService.Companion.COMMAND
import com.movisens.rxblemovisenssample.bluetooth.BluetoothService.Companion.COMMAND_START
import com.movisens.rxblemovisenssample.bluetooth.BluetoothService.Companion.SENSOR_MAC
import com.movisens.rxblemovisenssample.feature.connect.ConnectViewModel
import com.movisens.rxblemovisenssample.feature.scan.ScanViewModel
import com.movisens.rxblemovisenssample.model.MovisensDevicesRepository
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.exceptions.BleException
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

/**
 * Created by Robert Zetzsche on 27.05.2019.
 */
class Application : Application() {

    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // ignore BleExceptions as they were surely delivered at least once
        // Please read https://github.com/Polidea/RxAndroidBle/wiki/FAQ:-UndeliverableException
        RxJavaPlugins.setErrorHandler { error ->
            if (error is UndeliverableException && error.cause is BleException) {
                return@setErrorHandler
            } else {
                throw Exception(error)
            }
        }
        val mac = sharedPreferences.getString(SENSOR_MAC, "") ?: ""
        if (isSamplingRunning()) {
            if (mac.isNotEmpty()) {
                val intent = Intent(this, BluetoothService::class.java)
                intent.putExtra(SENSOR_MAC, mac)
                intent.putExtra(COMMAND, COMMAND_START)

                if (SDK_INT > O) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }
            } else {
                sharedPreferences.edit {
                    putBoolean("SAMPLING_RUNNING", false)
                }
            }
        }

        createNotificationChannel()

        startKoin {
            androidLogger()
            androidContext(this@Application)
            modules(listOf(scanDevicesModule))
        }
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


    private val scanDevicesModule = module {
        // single instance of HelloRepository
        single { MovisensDevicesRepository(RxBleClient.create(this@Application)) }
        // MyViewModel ViewModel
        viewModel { ScanViewModel(get()) }
        viewModel { ConnectViewModel(get()) }
    }

    override fun onTerminate() {
        super.onTerminate()
        stopKoin()
    }
}


