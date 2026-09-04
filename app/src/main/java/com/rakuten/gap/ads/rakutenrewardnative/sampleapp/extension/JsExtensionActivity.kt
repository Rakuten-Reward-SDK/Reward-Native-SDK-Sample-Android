package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.extension

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import com.rakuten.gap.ads.mission_core.Failed
import com.rakuten.gap.ads.mission_core.RakutenRewardConfig
import com.rakuten.gap.ads.mission_core.Success
import com.rakuten.gap.ads.mission_core.activity.RakutenRewardBaseActivity
import com.rakuten.gap.ads.mission_core.data.MissionAchievementData
import com.rakuten.gap.ads.mission_core.status.RakutenRewardSDKStatus
import com.rakuten.gap.ads.mission_ext.RewardJS
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.BuildConfig
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.mission.MissionViewModel
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.mission.MissionsScreen
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.util.showToast
import kotlinx.coroutines.launch

/**
 * This is Sample Code for JavaScript Extension SDK
 *
 * The JavaScript API will only work when the SDK status is ONLINE
 */
class JsExtensionActivity : RakutenRewardBaseActivity() {
    companion object {
        private const val SAMPLE_URL = BuildConfig.jsExtSampleUrl
        private const val SAMPLE_DOMAIN = BuildConfig.jsExtSampleDomain
        private const val APP_KEY = BuildConfig.appKey
    }

    private val viewModel: MissionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val webView = setupWebView()
        enableWebViewReadFromClipboard(webView)
        setContent {
            MaterialTheme {
                Surface {
                    JsExtensionScreen(viewModel, webView)
                }
            }
        }
    }

    /**
     * Use [RewardJS.setupWebView] API to setup the WebView
     * in order to listen the events from webpage.
     *
     * Provide the appCode, webpage domain and the WebView instance
     *
     */
    private fun setupWebView(): WebView {
        return WebView(this).apply {
            RewardJS.setupWebView(
                appCode = APP_KEY,
                domain = SAMPLE_DOMAIN,
                webView = this
            )
        }
    }

    @Composable
    fun JsExtensionScreen(viewModel: MissionViewModel, webView: WebView) {
        Column(modifier = Modifier.fillMaxSize()) {
            // WebView
            AndroidView(
                factory = { webView.apply { loadUrl(SAMPLE_URL) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.Gray)
            )

            // Mission List
            MissionsScreen(
                viewModel = viewModel, modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { actionCode ->
                copyToClipboard(actionCode)
                showToast("Action Code copied")
            }

            Button(
                onClick = {
                    webView.pasteFromClipboard()
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(8.dp)
            ) {
                Text(text = "Paste Code to WebView")
            }

        }
    }

    override fun onSDKStatusChanged(status: RakutenRewardSDKStatus) {
        super.onSDKStatusChanged(status)
        if (status == RakutenRewardSDKStatus.ONLINE) {
            viewModel.getMissions()
        }
    }

    override fun onUnclaimedAchievement(achievement: MissionAchievementData) {
        super.onUnclaimedAchievement(achievement)
        // check if mission notification type is custom
        val isCustom = achievement.custom
        // check if user disabled the mission achieved notification
        val isUiEnabled = RakutenRewardConfig.isUiEnabled()

        // show the UI only if the mission notification type is custom and
        // the user has enabled the mission achieved notification
        if (isCustom && isUiEnabled) {
            AlertDialog.Builder(this)
                .setTitle("Mission Achieved")
                .setMessage("You have achieved a mission.\nClaim [${achievement.point}] point")
                .setPositiveButton("Claim") { dialog, _ ->
                    claimPoint(achievement)
                    dialog.dismiss()
                }.setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun enableWebViewReadFromClipboard(webView: WebView) {
        webView.addJavascriptInterface(WebInterface(this@JsExtensionActivity), "Android")
    }

    /**
     * This method shows how to claim the point from MissionAchievementData object
     */
    private fun claimPoint(achievement: MissionAchievementData) {
        lifecycleScope.launch {
            when (val result = achievement.claim()) {
                is Failed -> Log.d(
                    "ChallengeCustomMissionActivity",
                    "Failed to claim point [${result.error}]"
                )

                is Success -> Log.d("ChallengeCustomMissionActivity", "Successfully claimed point")
            }
        }
    }
}