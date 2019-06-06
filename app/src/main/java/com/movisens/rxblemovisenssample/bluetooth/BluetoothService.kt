package com.movisens.rxblemovisenssample.bluetooth

import android.app.*
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.movisens.rxblemovisenssample.R
import com.movisens.rxblemovisenssample.bluetooth.binder.BluetoothBinder
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
    private lateinit var bluetoothServiceController: BluetoothServiceController
    private lateinit var movementAccelerationDisposable: Disposable
    private lateinit var alarmManager: AlarmManager

    private val subject: Subject<Boolean> = PublishSubject.create()
    private val bluetoothBinder = BluetoothBinder()

    companion object {
        const val COMMAND = "COMMAND"
        const val COMMAND_RECONNECT = "COMMAND_RECONNECT"
        const val COMMAND_START = "COMMAND_START"
        const val COMMAND_STOP = "COMMAND_STOP"

        const val SENSOR_MAC = "SENSOR_MAC"
    }

    override fun onBind(p0: Intent?): IBinder? {
        return bluetoothBinder
    }

    override fun onCreate() {
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.hasExtra(SENSOR_MAC) && intent.hasExtra(COMMAND)) {
            val command: String = intent.getStringExtra(COMMAND)
            val mac: String = intent.getStringExtra(SENSOR_MAC)
            when (command) {
                COMMAND_START -> {
                    if (intent.hasExtra(SENSOR_MAC)) {
                        showForegroundNotification()
                        bluetoothServiceController =
                            BluetoothServiceController(MovisensDevicesRepository(RxBleClient.create(this)), mac)
                        movementAccelerationDisposable = bluetoothServiceController.getMovementAccObservable(subject)
                            .subscribe(::handleUpdates, { handleErrors(it, mac) })
                        errorDisposable = bluetoothServiceController.errorSubject.subscribe { handleErrors(it, mac) }
                    }
                }
                COMMAND_STOP -> {
                    bluetoothServiceController.stopSensor().subscribe()
                }
                COMMAND_RECONNECT -> {
                    subject.onNext(true)
                }
            }
        } else
            stopSelf()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun handleUpdates(movementAccelerationBuffered: Double) {
        bluetoothBinder.pushMovementValue(movementAccelerationBuffered)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification = getNotification("Movement Acceleration: $movementAccelerationBuffered g")
        notificationManager.notify(123, notification)
    }

    private fun showForegroundNotification() {
        startForeground(123, getNotification("Currently no data available"))
    }

    private fun getNotification(title: String): Notification {
        val pendingIntent = getActivity(
            this, 0,
            Intent(this, ConnectActivity::class.java), FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(applicationContext, "test")
            .setContentTitle("Sensor Connection Running")
            .setContentText(title)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
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
            movementAccelerationDisposable.dispose()
            errorDisposable.dispose()
            bluetoothBinder.pushException(throwable)
        }
    }
}