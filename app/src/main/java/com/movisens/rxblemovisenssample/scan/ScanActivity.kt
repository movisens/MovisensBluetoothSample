package com.movisens.rxblemovisenssample.scan

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
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
        scanDisposable = scanViewModel.getMovisensDevices()
            .subscribe({ device -> showDevice(device) }, { throwable -> showError(throwable) })
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