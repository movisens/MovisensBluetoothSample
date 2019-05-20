package com.movisens.rxblemovisenssample.scan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.movisens.rxblemovisenssample.model.MovisensDevice
import com.movisens.rxblemovisenssample.model.MovisensDevicesRepository
import io.reactivex.Observable

class ScanViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MovisensDevicesRepository(application)

    fun getMovisensDevices(): Observable<MovisensDevice> {
        return repository.getAllMovisensDevices()
    }
}