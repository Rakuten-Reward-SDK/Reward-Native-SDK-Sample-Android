package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth.idsdk

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth.IAuthService
import r10.one.auth.Client
import r10.one.auth.MediationOptions
import r10.one.auth.PendingSession
import r10.one.auth.Session
import r10.one.auth.SessionRequest
import r10.one.auth.Token
import r10.one.auth.artifacts
import r10.one.auth.defaultClient
import r10.one.auth.exchangeToken
import r10.one.auth.idtoken.StandardClaim
import r10.one.auth.rzCookie
import r10.one.auth.sessionRequest
import java.lang.ref.WeakReference

/**
 *
 * @author zack.keng
 * Created on 2024/10/28
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */
class IdSdkAuth private constructor(activity: FragmentActivity) : IAuthService {
    companion object {
        private lateinit var instance: IdSdkAuth

        fun initClient(activity: FragmentActivity) {
            instance = IdSdkAuth(activity)
        }

        fun getInstance(): IdSdkAuth {
            return instance
        }
    }

    private val activityWeakRef: WeakReference<FragmentActivity> = WeakReference(activity)
    private val client: Client = defaultClient(activity) {
        clientId = IdSdkConst.CLIENT_ID
        serviceUrl = IdSdkConst.SERVICE_URL
        securityPolicy { disableUserPresence() }
    }
    private var session: Session? = null
    private val sessionRequest: SessionRequest by lazy {
        sessionRequest { }
    }

    override suspend fun login() {
        activityWeakRef.get()?.let { activity ->
            val completionIntent = Intent(activity, activity::class.java)
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                FLAG_MUTABLE
            } else {
                0
            }
            client.session(
                sessionRequest, activity, MediationOptions(
                    complete = PendingIntent.getActivity(activity, 0, completionIntent, flags),
                    failure = PendingIntent.getActivity(activity, 0, completionIntent, flags)
                )
            )
        }
    }

    override suspend fun logout() {
        try {
            session?.logout()
            session = null
        } catch (e: Exception) {
            Log.d("IdSdkAuth", "Logging out failed: $e")
        }
    }

    /**
     * Check if there is a valid session.
     */
    override suspend fun isLoggedIn(): Boolean {
        if (session != null) {
            return true
        }

        try {
            session = client.session(sessionRequest)
            return true
        } catch (e: Exception) {
            Log.d("IdSdkAuth", "Checking is there valid session: $e")
            return false
        }
    }

    override suspend fun getSession(pendingSession: PendingSession, callback: () -> Unit) {
        try {
            session = client.session(pendingSession)
            callback()
        } catch (e: Exception) {
            Log.d("IdSdkAuth", "Getting session: $e")
        }
    }

    /**
     * Get exchange token.
     *
     * @return Triple<Token?, String?, String?> - exchange token, rz cookie, easy id
     */
    override suspend fun getExchangeToken(
        audience: String,
        scope: Set<String>
    ): Triple<Token?, String?, String?> {
        try {
            val artifactResponse = session?.artifacts {
                +exchangeToken {
                    this.audience = audience
                    this.scope = scope
                }
                +rzCookie()
            }

            val exchangeToken = artifactResponse?.exchangeToken(audience)
            val rzCookie = artifactResponse?.rzCookie
            val easyId: String? = session?.idToken?.get(StandardClaim.SUBJECT)
            return Triple(exchangeToken, rzCookie, easyId)
        } catch (e: Exception) {
            return Triple(null, null, null)
        }
    }
}