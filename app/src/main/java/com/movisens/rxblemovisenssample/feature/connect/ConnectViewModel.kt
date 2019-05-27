package com.movisens.rxblemovisenssample.feature.scan.connect

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.movisens.rxblemovisenssample.model.MovisensDevicesRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import java.util.concurrent.TimeUnit

/**
 * Created by Robert Zetzsche on 22.05.2019.
 */
class ConnectViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MovisensDevicesRepository(application)

    data class StartBluetoothConnectionModel(val dataAvailable: Boolean, val measurementEnabled: Boolean)

    fun getMovisensSensorState(mac: String): Observable<StartBluetoothConnectionModel> {
        return repository.getMovisensSensorState(
            mac,
            BiFunction { dataAvailable, measurementAvailable ->
                StartBluetoothConnectionModel(
                    dataAvailable.dataAvailable,
                    measurementAvailable.measurementEnabled
                )
            })
    }

    fun stopMeasurementAndDeleteData(mac: String): Observable<Boolean> {
        return repository.stopMeasurementAndDeleteData(mac)
            .observeOn(AndroidSchedulers.mainThread())
            .delaySubscription(5, TimeUnit.SECONDS)
    }

    fun deleteData(mac: String): Observable<Boolean> {
        return repository.deleteData(mac)
            .observeOn(AndroidSchedulers.mainThread())
            .delaySubscription(5, TimeUnit.SECONDS)
    }

    fun startMeasurementAndActivateMovementAcceleration(mac: String): Observable<Double> {
        return repository.startMeasurementAndActivateMovementAcceleration(mac).map { it.movementAcceleration }
    }
}