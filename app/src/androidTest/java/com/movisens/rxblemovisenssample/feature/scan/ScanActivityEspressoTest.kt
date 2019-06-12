package com.movisens.rxblemovisenssample.feature.scan

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import com.movisens.rxblemovisenssample.R
import io.reactivex.Observable
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

/**
 * Created by Robert Zetzsche on 11.06.2019.
 */
class ScanActivityEspressoTest {

    @Rule
    @JvmField
    val rule = ActivityTestRule(ScanActivity::class.java, true, false)

    lateinit var scanViewModel: ScanViewModel

    @Before
    fun setup() {
        scanViewModel = mock(ScanViewModel::class.java)
        doReturn(Observable.just(ScanViewModel.MovisensDevice("Movisens 0654", "00:5a:23:14:a5:33", 0)))
            .`when`(scanViewModel).getMovisensDevices()


        loadKoinModules(module {
            viewModel(override = true) {
                scanViewModel
            }
        })
    }

    @Test
    fun testRightItemCount() {
        rule.launchActivity(null)
        verify(scanViewModel).getMovisensDevices()
        onView(withId(R.id.devices_recyclerview)).check { view, exception ->
            val recyclerView = view as RecyclerView
            val itemCount = recyclerView.adapter?.itemCount
            assertThat(itemCount, `is`(1))
        }
    }

    @Test
    fun testClickOnDeviceOpensConnectActivity() {
        rule.launchActivity(null)
        verify(scanViewModel).getMovisensDevices()
        onView(withText("Movisens 0654")).perform(click())
        onView(withId(R.id.sensor_name)).check(matches(withText("Movisens 0654")))
        onView(withId(R.id.sensor_mac)).check(matches(withText("00:5a:23:14:a5:33")))
    }

    @After
    fun cleanUp() {
        stopKoin()
    }

}