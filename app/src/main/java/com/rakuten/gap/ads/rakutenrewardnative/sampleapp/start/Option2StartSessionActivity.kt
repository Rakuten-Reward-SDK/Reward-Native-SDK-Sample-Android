package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.start

import android.os.Bundle
import com.rakuten.gap.ads.mission_core.RakutenReward
import com.rakuten.gap.ads.mission_core.RakutenRewardLifecycle
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.R

/**
 * This is an example of how to start SDK session with Option 2.
 */
class Option2StartSessionActivity : ChallengeCustomMissionActivity() {
    override fun toolbarTitle(): String = getString(R.string.title_option2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RakutenRewardLifecycle.onCreate(this)
        RakutenReward.addRakutenRewardListener(this)
    }

    override fun onStart() {
        super.onStart()
        RakutenRewardLifecycle.onStart(this)
    }

    override fun onResume() {
        super.onResume()
        RakutenRewardLifecycle.onResume(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        RakutenRewardLifecycle.onDestroy()
        RakutenReward.removeRakutenRewardListener(this)
    }
}