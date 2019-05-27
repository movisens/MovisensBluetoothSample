package com.movisens.rxblemovisenssample.feature.scan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.movisens.rxblemovisenssample.model.MovisensDevicesRepository
import com.polidea.rxandroidble2.scan.ScanResult
import io.reactivex.Observable

class ScanViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MovisensDevicesRepository(application)

    fun getMovisensDevices(): Observable<MovisensDevice> {
        return repository.getAllMovisensDevices()
            .map { scanResult: ScanResult ->
                MovisensDevice(
                    scanResult.bleDevice.name!!,
                    scanResult.bleDevice.macAddress!!,
                    scanResult.rssi
                )
            }
    }

    data class MovisensDevice(val name: String, val mac: String, val rssi: Int)
}