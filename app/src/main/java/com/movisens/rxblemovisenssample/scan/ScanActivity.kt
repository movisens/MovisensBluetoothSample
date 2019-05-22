package com.movisens.rxblemovisenssample.scan

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.BLUETOOTH
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.movisens.rxblemovisenssample.R
import com.movisens.rxblemovisenssample.model.MovisensDevice
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_scan.*

class ScanActivity : AppCompatActivity() {
    lateinit var scanViewModel: ScanViewModel
    lateinit var scanDisposable: Disposable
    lateinit var adapter: ScanRecyclerViewAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scanViewModel = ViewModelProviders.of(this).get(ScanViewModel::class.java)
        setContentView(R.layout.activity_scan)
        adapter = ScanRecyclerViewAdapter {
            Log.e("TEST", it.toString())
        }
        devices_recyclerview.layoutManager = LinearLayoutManager(this)
        devices_recyclerview.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        Dexter.withActivity(this)
            .withPermissions(listOf(ACCESS_COARSE_LOCATION, BLUETOOTH))
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        scanDisposable = scanViewModel.getMovisensDevices()
                            .subscribe(this@ScanActivity::showDevice, this@ScanActivity::showError)
                    } else {
                        // handle denied permissions with DialogOnAnyDeniedMultiplePermissionsListener
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>,
                    token: PermissionToken
                ) {
                    // handle rationale correctly like described here https://developer.android.com/training/permissions/requesting#explain
                    token.continuePermissionRequest()
                }
            })

    }

    private fun showError(throwable: Throwable) {
        throwable.printStackTrace()
        Snackbar.make(activity_scan_root, throwable.localizedMessage, Snackbar.LENGTH_LONG).show()
    }

    private fun showDevice(device: MovisensDevice) {
        adapter.addDevice(device)
    }

    override fun onPause() {
        super.onPause()
        scanDisposable.dispose()
    }
}