package com.movisens.rxblemovisenssample.util

import android.bluetooth.BluetoothGattCharacteristic
import com.movisens.movisensgattlib.MovisensCharacteristics.*
import com.movisens.movisensgattlib.MovisensServices.SENSOR_CONTROL
import com.movisens.smartgattlib.helper.Characteristic
import com.polidea.rxandroidble2.RxBleDevice
import com.polidea.rxandroidble2.mockrxandroidble.RxBleClientMock
import io.reactivex.Observable.just
import java.util.*

class MockBleDeviceBuilder {
    private var measurementEnabled: Boolean = false
    private var dataAvailable: Boolean = false
    private var macAddress: String = DEFAULT_MAC
    private var name: String = DEFAULT_NAME


    private val bufferedChars: MutableSet<Characteristic<*>>

    companion object {
        const val DEFAULT_MAC = "00:5a:23:14:a5:33"
        const val DEFAULT_NAME = "MOVISENS Sensor 2365"
    }

    init {
        bufferedChars = HashSet()
    }

    fun build(): RxBleDevice {
        return RxBleClientMock.DeviceBuilder()
            .deviceMacAddress(macAddress)
            .deviceName(name)
            .scanRecord(ByteArray(0))
            .rssi(-40)
            .notificationSource(DATA_AVAILABLE.uuid, just(getBooleanAsBytes(dataAvailable)))
            .addService(
                SENSOR_CONTROL.uuid,
                buildCharacteristics()
            )
            .build()
    }

    private fun buildCharacteristics(): List<BluetoothGattCharacteristic> {
        val characteristicsBuilder = RxBleClientMock.CharacteristicsBuilder()
            .addCharacteristic(
                DATA_AVAILABLE.uuid,
                getBooleanAsBytes(dataAvailable),
                RxBleClientMock.DescriptorsBuilder()
                    .build()
            )
            .addCharacteristic(
                MEASUREMENT_ENABLED.uuid,
                getBooleanAsBytes(measurementEnabled),
                RxBleClientMock.DescriptorsBuilder()
                    .build()
            )
            .addCharacteristic(
                DELETE_DATA.uuid,
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

    fun setMeasurementEnabled(measurementEnabled: Boolean): MockBleDeviceBuilder {
        this.measurementEnabled = measurementEnabled
        return this
    }

    fun setDataAvailable(dataAvailable: Boolean): MockBleDeviceBuilder {
        this.dataAvailable = dataAvailable
        return this
    }

    fun setMacAddress(macAddress: String): MockBleDeviceBuilder {
        this.macAddress = macAddress
        return this
    }

    fun setName(name: String): MockBleDeviceBuilder {
        this.name = name
        return this
    }

    fun addCharacteristic(characteristic: Characteristic<*>): MockBleDeviceBuilder {
        this.bufferedChars.add(characteristic)
        return this
    }

    private fun getBooleanAsBytes(bool: Boolean): ByteArray {
        val en = ByteBufferExt.allocate(1)
        en.putBoolean(bool)
        return en.array()
    }

    private fun getIntAsBytes(integer: Int): ByteArray {
        val en = ByteBufferExt.allocate(4)
        en.putInt32(integer)
        return en.array()
    }
}