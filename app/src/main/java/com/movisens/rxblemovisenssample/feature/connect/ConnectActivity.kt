package com.movisens.rxblemovisenssample.feature.connect

import android.app.Service
import android.content.*
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.IBinder
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.movisens.rxblemovisenssample.R.layout
import com.movisens.rxblemovisenssample.bluetooth.BluetoothService
import com.movisens.rxblemovisenssample.bluetooth.BluetoothService.Companion.COMMAND
import com.movisens.rxblemovisenssample.bluetooth.BluetoothService.Companion.COMMAND_START
import com.movisens.rxblemovisenssample.bluetooth.BluetoothService.Companion.SENSOR_MAC
import com.movisens.rxblemovisenssample.bluetooth.binder.IBluetoothBinder
import com.movisens.rxblemovisenssample.ui.GenericDialogFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_connect.*

/**
 * Created by Robert Zetzsche on 22.05.2019.
 */
class ConnectActivity : AppCompatActivity(), ServiceConnection {
    private lateinit var errorDisposable: Disposable
    private lateinit var bluetoothBinder: IBluetoothBinder
    private lateinit var connectViewModel: ConnectViewModel
    private lateinit var checkStateDisposable: Disposable
    private lateinit var movementAccelerationDisposable: Disposable
    private lateinit var deleteDisposable: Disposable
    private lateinit var stopAndDeleteDisposable: Disposable

    private lateinit var mac: String
    private lateinit var name: String

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_connect)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        mac = intent?.extras?.getString("MAC") ?: ""
        name = intent?.extras?.getString("NAME") ?: ""

        connectViewModel = ViewModelProviders.of(this).get(ConnectViewModel::class.java)
        check_sensor_state.setOnClickListener {
            showWaitDialog()
            checkStateDisposable = connectViewModel.getMovisensSensorState(mac)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    checkStateDisposable.dispose()
                    dismissWaitDialog()
                    if (!it.dataAvailable && !it.measurementEnabled) {
                        activate_mov_acc.isEnabled = true
                    } else {
                        if (it.measurementEnabled && it.dataAvailable) {
                            showStopMeasurementAndDeleteDataDialog()
                        } else if (it.dataAvailable) {
                            showDeleteDataDialog()
                        }
                    }
                }, ::showError)
        }

        val samplingRunning = sharedPreferences.getBoolean("SAMPLING_RUNNING", false)
        activate_mov_acc.text = if (samplingRunning) "Stop Measurement" else "Activate Movement Acceleration"
        activate_mov_acc.setOnClickListener {
            if (!sharedPreferences.getBoolean("SAMPLING_RUNNING", false)) {
                val intent = Intent(this, BluetoothService::class.java)
                intent.putExtra(COMMAND, COMMAND_START)
                intent.putExtra(SENSOR_MAC, mac)
                if (SDK_INT > Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }
                bindService(intent, this, Service.BIND_ABOVE_CLIENT)
                sharedPreferences.edit {
                    putBoolean("SAMPLING_RUNNING", true)
                }
                activate_mov_acc.text = "Stop Measurement"
            } else {

                //   bluetoothBinder.stopSensor()
                activate_mov_acc.isEnabled = false
                activate_mov_acc.text = "Activate Movement Acceleration"
                sharedPreferences.edit {
                    putBoolean("SAMPLING_RUNNING", false)
                }
            }
        }
    }

    private fun dismissWaitDialog() {
        val indefiniteDialog = supportFragmentManager.findFragmentByTag("waitDialog") as? GenericDialogFragment
        indefiniteDialog?.dismiss()
    }

    private fun showWaitDialog() {
        val indefiniteDialog = GenericDialogFragment()
        indefiniteDialog.genericDialog = AlertDialog.Builder(this).setMessage("Please Wait").create()
        indefiniteDialog.show(supportFragmentManager, "waitDialog")
    }

    private fun showDeleteDataDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Delete data")
            .setMessage("There is currently a measurement on the sensor! Please delete the data!")
            .setPositiveButton("ok") { dialogInterface, _ ->
                showWaitDialog()
                deleteDisposable = connectViewModel.deleteData(mac)
                    .subscribe({
                        if (it) {
                            activate_mov_acc.isEnabled = true
                            deleteDisposable.dispose()
                            dismissWaitDialog()
                        }
                    }, ::showError)
            }.setNegativeButton("cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }.create()

        val deleteFragment = GenericDialogFragment()
        deleteFragment.genericDialog = dialog
        deleteFragment.show(supportFragmentManager, "stopDialog")
    }

    private fun showStopMeasurementAndDeleteDataDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Stop measurement and delete data")
            .setMessage("There is currently a measurement running! Please stop this measurement and delete the data!")
            .setPositiveButton("ok") { dialogInterface: DialogInterface?, i: Int ->
                showWaitDialog()
                stopAndDeleteDisposable = connectViewModel.stopMeasurementAndDeleteData(mac)
                    .subscribe({
                        if (it) {
                            activate_mov_acc.isEnabled = true
                            stopAndDeleteDisposable.dispose()
                            dismissWaitDialog()
                        }
                    }, ::showError)
            }.setNegativeButton("cancel") { dialogInterface, i ->
                dialogInterface.dismiss()
            }.create()

        val stopAndDeleteDialogFragment = GenericDialogFragment()
        stopAndDeleteDialogFragment.genericDialog = dialog
        stopAndDeleteDialogFragment.show(supportFragmentManager, "stopAndDeleteDialog")
    }

    private fun showMovementValues(movementAcceleration: Double) {
        Toast.makeText(this, "Movement $movementAcceleration", Toast.LENGTH_LONG).show()
        // write data to UI
    }

    private fun showError(throwable: Throwable) {
        throwable.printStackTrace()
        dismissWaitDialog()
        checkStateDisposable.dispose()
        Snackbar.make(activity_connect_root, throwable.localizedMessage, Snackbar.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        if (sharedPreferences.getBoolean("SAMPLING_RUNNING", false)) {
            bindService(Intent(this, BluetoothService::class.java), this, Service.BIND_ADJUST_WITH_ACTIVITY)
        }
    }

    override fun onPause() {
        super.onPause()
        unbindService(this)
    }

    override fun onServiceDisconnected(componentName: ComponentName) {
        if (::movementAccelerationDisposable.isInitialized)
            movementAccelerationDisposable.dispose()
        if (::errorDisposable.isInitialized)
            errorDisposable.dispose()
    }

    override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
        this.bluetoothBinder = binder as IBluetoothBinder
        movementAccelerationDisposable =
            bluetoothBinder.getMovementObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showMovementValues)
        errorDisposable = bluetoothBinder.getErrorObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::showError)
    }
}