package com.rakuten.gap.ads.rakutenrewardnative.sampleapp

import android.app.Application
import com.rakuten.gap.ads.mission_core.RakutenReward
import com.rakuten.gap.ads.mission_core.RakutenRewardConfig
import com.rakuten.gap.ads.mission_core.auth.RewardTokenProvider
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth.idsdk.IdSdkAuth
import r10.one.auth.session.SessionMediationOptions

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            // call this method to enable debug log
            // After enable the debug log, you can see the SDK logs with the tag `RakutenRewardSDK`.
            RakutenRewardConfig.isDebuggable()
        }

        IdSdkAuth.initClient(this)

        initRewardSDK()
    }

    private fun initRewardSDK() {
        RakutenReward.init(object: RewardTokenProvider {
            override suspend fun getAccessToken(): String {
                val authClient = IdSdkAuth.getInstance()
                val token = authClient.getApicAccessToken(SessionMediationOptions.NO_MEDIATION)
                return token?.value ?: ""
            }

        })
    }

}