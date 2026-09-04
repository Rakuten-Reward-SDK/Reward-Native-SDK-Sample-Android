package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.mission

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.findNavController
import com.rakuten.gap.ads.mission_core.data.MissionLiteData

/**
 *
 * @author zack.keng
 * Created on 2024/11/21
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */
@Composable
fun MissionsScreen(viewModel: MissionViewModel, modifier: Modifier, onClick: (String) -> Unit) {
    val missions by viewModel.missions.collectAsState()

    if (missions.isEmpty()) {
        Text(text = "No missions available", modifier = modifier.padding(16.dp))
    } else {
        LazyColumn(modifier = modifier) {
            items(missions) { mission ->
                MissionItem(missionLiteData = mission) {
                    onClick(mission.actionCode.toString())
                }
                Divider(color = Color.LightGray, thickness = 1.dp)
            }
        }
    }
}

@Composable
fun MissionItem(missionLiteData: MissionLiteData, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .clickable {
                onClick()
            }
    ) {
        Text(
            text = missionLiteData.name.toString(),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = missionLiteData.instruction.toString(),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMissionItem() {
    MaterialTheme {
        MissionItem(
            MissionLiteData(
                "Mission 1",
                "123123",
                null,
                "This is Mission 1",
                null,
                null,
                0,
                null,
                null,
                mutableMapOf(),
                1
            )
        ) {}
    }
}
