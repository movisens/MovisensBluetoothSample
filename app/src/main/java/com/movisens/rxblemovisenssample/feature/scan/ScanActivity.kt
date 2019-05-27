package com.movisens.rxblemovisenssample.feature.scan

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.BasePermissionListener
import com.movisens.rxblemovisenssample.feature.scan.connect.ConnectActivity
import com.polidea.rxandroidble2.exceptions.BleScanException
import com.polidea.rxandroidble2.exceptions.BleScanException.BLUETOOTH_DISABLED
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_scan.*


class ScanActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_ENABLE_BT: Int = 1
    }

    lateinit var scanViewModel: ScanViewModel
    lateinit var scanDisposable: Disposable
    lateinit var adapter: ScanRecyclerViewAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scanViewModel = ViewModelProviders.of(this).get(ScanViewModel::class.java)
        setContentView(com.movisens.rxblemovisenssample.R.layout.activity_scan)
        adapter = ScanRecyclerViewAdapter {
            val intent = Intent(this, ConnectActivity::class.java)
            intent.putExtra("MAC", it.mac)
            intent.putExtra("NAME", it.name)
            startActivity(intent)
        }
        devices_recyclerview.layoutManager = LinearLayoutManager(this)
        devices_recyclerview.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        Dexter.withActivity(this)
            .withPermission(ACCESS_COARSE_LOCATION)
            .withListener(object : BasePermissionListener() {
                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    super.onPermissionRationaleShouldBeShown(permission, token)
                    // handle rationale correctly like described here https://developer.android.com/training/permissions/requesting#explain
                }

                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    super.onPermissionGranted(response)
                    scanDisposable = scanViewModel.getMovisensDevices()
                        .subscribe(this@ScanActivity::showDevice, this@ScanActivity::showError)
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    super.onPermissionDenied(response)
                    // handle denied permissions with DialogOnAnyDeniedMultiplePermissionsListener
                }
            })
            .check()

    }

    private fun showError(throwable: Throwable) {
        if (throwable is BleScanException) {
            // on first step ask for consent of the user to activate bluetooth.
            // You can add bluetooth admin permission, to activate it by yourself.
            // https://developer.android.com/reference/android/bluetooth/BluetoothAdapter.html#enable()
            if (throwable.reason == BLUETOOTH_DISABLED) {
                Snackbar.make(activity_scan_root, "Bluetooth disabled! Please enable it.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("enable") {
                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                    }.show()
            }
        } else {
            Snackbar.make(activity_scan_root, throwable.localizedMessage, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun showDevice(device: ScanViewModel.MovisensDevice) {
        adapter.addDevice(device)
    }

    override fun onPause() {
        super.onPause()
        if (::scanDisposable.isInitialized)
            scanDisposable.dispose()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            scanDisposable.dispose()
            scanDisposable = scanViewModel.getMovisensDevices()
                .subscribe(this@ScanActivity::showDevice, this@ScanActivity::showError)
        }
    }
}