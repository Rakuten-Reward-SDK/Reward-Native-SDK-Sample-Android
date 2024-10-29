package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.util

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.R

/**
 *
 * @author zack.keng
 * Created on 2024/08/27
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */

fun Context.openDialog(message: String): AlertDialog {
    return AlertDialog.Builder(this)
        .setTitle(R.string.app_name)
        .setMessage(message)
        .setCancelable(true)
        .show()
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            observer.onChanged(value)
            removeObserver(this)
        }
    })
}