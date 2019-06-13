package com.movisens.rxblemovisenssample.feature.scan

import androidx.lifecycle.ViewModel
import com.movisens.rxblemovisenssample.model.MovisensDevicesRepository
import com.polidea.rxandroidble2.scan.ScanResult
import io.reactivex.Observable

open class ScanViewModel(private val repository: MovisensDevicesRepository) : ViewModel() {

    open fun getMovisensDevices(): Observable<MovisensDevice> {
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