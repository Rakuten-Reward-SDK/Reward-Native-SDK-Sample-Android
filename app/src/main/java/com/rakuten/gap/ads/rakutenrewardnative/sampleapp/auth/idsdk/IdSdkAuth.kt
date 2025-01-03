package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth.idsdk

import android.content.Context
import android.content.Intent
import android.util.Log
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth.IAuthService
import kotlinx.coroutines.withTimeout
import r10.one.auth.Client
import r10.one.auth.Error
import r10.one.auth.MediationRequiredError
import r10.one.auth.NetworkError
import r10.one.auth.RequestError
import r10.one.auth.Token
import r10.one.auth.apic.apic
import r10.one.auth.defaultClient
import r10.one.auth.environment.RakutenEnv
import r10.one.auth.providers.artifacts
import r10.one.auth.providers.cookie
import r10.one.auth.providers.token
import r10.one.auth.session.SessionMediationOptions
import r10.one.auth.session.sessionContainer
import r10.one.auth.session.sessionUpdated

/**
 *
 * @author zack.keng
 * Created on 2024/10/28
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */
class IdSdkAuth private constructor(context: Context) : IAuthService {
    companion object {
        private lateinit var instance: IdSdkAuth

        fun initClient(context: Context) {
            instance = IdSdkAuth(context)
        }

        fun getInstance(): IdSdkAuth {
            return instance
        }
    }

    private val client: Client = defaultClient(context) {
        clientId = IdSdkConst.CLIENT_ID
        serviceUrl = IdSdkConst.SERVICE_URL
        securityPolicy { disableUserPresence() }
    }

    private val sessionProvider = sessionContainer(client)

    private val accessTokenForMission = sessionProvider.artifacts.apic {
        env = RakutenEnv.PROD
        scope = setOf(IdSdkConst.SCOPES_MISSION)
        tokenEndpoint = IdSdkConst.TOKEN_ENDPOINT
        tokenTtlSeconds = 3600
    }.provider()

    private val rzCookie = sessionProvider.artifacts.rzCookie.provider()

    override suspend fun login(mediation: SessionMediationOptions) {
        sessionProvider.session(mediation)
    }

    override suspend fun logout() {
        val session = sessionProvider.session
        if (session == null) {
            Log.d("IdSdkAuth", "User not authenticated")
            return
        }

        showErrors("Cannot Logout") {
            session.logout()
        }
    }

    /**
     * Check if there is a valid session.
     */
    override suspend fun isLoggedIn(): Boolean {
        val session = showErrors("Check local session failed") {
            sessionProvider.session(SessionMediationOptions.NO_MEDIATION)
        }

        return session != null
    }

    /**
     * After logged in from OMNI UI, call this method to check the session.
     */
    override suspend fun getSession(intent: Intent?, callback: () -> Unit) {
        if (intent?.sessionUpdated == true) {
            withTimeout(1_000) {
                while (sessionProvider.session == null) {
                    /* Wait for session */
                }

                if (sessionProvider.session != null) {
                    callback()
                }
            }
        }
    }

    /**
     * Get API-C access token
     */
    override suspend fun getApicAccessToken(mediation: SessionMediationOptions): Token? {
        val token = accessTokenForMission.token(mediation).onFailure {
            Log.w("IdSdkAuth", "Failed to get access token", it)
        }.getOrNull()

        return token
    }

    /**
     * Get RZ cookie
     */
    override suspend fun getRzCookie(): String? {
        val cookie = rzCookie.cookie(SessionMediationOptions.NO_MEDIATION).onFailure {
            Log.w("IdSdkAuth", "Failed to get rz cookie", it)
        }.getOrNull()

        return cookie?.value
    }

    private suspend fun <T> showErrors(prefix: String, block: suspend () -> T): T? =
        try {
            block()
        } catch (e: NetworkError) {
            Log.w(prefix, "NetworkError", e)
            null
        } catch (e: RequestError) {
            Log.w(prefix, "RequestError", e)
            null
        } catch (e: MediationRequiredError) {
            Log.w(prefix, "MediationRequiredError", e)
            null
        } catch (e: Error) {
            Log.w(prefix, "Error", e)
            null
        }
}