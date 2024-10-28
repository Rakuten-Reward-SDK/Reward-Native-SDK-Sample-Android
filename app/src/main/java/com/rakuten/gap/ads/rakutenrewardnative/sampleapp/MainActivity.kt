package com.rakuten.gap.ads.rakutenrewardnative.sampleapp

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.rakuten.gap.ads.mission_core.Failed
import com.rakuten.gap.ads.mission_core.RakutenReward
import com.rakuten.gap.ads.mission_core.RakutenRewardCoroutine
import com.rakuten.gap.ads.mission_core.Success
import com.rakuten.gap.ads.mission_core.activity.RakutenRewardBaseActivity
import com.rakuten.gap.ads.mission_core.api.status.RakutenRewardAPIError
import com.rakuten.gap.ads.mission_core.status.RakutenRewardSDKStatus
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.util.openDialog
import kotlinx.coroutines.launch

class MainActivity : RakutenRewardBaseActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    companion object {
        private const val DAILY_MISSION = BuildConfig.dailyMissionCode
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))
        val host: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
                ?: return

        val navController = host.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setUpActionBar(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
    }

    private fun setUpActionBar(navController: NavController) {
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSDKStatusChanged(status: RakutenRewardSDKStatus) {
        when (status) {
            RakutenRewardSDKStatus.ONLINE -> logDailyAction()
            RakutenRewardSDKStatus.OFFLINE -> openDialog("Reward SDK is offline")
            RakutenRewardSDKStatus.APPCODEINVALID -> openDialog("Application Key is invalid")
            RakutenRewardSDKStatus.TOKENEXPIRED -> openDialog("Token is expired")
            RakutenRewardSDKStatus.USER_NOT_CONSENT -> requestConsent()
        }

    }

    private fun requestConsent() {
        lifecycleScope.launch {
            RakutenReward.requestForConsent()
        }
    }

    private fun logDailyAction() {
        lifecycleScope.launch {
            when (val result = RakutenRewardCoroutine.logAction(DAILY_MISSION)) {
                is Failed -> {
                    if (result.error != RakutenRewardAPIError.MISSION_REACHED_CAP) {
                        openDialog("Log Action error [${result.error}]")
                    }
                }

                is Success -> Log.d("MainActivity", "Daily mission logged successfully")
            }
        }
    }
}