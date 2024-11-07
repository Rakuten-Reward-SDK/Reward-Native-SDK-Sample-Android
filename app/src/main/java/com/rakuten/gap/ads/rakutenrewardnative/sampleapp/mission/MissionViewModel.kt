package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.mission

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rakuten.gap.ads.mission_core.Failed
import com.rakuten.gap.ads.mission_core.RakutenRewardCoroutine
import com.rakuten.gap.ads.mission_core.Success
import com.rakuten.gap.ads.mission_core.data.MissionData
import com.rakuten.gap.ads.mission_core.data.MissionLiteData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * This class demo about Mission API
 *
 * API to demo [RakutenRewardCoroutine.getMissionsLite], [RakutenRewardCoroutine.getMissionDetails], [RakutenRewardCoroutine.logAction]
 */
class MissionViewModel: ViewModel() {
    private val _missions = MutableStateFlow<List<MissionLiteData>>(emptyList())
    val missions: StateFlow<List<MissionLiteData>> = _missions

    private val _missionDetails = MutableStateFlow<MissionData?>(null)
    val missionDetails: StateFlow<MissionData?> = _missionDetails

    init {
        getMissions()
    }

    private fun getMissions() {
        viewModelScope.launch {
            when (val missionList = RakutenRewardCoroutine.getMissionsLite()) {
                is Failed -> _missions.value = emptyList()
                is Success -> _missions.value = missionList.data
            }
        }
    }

    fun getMissionDetails(code: String) {
        viewModelScope.launch {
            when (val mission = RakutenRewardCoroutine.getMissionDetails(code)) {
                is Failed -> _missionDetails.value = null
                is Success -> _missionDetails.value = mission.data
            }
        }
    }

    fun logAction(code: String) {
        viewModelScope.launch {
            when (val result = RakutenRewardCoroutine.logAction(code)) {
                is Failed -> Log.w("MissionViewModel", "Failed to log action: $code [${result.error}]")
                is Success -> getMissionDetails(code)
            }
        }
    }
}