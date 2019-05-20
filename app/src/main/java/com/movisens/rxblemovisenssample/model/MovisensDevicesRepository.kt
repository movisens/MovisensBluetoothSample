package com.movisens.rxblemovisenssample.model

import android.app.Application
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanResult
import com.polidea.rxandroidble2.scan.ScanSettings
import com.polidea.rxandroidble2.scan.ScanSettings.CALLBACK_TYPE_ALL_MATCHES
import com.polidea.rxandroidble2.scan.ScanSettings.SCAN_MODE_LOW_LATENCY
import io.reactivex.Observable

class MovisensDevicesRepository(val application: Application) {
    private val rxBleClient: RxBleClient = RxBleClient.create(application)
    private val MIN_RSSI = -90


    fun getAllMovisensDevices(): Observable<MovisensDevice> {
        return rxBleClient.scanBleDevices(
            ScanSettings.Builder()
                .setScanMode(SCAN_MODE_LOW_LATENCY)
                .setCallbackType(CALLBACK_TYPE_ALL_MATCHES)
                .build()
            , ScanFilter.empty()
        )
            .filter { scanResult: ScanResult -> scanResult.rssi > MIN_RSSI }
            .filter { scanResult: ScanResult -> isMovisensDevice(scanResult) }
            .map { scanResult: ScanResult ->
                MovisensDevice(
                    scanResult.bleDevice.name!!,
                    scanResult.bleDevice.macAddress!!,
                    scanResult.rssi
                )
            }
    }

    private fun isMovisensDevice(scanResult: ScanResult): Boolean {
        return scanResult.bleDevice.name != null
                && scanResult.bleDevice.name!!.startsWith("MOVISENS");
    }
}