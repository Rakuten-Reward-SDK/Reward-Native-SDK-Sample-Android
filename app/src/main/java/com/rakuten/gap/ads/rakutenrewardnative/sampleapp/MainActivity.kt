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
import com.rakuten.gap.ads.mission_core.RakutenRewardLifecycle
import com.rakuten.gap.ads.mission_core.Success
import com.rakuten.gap.ads.mission_core.activity.RakutenRewardBaseActivity
import com.rakuten.gap.ads.mission_core.api.status.RakutenRewardAPIError
import com.rakuten.gap.ads.mission_core.listeners.RakutenRewardListener
import com.rakuten.gap.ads.mission_core.observers.RakutenRewardManager
import com.rakuten.gap.ads.mission_core.status.RakutenRewardConsentStatus
import com.rakuten.gap.ads.mission_core.status.RakutenRewardSDKStatus
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.util.openDialog
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.util.showToast
import kotlinx.coroutines.launch

/**
 * In order to use the RakutenReward SDK, you need to use one of these options to start SDK session:
 * 1. Extend from [RakutenRewardBaseActivity]
 * 2. Use [RakutenRewardLifecycle] in your Activity class
 * 3. Call [RakutenRewardManager.bindRakutenRewardIn] in onCreate of your Activity class
 *
 * For option 2 and 3, you need to add [RakutenRewardListener] by calling [RakutenReward.addRakutenRewardListener]
 *
 * For option 1, you can directly override [onSDKStatusChanged]
 */
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

    fun goBack() {
        findNavController(R.id.nav_host_fragment).popBackStack()
    }

    private fun setUpActionBar(navController: NavController) {
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    /**
     * This method will be called when SDK status changed
     *
     * To access any SDK API, the SDK Status must be ONLINE.
     */
    override fun onSDKStatusChanged(status: RakutenRewardSDKStatus) {
        when (status) {
            RakutenRewardSDKStatus.ONLINE -> {
                showToast("Reward SDK is online")
                logDailyAction()
            }

            RakutenRewardSDKStatus.OFFLINE -> openDialog("Reward SDK is offline")
            RakutenRewardSDKStatus.APPCODEINVALID -> openDialog("Application Key is invalid")
            RakutenRewardSDKStatus.TOKENEXPIRED -> openDialog("Token is expired")
            RakutenRewardSDKStatus.USER_NOT_CONSENT -> requestConsent()
        }

    }

    override fun onSDKConsentPresented() {
        Log.d("MainActivity", "Request Consent presented")
    }

    override fun onSDKConsentClosed() {
        super.onSDKConsentClosed()
        Log.d("MainActivity", "Request Consent closed")
    }

    private fun requestConsent() {
        lifecycleScope.launch {
            // once user provided consent, SDK status will be changed to ONLINE
            RakutenReward.requestForConsent { consentStatus ->
                when (consentStatus) {
                    RakutenRewardConsentStatus.CONSENT_PROVIDED -> showToast("User provided consent")
                    RakutenRewardConsentStatus.CONSENT_NOT_PROVIDED -> showToast("User did not provide consent")
                    RakutenRewardConsentStatus.CONSENT_FAILED -> showToast("User provided consent but failed to sync to server")
                    RakutenRewardConsentStatus.CONSENT_PROVIDED_RESTART_SESSION_FAILED -> showToast(
                        "User provided consent but failed to restart session"
                    )
                }
            }
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