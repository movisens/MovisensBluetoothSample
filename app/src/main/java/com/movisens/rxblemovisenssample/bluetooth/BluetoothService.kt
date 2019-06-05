package com.movisens.rxblemovisenssample.bluetooth

import android.app.*
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.movisens.rxblemovisenssample.bluetooth.binder.BluetoothBinder
import com.movisens.rxblemovisenssample.bluetooth.binder.IBluetoothBinder
import com.movisens.rxblemovisenssample.exceptions.ReconnectException
import com.movisens.rxblemovisenssample.exceptions.UnrecoverableException
import com.movisens.rxblemovisenssample.feature.connect.ConnectActivity
import com.movisens.rxblemovisenssample.model.MovisensDevicesRepository
import com.polidea.rxandroidble2.RxBleClient
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit

/**
 * Created by Robert Zetzsche on 27.05.2019.
 */

class BluetoothService : Service() {
    private var reconnectPendingIntent: PendingIntent? = null
    private lateinit var errorDisposable: Disposable
    private lateinit var IBluetoothBinder: IBluetoothBinder
    private lateinit var bluetoothController: BluetoothServiceController
    private lateinit var movementAccelerationDisposable: Disposable
    private val subject: Subject<Boolean> = PublishSubject.create()
    private val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val COMMAND = "COMMAND"
        const val COMMAND_RECONNECT = "COMMAND_RECONNECT"
        const val SENSOR_MAC = "SENSOR_MAC"
    }

    override fun onBind(p0: Intent?): IBinder {
        return IBluetoothBinder
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.hasExtra(SENSOR_MAC) && intent.hasExtra(COMMAND)) {
            val command: String = intent.getStringExtra(COMMAND)
            val mac: String = intent.getStringExtra(SENSOR_MAC)
            when (command) {
                "START_COMMAND" -> {
                    if (intent.hasExtra(SENSOR_MAC)) {
                        showForegroundNotification()
                        bluetoothController =
                            BluetoothServiceController(MovisensDevicesRepository(RxBleClient.create(this)), mac)
                        IBluetoothBinder = BluetoothBinder(bluetoothController)
                        movementAccelerationDisposable = bluetoothController.getMovementAccObservable(subject)
                            .subscribe()
                        errorDisposable = bluetoothController.errorSubject.subscribe { handleErrors(it, mac) }
                    }
                }
                "STOP_COMMAND" -> {
                    bluetoothController.stopSensor().subscribe()
                }
                "RECONNECT" -> {
                    subject.onNext(true)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun showForegroundNotification() {
        val notification: Notification? = null
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        startForeground(123, notification)
    }

    private fun handleErrors(throwable: Throwable, mac: String) {
        if (throwable is ReconnectException) {
            val serviceIntent = Intent(this, BluetoothService::class.java)
            serviceIntent.putExtra(COMMAND, COMMAND_RECONNECT)
            serviceIntent.putExtra(SENSOR_MAC, mac)
            reconnectPendingIntent = getService(this, 45698, serviceIntent, FLAG_UPDATE_CURRENT)
            val triggerAtMillis = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5)
            val intent = Intent(this, ConnectActivity::class.java)
            val pi = getActivity(this, System.currentTimeMillis().toInt(), intent, FLAG_UPDATE_CURRENT)
            val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerAtMillis, pi)
            alarmManager.setAlarmClock(alarmClockInfo, reconnectPendingIntent)
        } else if (throwable is UnrecoverableException) {
            // Do something
        }
    }
}