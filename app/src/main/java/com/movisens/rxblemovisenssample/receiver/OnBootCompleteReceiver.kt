package com.movisens.rxblemovisenssample.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by Robert Zetzsche on 27.05.2019.
 */
class OnBootCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        // Nothing to do here because boot complete intent will start
        // application which handles the restart of the service
    }
}