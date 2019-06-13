package com.movisens.rxblemovisenssample.model

import android.os.Build
import com.movisens.movisensgattlib.MovisensCharacteristics.MOVEMENT_ACCELERATION_BUFFERED
import com.movisens.movisensgattlib.attributes.DataAvailable
import com.movisens.movisensgattlib.attributes.MeasurementEnabled
import com.movisens.rxblemovisenssample.util.ByteBufferExt
import com.movisens.rxblemovisenssample.util.MockBleDeviceBuilder
import com.movisens.rxblemovisenssample.util.MockBleDeviceBuilder.Companion.DEFAULT_MAC
import com.polidea.rxandroidble2.mockrxandroidble.RxBleClientMock
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.M])
@RunWith(RobolectricTestRunner::class)
class MovisensDeviceRepositoryTest {
    @Test
    internal fun testMovisensSensorState() {
        val rxBleClient = RxBleClientMock.Builder()
            .addDevice(
                MockBleDeviceBuilder()
                    .setDataAvailable(true)
                    .setMeasurementEnabled(true)
                    .build()
            )
            .build()

        val movisensDevicesRepository = MovisensDevicesRepository(rxBleClient)

        val test = movisensDevicesRepository.getMovisensSensorState(
            DEFAULT_MAC,
            BiFunction<DataAvailable, MeasurementEnabled, Pair<Boolean, Boolean>> { dataAvailable, measurementEnabled ->
                return@BiFunction Pair<Boolean, Boolean>(
                    dataAvailable.dataAvailable,
                    measurementEnabled.measurementEnabled
                )
            }).test()

        test.assertValue(Pair(first = true, second = true))
    }

    @Test
    internal fun testMovementAcc() {
        val rxBleClient = RxBleClientMock.Builder()
            .addDevice(
                MockBleDeviceBuilder()
                    .setDataAvailable(true)
                    .setMeasurementEnabled(true)
                    .addNotificationSource(
                        MOVEMENT_ACCELERATION_BUFFERED.uuid,
                        Observable.just(getMovementAccelerationBufferedAsBytes(2.0))
                    )
                    .build()
            )
            .build()

        val movisensDevicesRepository = MovisensDevicesRepository(rxBleClient)

        val test = movisensDevicesRepository.activateMovementAccelerationIfPossible(DEFAULT_MAC).test()

        test.assertValue { it.movementAcceleration.equals(2.0) }
    }

    private fun getMovementAccelerationBufferedAsBytes(movementAcceleration: Double): ByteArray? {
        val en = ByteBufferExt.allocate(7)
        en.putUint32(0)
        en.putInt8(1)
        en.putInt16((movementAcceleration / 0.00390625).toShort())
        return en.array()
    }

}