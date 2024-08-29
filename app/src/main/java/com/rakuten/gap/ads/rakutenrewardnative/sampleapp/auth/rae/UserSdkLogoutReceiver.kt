package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth.rae

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.rakuten.gap.ads.mission_core.RakutenAuth
import com.rakuten.gap.ads.mission_core.listeners.LogoutResultCallback
import jp.co.rakuten.sdtd.user.LoginConfig

/**
 *
 * @author zack.keng
 * Created on 2024/08/29
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */
class UserSdkLogoutReceiver : BroadcastReceiver() {
    private lateinit var logoutCallback: LogoutResultCallback

    companion object {
        @SuppressLint("UnspecifiedRegisterReceiverFlag")
        fun registerReceiver(
            context: Context,
            logoutCallback: LogoutResultCallback
        ): UserSdkLogoutReceiver {
            val receiver = UserSdkLogoutReceiver()
            receiver.logoutCallback = logoutCallback
            val intentFilter = IntentFilter(LoginConfig.ACTION_APP_LOGOUT).apply {
                addDataScheme("package")
                addDataAuthority(context.packageName, null)
            }
            context.registerReceiver(receiver, intentFilter)
            return receiver
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        RakutenAuth.logout(logoutCallback)
    }
}