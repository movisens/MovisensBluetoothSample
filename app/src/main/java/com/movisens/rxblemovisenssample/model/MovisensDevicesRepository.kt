package com.movisens.rxblemovisenssample.model

import com.movisens.movisensgattlib.MovisensCharacteristics.*
import com.movisens.movisensgattlib.attributes.*
import com.movisens.rxblemovisenssample.exceptions.UnrecoverableException
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanResult
import com.polidea.rxandroidble2.scan.ScanSettings
import com.polidea.rxandroidble2.scan.ScanSettings.CALLBACK_TYPE_ALL_MATCHES
import com.polidea.rxandroidble2.scan.ScanSettings.SCAN_MODE_LOW_LATENCY
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

class MovisensDevicesRepository(private val rxBleClient: RxBleClient) {

    companion object {
        const val MIN_RSSI = -90
    }

    fun getAllMovisensDevices(): Observable<ScanResult> {
        return rxBleClient.scanBleDevices(
            ScanSettings.Builder()
                .setScanMode(SCAN_MODE_LOW_LATENCY)
                .setCallbackType(CALLBACK_TYPE_ALL_MATCHES)
                .build()
            , ScanFilter.empty()
        )
            .filter { scanResult: ScanResult -> scanResult.rssi > MIN_RSSI }
            .filter { scanResult: ScanResult -> isMovisensDevice(scanResult) }
            .filter { scanResult: ScanResult -> scanResult.bleDevice.name!!.contains("2695") }
    }

    fun <T> getMovisensSensorState(
        mac: String,
        biFunction: BiFunction<DataAvailable, MeasurementEnabled, T>
    ): Observable<T> {
        return rxBleClient.getBleDevice(mac)
            .establishConnection(false)
            .flatMap { connection ->
                combineSensorStateWithFunction(connection, biFunction)
            }
    }

    private fun <T> combineSensorStateWithFunction(
        connection: RxBleConnection,
        biFunction: BiFunction<DataAvailable, MeasurementEnabled, T>
    ): Observable<T> {
        return Observable.combineLatest(
            connection.readCharacteristic(DATA_AVAILABLE.uuid)
                .toObservable()
                .map { DataAvailable(it) },
            connection.readCharacteristic(MEASUREMENT_ENABLED.uuid)
                .toObservable()
                .map { MeasurementEnabled(it) },
            biFunction
        )
    }

    fun activateMovementAccelerationIfPossible(mac: String): Observable<MovementAccelerationData> {
        var connection: RxBleConnection? = null
        return rxBleClient.getBleDevice(mac)
            .establishConnection(false)
            .flatMap {
                connection = it
                combineSensorStateWithFunction(
                    it,
                    BiFunction<DataAvailable, MeasurementEnabled, Boolean> { t1, t2
                        ->
                        return@BiFunction t2.measurementEnabled || !t1.dataAvailable
                    })
            }
            .doOnNext { if (!it) throw UnrecoverableException() }
            .flatMap {
                connection?.writeCharacteristic(MEASUREMENT_ENABLED.uuid, MeasurementEnabled(true).bytes)
                    ?.toObservable()
            }
            .map { MeasurementEnabled(it).measurementEnabled }
            .filter { it }
            .flatMap { connection?.setupIndication(MOVEMENT_ACCELERATION_BUFFERED.uuid) }
            .flatMap { it }
            .map { MovementAccelerationBuffered.CHARACTERISTIC.createAttribute(it) as MovementAccelerationBuffered }
            .flatMapIterable { it.data }
    }

    private fun stopMeasurement(connection: RxBleConnection): Observable<MeasurementEnabled> {
        return connection.writeCharacteristic(MEASUREMENT_ENABLED.uuid, MeasurementEnabled(false).bytes)
            .toObservable()
            .map { MeasurementEnabled(it) }
    }

    fun stopMeasurementAndDeleteData(
        mac: String
    ): Observable<Boolean> {
        var rxBleConnection: RxBleConnection? = null
        return rxBleClient.getBleDevice(mac).establishConnection(false)
            .flatMap {
                rxBleConnection = it
                stopMeasurement(it)
            }
            .map { it.measurementEnabled }
            .filter { !it }
            .flatMap { deleteDataWithConnection(rxBleConnection!!) }
    }

    private fun deleteDataWithConnection(connection: RxBleConnection): Observable<Boolean> {
        return Observable.combineLatest(
            connection.setupNotification(DATA_AVAILABLE.uuid).flatMap { it }.map(::DataAvailable),
            connection.writeCharacteristic(DELETE_DATA.uuid, DeleteData(true).bytes).toObservable(),
            BiFunction<DataAvailable, ByteArray, Boolean> { dataAvailable, _ -> !dataAvailable.dataAvailable }
        )
    }

    fun deleteData(mac: String): Observable<Boolean> {
        return rxBleClient.getBleDevice(mac)
            .establishConnection(false)
            .flatMap(::deleteDataWithConnection)
    }

    fun stopSensor(mac: String): Observable<Boolean> {
        return rxBleClient.getBleDevice(mac)
            .establishConnection(false)
            .flatMap(::stopMeasurement)
            .map { !it.measurementEnabled }
    }

    private fun isMovisensDevice(scanResult: ScanResult): Boolean {
        return scanResult.bleDevice.name != null
                && scanResult.bleDevice.name!!.startsWith("MOVISENS")
    }
}

