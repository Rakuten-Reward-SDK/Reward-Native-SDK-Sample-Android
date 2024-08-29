package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth.rae

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth.IAuthService
import jp.co.rakuten.api.rae.engine.model.TokenResult
import jp.co.rakuten.sdtd.user.LoginManager
import jp.co.rakuten.sdtd.user.auth.AuthProviderRAE
import jp.co.rakuten.sdtd.user.auth.AuthResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

/**
 *
 * @author zack.keng
 * Created on 2024/08/29
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */
class UserSdkAuth :
    IAuthService {
    companion object {
        lateinit var INSTANCE: UserSdkAuth

        fun init(context: Context) {
            INSTANCE = UserSdkAuth().apply {
                init(context)
            }
        }
    }

    private lateinit var context: WeakReference<Context>
    private var requestCode: Int = 0

    private fun init(context: Context) {
        LoginManager.initialize(context)
            .addAuthProvider(
                AuthConst.AUTH_NAME, AuthProviderRAE.createJapanIdProvider()
                    .domain(AuthConst.DOMAIN)
                    .client(AuthConst.CLIENT, AuthConst.SECRET)
                    .scopes(AuthConst.SCOPE)
                    .build()
            ).apply()
    }

    fun setRequestCode(requestCode: Int) {
        this.requestCode = requestCode
    }

    fun setActivityContext(activity: Activity) {
        context = WeakReference(activity)
    }

    suspend fun getAccessToken(): String? {
        return withContext(Dispatchers.IO) {
            val service = LoginManager.getInstance().loginService
            try {
                val response: AuthResponse<TokenResult> = service.authRequest(AuthConst.AUTH_NAME)
                response.token
            } catch (e: Exception) {
                Log.e("UserSdkAuth", "Failed to get access token", e)
                null
            }
        }
    }

    override suspend fun login() {
        context.get()?.let {
            (it as ComponentActivity).startActivityForResult(
                LoginManager.getInstance().newLoginIntent(),
                requestCode
            )
        }
    }

    override suspend fun logout() {
        context.get()?.startActivity(LoginManager.getInstance().newLogoutIntent())
    }

    override fun isLoggedIn(): Boolean = LoginManager.getInstance().isLoggedIn
}