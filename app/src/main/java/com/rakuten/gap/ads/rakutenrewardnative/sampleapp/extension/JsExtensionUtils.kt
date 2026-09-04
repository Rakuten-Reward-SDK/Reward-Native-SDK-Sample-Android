package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.extension

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView

/**
 *
 * @author zack.keng
 * Created on 2024/11/21
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */

/**
 * Copy text to clipboard
 */
fun Context.copyToClipboard(text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("label", text)
    clipboard.setPrimaryClip(clip)
}

/**
 * Get text from clipboard

 */
fun Context.getClipboardText(): String {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    return clipboard.primaryClip?.getItemAt(0)?.text.toString()
}

/**
 * Paste text from clipboard to WebView
 */
fun WebView.pasteFromClipboard() {
    this.evaluateJavascript(
        """
                (function() {
                    var inputElement = document.querySelector('.action-input');
                    if (inputElement) {
                        inputElement.value = Android.getClipboardText();
                    }
                })();
            """.trimIndent(), null
    )
}

class WebInterface(private val context: Context) {

    /**
     * Web Interface to trigger Native code to copy text to clipboard
     */
    @JavascriptInterface
    fun getClipboardText(): String {
        return context.getClipboardText()
    }
}