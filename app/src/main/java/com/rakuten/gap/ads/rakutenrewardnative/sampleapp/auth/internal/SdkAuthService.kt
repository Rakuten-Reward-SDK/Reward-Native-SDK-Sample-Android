package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth.internal

import android.content.Intent
import androidx.fragment.app.Fragment
import com.rakuten.gap.ads.mission_core.RakutenAuth
import com.rakuten.gap.ads.mission_core.listeners.LoginResultCallback
import com.rakuten.gap.ads.mission_core.listeners.LogoutResultCallback
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth.IAuthService

/**
 *
 * @author zack.keng
 * Created on 2024/08/29
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */
class SdkAuthService private constructor(
    private val requestCode: Int,
    private val fragment: Fragment,
    private val loginCallback: LoginResultCallback,
    private val logoutCallback: LogoutResultCallback
) : IAuthService {
    companion object {
        fun build(block: Builder.() -> Unit): SdkAuthService {
            val builder = Builder()
            builder.block()
            return builder.build()
        }
    }

    override suspend fun login() {
        RakutenAuth.openLoginPage(fragment, requestCode)
    }

    fun handleActivityResult(data: Intent?) {
        RakutenAuth.handleActivityResult(data, loginCallback)
    }

    override suspend fun logout() {
        RakutenAuth.logout(logoutCallback)
    }

    class Builder {
        private var requestCode: Int = 0
        private lateinit var fragment: Fragment
        private lateinit var loginCallback: LoginResultCallback
        private lateinit var logoutCallback: LogoutResultCallback

        fun setRequestCode(requestCode: Int) = apply {
            this.requestCode = requestCode
        }

        fun setFragment(fragment: Fragment) = apply {
            this.fragment = fragment
        }

        fun setLoginCallback(loginCallback: LoginResultCallback) = apply {
            this.loginCallback = loginCallback
        }

        fun setLogoutCallback(logoutCallback: LogoutResultCallback) = apply {
            this.logoutCallback = logoutCallback
        }

        fun build() = SdkAuthService(requestCode, fragment, loginCallback, logoutCallback)
    }
}