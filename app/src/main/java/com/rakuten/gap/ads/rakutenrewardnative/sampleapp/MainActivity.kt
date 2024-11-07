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
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth.IAuthService
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth.rae.UserSdkAuth
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.login.LoginFragment.Companion.REQUEST_CODE
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.login.LoginViewModel
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

    val viewModel: LoginViewModel by viewModels<LoginViewModel> {
        LoginViewModel.Factory(authService)
    }

    private val authService: IAuthService by lazy {
        UserSdkAuth.INSTANCE.apply {
            setRequestCode(REQUEST_CODE)
            setActivityContext(this@MainActivity)
        }
    }

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
        getAccessToken()
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("MainActivity", "OnActivityResult")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                viewModel.getAccessToken()
            } else {
                openDialog("Login cancelled")
            }
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
                showToast("Reward SDK is online")
                logDailyAction()
            }
            RakutenRewardSDKStatus.OFFLINE -> openDialog("Reward SDK is offline")
            RakutenRewardSDKStatus.APPCODEINVALID -> openDialog("Application Key is invalid")
            RakutenRewardSDKStatus.TOKENEXPIRED -> getAccessToken()
            RakutenRewardSDKStatus.USER_NOT_CONSENT -> requestConsent()
        }

    }

    private fun getAccessToken() {
        with(UserSdkAuth.INSTANCE) {
            if (isLoggedIn()) {
                lifecycleScope.launch {
                    getAccessToken()?.let {
                        RakutenReward.setRaeToken(it)
                        RakutenReward.startSession()
                    }
                }
            }
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