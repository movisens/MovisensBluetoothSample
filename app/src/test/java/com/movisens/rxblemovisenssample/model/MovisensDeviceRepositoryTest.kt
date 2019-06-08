package com.movisens.rxblemovisenssample.model

import android.os.Build
import com.movisens.movisensgattlib.attributes.DataAvailable
import com.movisens.movisensgattlib.attributes.MeasurementEnabled
import com.movisens.rxblemovisenssample.util.MockMovisensBleDeviceBuilder
import com.movisens.rxblemovisenssample.util.MockMovisensBleDeviceBuilder.Companion.DEFAULT_MAC
import com.polidea.rxandroidble2.mockrxandroidble.RxBleClientMock
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
                MockMovisensBleDeviceBuilder()
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
}