package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth

import android.content.Intent
import r10.one.auth.PendingSession
import r10.one.auth.Token
import r10.one.auth.session.SessionMediationOptions

/**
 *
 * @author zack.keng
 * Created on 2024/08/29
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */
interface IAuthService {

    suspend fun login(mediation: SessionMediationOptions)

    suspend fun logout()

    suspend fun isLoggedIn(): Boolean

    suspend fun getSession(intent: Intent?, callback: () -> Unit)

    suspend fun getApicAccessToken(mediation: SessionMediationOptions): Token?

    suspend fun getRzCookie(): String?

    suspend fun getExchangeToken(mediation: SessionMediationOptions): Token?
}