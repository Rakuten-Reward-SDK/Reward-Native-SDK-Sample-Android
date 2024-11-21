package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.util

import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.justStart(clazz: Class<*>) {
    startActivity(Intent(this, clazz))
}

fun Context.isAndroid14OrNewer(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
}