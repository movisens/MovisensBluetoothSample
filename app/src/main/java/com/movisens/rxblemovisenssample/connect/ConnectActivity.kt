package com.movisens.rxblemovisenssample.connect

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.movisens.rxblemovisenssample.R.layout
import com.movisens.rxblemovisenssample.ui.GenericDialogFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_connect.*

/**
 * Created by Robert Zetzsche on 22.05.2019.
 */
class ConnectActivity : AppCompatActivity() {

    private lateinit var connectViewModel: ConnectViewModel
    private lateinit var checkStateDisposable: Disposable
    private lateinit var movementAccelerationDisposable: Disposable
    private lateinit var deleteDisposable: Disposable
    private lateinit var stopAndDeleteDisposable: Disposable

    private lateinit var mac: String
    private lateinit var name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_connect)

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


        activate_mov_acc.setOnClickListener {
            if (::movementAccelerationDisposable.isInitialized && !movementAccelerationDisposable.isDisposed) {
                movementAccelerationDisposable.dispose()
            } else {
                movementAccelerationDisposable = connectViewModel
                    .startMeasurementAndActivateMovementAcceleration(mac)
                    .subscribe(::showMovementValues, ::showError)
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
    }

    override fun onPause() {
        super.onPause()
    }
}