package com.movisens.rxblemovisenssample.util

import android.bluetooth.BluetoothGattCharacteristic
import com.movisens.movisensgattlib.MovisensCharacteristics.*
import com.movisens.movisensgattlib.MovisensServices.SENSOR_CONTROL
import com.movisens.smartgattlib.helper.Characteristic
import com.polidea.rxandroidble2.RxBleDevice
import com.polidea.rxandroidble2.mockrxandroidble.RxBleClientMock
import io.reactivex.Observable.just
import java.util.HashSet

class MockMovisensBleDeviceBuilder {
    private var measurementEnabled: Boolean = false
    private var dataAvailable: Boolean = false
    private val bufferedChars: MutableSet<Characteristic<*>>

    init {
        bufferedChars = HashSet()
    }

    fun build(): RxBleDevice {
        return RxBleClientMock.DeviceBuilder()
            .deviceMacAddress("00:5a:23:14:a5:33")
            .deviceName("MOVISENS Sensor 2365")
            .scanRecord(ByteArray(0))
            .rssi(-40)
            .notificationSource(DATA_AVAILABLE.getUuid(), just(getBooleanAsBytes(dataAvailable)))
            .addService(
                SENSOR_CONTROL.getUuid(),
                buildCharacteristics()
            )
            .build()
    }

    private fun buildCharacteristics(): List<BluetoothGattCharacteristic> {
        val characteristicsBuilder = RxBleClientMock.CharacteristicsBuilder()
            .addCharacteristic(
                DATA_AVAILABLE.getUuid(),
                getBooleanAsBytes(dataAvailable),
                RxBleClientMock.DescriptorsBuilder()
                    .build()
            )
            .addCharacteristic(
                MEASUREMENT_ENABLED.getUuid(),
                getBooleanAsBytes(measurementEnabled),
                RxBleClientMock.DescriptorsBuilder()
                    .build()
            )
            .addCharacteristic(
                DELETE_DATA.getUuid(),
                getBooleanAsBytes(false),
                RxBleClientMock.DescriptorsBuilder()
                    .build()
            )

        for (bufferedChar in bufferedChars) {
            characteristicsBuilder.addCharacteristic(
                bufferedChar.uuid,
                getIntAsBytes(0),
                RxBleClientMock.DescriptorsBuilder()
                    .build()
            )
        }
        return characteristicsBuilder.build()
    }

    fun setMeasurementEnabled(measurementEnabled: Boolean): MockMovisensBleDeviceBuilder {
        this.measurementEnabled = measurementEnabled
        return this
    }

    fun setDataAvailable(dataAvailable: Boolean): MockMovisensBleDeviceBuilder {
        this.dataAvailable = dataAvailable
        return this
    }

    fun addCharacteristic(characteristic: Characteristic<*>): MockMovisensBleDeviceBuilder {
        this.bufferedChars.add(characteristic)
        return this
    }

    fun getBooleanAsBytes(bool: Boolean): ByteArray {
        val en = ByteBufferExt.allocate(1)
        en.putBoolean(bool)
        return en.array()
    }

    fun getIntAsBytes(integer: Int): ByteArray {
        val en = ByteBufferExt.allocate(4)
        en.putInt32(integer)
        return en.array()
    }
}