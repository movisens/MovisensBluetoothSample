package com.movisens.rxblemovisenssample.application

import android.app.Application
import com.polidea.rxandroidble2.exceptions.BleException
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins

/**
 * Created by Robert Zetzsche on 27.05.2019.
 */
class Application : Application() {

    override fun onCreate() {
        super.onCreate()

        // ignore BleExceptions as they were surely delivered at least once
        // Please read https://github.com/Polidea/RxAndroidBle/wiki/FAQ:-UndeliverableException
        RxJavaPlugins.setErrorHandler { error ->
            if (error is UndeliverableException && error.cause is BleException) {
                return@setErrorHandler
            } else {
                throw Exception(error)
            }
        }
    }
}