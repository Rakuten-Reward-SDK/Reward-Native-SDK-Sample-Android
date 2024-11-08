package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.start

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.rakuten.gap.ads.mission_core.Failed
import com.rakuten.gap.ads.mission_core.RakutenRewardConfig
import com.rakuten.gap.ads.mission_core.RakutenRewardCoroutine
import com.rakuten.gap.ads.mission_core.Success
import com.rakuten.gap.ads.mission_core.api.status.RakutenRewardClaimStatus
import com.rakuten.gap.ads.mission_core.data.MissionAchievementData
import com.rakuten.gap.ads.mission_core.data.RakutenRewardUser
import com.rakuten.gap.ads.mission_core.listeners.RakutenRewardListener
import com.rakuten.gap.ads.mission_core.status.RakutenRewardSDKStatus
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.BuildConfig
import kotlinx.coroutines.launch

/**
 * This class is an example of how to show a custom mission achieved notification.
 *
 * Implement [RakutenRewardListener] and listen to [onUnclaimedAchievement] to show the custom mission achieved notification.
 */
abstract class ChallengeCustomMissionActivity : ComponentActivity(), RakutenRewardListener {

    abstract fun toolbarTitle(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    MainScreen(
                        title = toolbarTitle(),
                        onClose = { finish() },
                        onButtonClick = ::logAction
                    )
                }
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun MainScreen(title: String, onClose: () -> Unit, onButtonClick: () -> Unit) {
        Scaffold(
            topBar = {
                ToolbarWithCloseButton(title, onClose)
            }
        ) {
            CenteredButton(onClick = onButtonClick)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ToolbarWithCloseButton(title: String, onClose: () -> Unit) {
        TopAppBar(title = {
            Text(text = title, fontSize = 20.sp)
        }, navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close Screen")
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        ))
    }

    @Composable
    fun CenteredButton(onClick: () -> Unit) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Challenge Custom Mission")
            }
        }
    }

    private fun logAction() {
        lifecycleScope.launch {
            when (val result = RakutenRewardCoroutine.logAction(BuildConfig.customMissionCode)) {
                is Failed -> Log.w(
                    "ChallengeCustomMissionActivity",
                    "Failed to log action: ${result.error}"
                )

                is Success -> Log.w("ChallengeCustomMissionActivity", "Successfully logged action")
            }
        }
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

    // =============== RakutenRewardListener =====================
    override fun onSDKStatusChanged(status: RakutenRewardSDKStatus) {
        Log.d("ChallengeCustomMissionActivity", "onSDKStatusChanged: $status")
    }

    override fun onSDKClaimClosed(
        missionAchievementData: MissionAchievementData,
        status: RakutenRewardClaimStatus
    ) {
        when (status) {
            RakutenRewardClaimStatus.NOTYET -> Log.d(
                "ChallengeCustomMissionActivity",
                "Point haven't claimed yet"
            )

            RakutenRewardClaimStatus.SUCCESS -> Log.d(
                "ChallengeCustomMissionActivity",
                "Point claimed successfully"
            )

            RakutenRewardClaimStatus.FAIL -> Log.d(
                "ChallengeCustomMissionActivity",
                "Point claim failed"
            )
        }
    }

    override fun onSDKClaimPresented(missionAchievementData: MissionAchievementData) {
        Log.d("ChallengeCustomMissionActivity", "Claim Point UI presented")
    }

    override fun onSDKConsentClosed() = Unit

    override fun onSDKConsentPresented() = Unit

    override fun onUnclaimedAchievement(achievement: MissionAchievementData) {
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

    override fun onUserUpdated(user: RakutenRewardUser) {
        Log.d("ChallengeCustomMissionActivity", "User updated: $user")
    }
}