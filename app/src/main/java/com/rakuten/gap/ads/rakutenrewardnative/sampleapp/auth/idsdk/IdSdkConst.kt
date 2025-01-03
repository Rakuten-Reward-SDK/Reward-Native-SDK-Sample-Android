package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth.idsdk

import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.BuildConfig

/**
 *
 * @author zack.keng
 * Created on 2024/10/28
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */
object IdSdkConst {
    const val CLIENT_ID = BuildConfig.idSdkClientId
    const val SERVICE_URL = BuildConfig.idSdkServiceUrl
    const val SCOPES_MISSION = BuildConfig.idSdkScopeMission
    const val AUDIENCE_MISSION = BuildConfig.idSdkAudienceMission
    const val TOKEN_ENDPOINT = BuildConfig.accessTokenEndPoint
}