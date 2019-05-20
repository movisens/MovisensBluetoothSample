package com.movisens.rxblemovisenssample.scan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.movisens.rxblemovisenssample.model.MovisensDevice
import io.reactivex.disposables.Disposable

class ScanActivity : AppCompatActivity() {
    lateinit var scanViewModel: ScanViewModel
    lateinit var scanDisposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scanViewModel = ViewModelProviders.of(this).get(ScanViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        scanDisposable = scanViewModel.getMovisensDevices()
            .subscribe({ device -> showDevice(device) }, { throwable -> showError(throwable) })
    }

    private fun showError(throwable: Throwable?) {

    }

    private fun showDevice(device: MovisensDevice?) {

    }

    override fun onPause() {
        super.onPause()
        scanDisposable.dispose()
    }
}