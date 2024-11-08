package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.rakuten.gap.ads.mission_core.RakutenAuth
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.databinding.DialogAuthApiBinding
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.util.showToast

/**
 * This is an example of [RakutenAuth] API usage.
 */
fun Context.showAuthApiDialog() {
    val dialogBinding =
        DialogAuthApiBinding.inflate(LayoutInflater.from(this), null, false)
    with(dialogBinding) {
        authIsSignedIn.setOnClickListener {
            val isSignedIn = RakutenAuth.hasUserSignedIn()
            showToast("User signed in [$isSignedIn]")
        }
        authUserName.setOnClickListener {
            showToast("User's name [${RakutenAuth.getUserName()}]")
        }
        authUserInfo.setOnClickListener {
            RakutenAuth.getUserInfo({ userInfo ->
                showToast("User info [${userInfo}]")
            }) { error ->
                showToast("Failed to get user info [${error}]")
            }
        }
    }
    AlertDialog.Builder(this)
        .setView(dialogBinding.root)
        .setPositiveButton(android.R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }.show()
}