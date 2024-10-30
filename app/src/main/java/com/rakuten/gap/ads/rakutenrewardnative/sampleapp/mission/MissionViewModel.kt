package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.mission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rakuten.gap.ads.mission_core.Failed
import com.rakuten.gap.ads.mission_core.RakutenRewardCoroutine
import com.rakuten.gap.ads.mission_core.Success
import com.rakuten.gap.ads.mission_core.data.MissionLiteData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 *
 * @author zack.keng
 * Created on 2024/10/29
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */
class MissionViewModel: ViewModel() {
    private val _missions = MutableStateFlow<List<MissionLiteData>>(emptyList())
    val missions: StateFlow<List<MissionLiteData>> = _missions

    init {
        getMissions()
    }

    fun getMissions() {
        viewModelScope.launch {
            when (val missionList = RakutenRewardCoroutine.getMissionsLite()) {
                is Failed -> _missions.value = emptyList()
                is Success -> _missions.value = missionList.data
            }
        }
    }
}