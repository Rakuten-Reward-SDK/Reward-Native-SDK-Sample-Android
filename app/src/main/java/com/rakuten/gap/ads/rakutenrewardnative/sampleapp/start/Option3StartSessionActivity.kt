package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.start

import android.os.Bundle
import com.rakuten.gap.ads.mission_core.RakutenReward
import com.rakuten.gap.ads.mission_core.observers.RakutenRewardManager
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.R

/**
 * This is an example of how to start SDK session with Option 3.
 */
class Option3StartSessionActivity: ChallengeCustomMissionActivity() {
    override fun toolbarTitle(): String = getString(R.string.title_option3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RakutenRewardManager.bindRakutenRewardIn(this, this)
        RakutenReward.addRakutenRewardListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        RakutenReward.removeRakutenRewardListener(this)
    }
}