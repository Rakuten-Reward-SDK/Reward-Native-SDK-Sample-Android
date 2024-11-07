package com.rakuten.gap.ads.rakutenrewardnative.sampleapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
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
import com.rakuten.gap.ads.mission_core.status.RakutenRewardSDKStatus
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth.idsdk.IdSdkAuth
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.login.LoginViewModel
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.util.observeOnce
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.util.openDialog
import kotlinx.coroutines.launch
import r10.one.auth.pendingSession

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

    private val idSdkAuth: IdSdkAuth by lazy {
        IdSdkAuth.initClient(this)
        IdSdkAuth.getInstance()
    }

    private val loginViewModel: LoginViewModel by viewModels<LoginViewModel> {
        LoginViewModel.Factory(idSdkAuth)
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
        setObserver()
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        loginViewModel.isLoggedInLiveData.observeOnce(this) {
            if (it) loginViewModel.getExchangeToken()
        }
        loginViewModel.isLoggedIn()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.pendingSession?.let { loginViewModel.authenticate(it) }
    }

    private fun setObserver() {
        loginViewModel.tokenLiveData.observe(this) {
            RakutenReward.setRIdToken(it)
            RakutenReward.startSession()
        }
        loginViewModel.exchangeTokenLiveData.observe(this) {
            loginViewModel.getAccessToken(this, it)
        }
        loginViewModel.rzCookieLiveData.observe(this) {
            RakutenReward.setRz(it)
        }
        loginViewModel.easyIdLiveData.observe(this) {
            Log.d("MainActivity", "EasyId: $it")
        }
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
                popToMain()
                logDailyAction()
            }

            RakutenRewardSDKStatus.OFFLINE -> openDialog("Reward SDK is offline")
            RakutenRewardSDKStatus.APPCODEINVALID -> openDialog("Application Key is invalid")
            RakutenRewardSDKStatus.TOKENEXPIRED -> openDialog("Token is expired")
            RakutenRewardSDKStatus.USER_NOT_CONSENT -> requestConsent()
        }

    }

    private fun popToMain() {
        val navController = findNavController(R.id.nav_host_fragment)
        navController.popBackStack(R.id.mainFragment, false)
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