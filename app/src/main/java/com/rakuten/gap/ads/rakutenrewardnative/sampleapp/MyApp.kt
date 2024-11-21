package com.rakuten.gap.ads.rakutenrewardnative.sampleapp

import android.app.Application
import com.rakuten.gap.ads.mission_core.RakutenRewardConfig
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth.rae.UserSdkAuth

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        UserSdkAuth.init(this)
        if (BuildConfig.DEBUG) {
            // call this method to enable debug log
            // After enable the debug log, you can see the SDK logs with the tag `RakutenRewardSDK`.
            RakutenRewardConfig.isDebuggable()
        }
    }
}