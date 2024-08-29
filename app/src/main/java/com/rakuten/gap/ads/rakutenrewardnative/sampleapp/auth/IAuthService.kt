package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth

/**
 *
 * @author zack.keng
 * Created on 2024/08/29
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */
interface IAuthService {

    suspend fun login()

    suspend fun logout()
}