package com.movisens.rxblemovisenssample.feature.scan

import android.Manifest
import android.os.Looper.getMainLooper
import androidx.recyclerview.widget.RecyclerView
import com.movisens.rxblemovisenssample.bluetooth.BluetoothService
import com.movisens.rxblemovisenssample.feature.connect.ConnectActivity
import com.movisens.rxblemovisenssample.feature.scan.ScanActivity.Companion.SENSOR_NAME
import io.reactivex.Observable
import junit.framework.Assert.assertEquals
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.mockito.Mockito
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowApplication


/**
 * Created by Robert Zetzsche on 12.06.2019.
 */
@RunWith(RobolectricTestRunner::class)
class ScanActivityRobolectricTest {
    lateinit var scanViewModel: ScanViewModel
    lateinit var shadowApplication: ShadowApplication

    @Before
    fun setUp() {
        scanViewModel = Mockito.mock(ScanViewModel::class.java)
        Mockito.doReturn(Observable.just(ScanViewModel.MovisensDevice("Movisens 0654", "00:5a:23:14:a5:33", 0)))
            .`when`(scanViewModel).getMovisensDevices()

        loadKoinModules(module {
            viewModel(override = true) {
                scanViewModel
            }
        })

        shadowApplication = Shadows.shadowOf(RuntimeEnvironment.application)
        shadowApplication.grantPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    @Test
    @LooperMode(LooperMode.Mode.PAUSED)
    fun testRightItemCount() {
        shadowOf(getMainLooper()).idle()
        val controller = buildActivity<ScanActivity>(ScanActivity::class.java).setup()
        val recyclerView =
            controller.get().findViewById<RecyclerView>(com.movisens.rxblemovisenssample.R.id.devices_recyclerview)
        val itemCount = recyclerView.adapter?.itemCount
        Assert.assertEquals(1, itemCount)
    }

    @Test
    @LooperMode(LooperMode.Mode.PAUSED)
    fun testClickOnDeviceOpensConnectActivity() {
        shadowOf(getMainLooper()).idle()
        val controller = buildActivity<ScanActivity>(ScanActivity::class.java).setup()
        val recyclerView =
            controller.get().findViewById<RecyclerView>(com.movisens.rxblemovisenssample.R.id.devices_recyclerview)
        recyclerView.getChildAt(0).performClick()
        val nextActivityIntent = shadowApplication.nextStartedActivity
        assertEquals(nextActivityIntent.component.className, ConnectActivity::class.java.name)
        assertEquals(nextActivityIntent.getStringExtra(SENSOR_NAME), "Movisens 0654")
        assertEquals(nextActivityIntent.getStringExtra(BluetoothService.SENSOR_MAC), "00:5a:23:14:a5:33")
    }
}