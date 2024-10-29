package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth

import r10.one.auth.PendingSession
import r10.one.auth.Token

/**
 *
 * @author zack.keng
 * Created on 2024/08/29
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */
interface IAuthService {

    suspend fun login()

    suspend fun logout()

    suspend fun isLoggedIn(): Boolean

    suspend fun getSession(pendingSession: PendingSession, callback: () -> Unit)

    suspend fun getExchangeToken(audience: String, scope: Set<String>): Triple<Token?, String?, String?>
}