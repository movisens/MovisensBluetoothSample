package com.movisens.rxblemovisenssample.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

/**
 * Created by Robert Zetzsche on 23.05.2019.
 */
class GenericDialogFragment : DialogFragment() {

    lateinit var genericDialog: Dialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        retainInstance = true
        return genericDialog
    }

    override fun onDestroyView() {
        val dialog = dialog
        // handles https://code.google.com/p/android/issues/detail?id=17423
        if (dialog != null && retainInstance) {
            dialog.setDismissMessage(null)
        }
        super.onDestroyView()
    }
}