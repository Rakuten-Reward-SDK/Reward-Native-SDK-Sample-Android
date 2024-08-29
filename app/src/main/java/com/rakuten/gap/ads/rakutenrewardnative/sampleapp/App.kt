package com.rakuten.gap.ads.rakutenrewardnative.sampleapp

import android.app.Application
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth.rae.UserSdkAuth
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.login.LoginFragment

/**
 *
 * @author zack.keng
 * Created on 2024/08/29
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        UserSdkAuth.init(this)
    }
}