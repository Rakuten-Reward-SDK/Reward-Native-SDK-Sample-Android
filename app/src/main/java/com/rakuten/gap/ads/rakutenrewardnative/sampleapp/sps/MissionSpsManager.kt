package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.sps

import com.rakuten.gap.ads.mission_core.RakutenRewardConfig
import com.rakuten.gap.ads.mission_sps.activity.mode.MissionTheme
import com.rakuten.gap.ads.mission_sps.api.RakutenMissionSps
import com.rakuten.gap.ads.mission_sps.api.getTheme
import com.rakuten.gap.ads.mission_sps.api.isMissionFeaturesOptedOut
import com.rakuten.gap.ads.mission_sps.api.setOptOutMissionFeatures
import com.rakuten.gap.ads.mission_sps.api.setTheme
import com.rakuten.gap.ads.mission_sps.listener.SpsMissionListener

/**
 *
 * @author zack.keng
 * Created on 2024/11/29
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */
class MissionSpsManager : SpsMissionListener {
    init {
        RakutenMissionSps.INSTANCE.setSpsMissionListener(this)
    }

    val currentTheme: String
        get() {
            return when (RakutenRewardConfig.getTheme()) {
                MissionTheme.Panda -> "Panda"
                MissionTheme.Simple -> "Simple"
            }
        }

    val isMissionFeatureOptedOut: Boolean
        get() = RakutenRewardConfig.isMissionFeaturesOptedOut()


    fun setMissionTheme(theme: String) {
        when (theme) {
            "Panda" -> RakutenRewardConfig.setTheme(MissionTheme.Panda)
            "Simple" -> RakutenRewardConfig.setTheme(MissionTheme.Simple)
        }
    }

    fun setOptOutMissionFeature(optOut: Boolean) {
        RakutenRewardConfig.setOptOutMissionFeatures(optOut)
    }

    override fun onThemeChanged(theme: MissionTheme) {
        // Handle theme change
    }

}