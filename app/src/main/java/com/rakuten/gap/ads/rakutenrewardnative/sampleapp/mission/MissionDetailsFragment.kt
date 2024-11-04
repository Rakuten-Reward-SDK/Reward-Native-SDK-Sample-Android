package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.mission

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.placeholder
import com.rakuten.gap.ads.mission_core.data.MissionData
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.R

/**
 *
 * @author zack.keng
 * Created on 2024/11/01
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */
class MissionDetailsFragment : Fragment() {

    @Composable
    fun MissionDetailsScreen(missionData: MissionData) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(requireContext())
                        .data(missionData.iconurl)
                        .placeholder(R.drawable.baseline_question_mark_24)
                        .error(R.drawable.baseline_question_mark_24)
                        .build()
                )

                Image(
                    painter = painter,
                    contentDescription = "Mission Icon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = missionData.name ?: "No Name",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val progress: Float =
                        (missionData.progress.toFloat() / missionData.times.toFloat())
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Challenge")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = missionData.instruction ?: "NO instruction")
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewDetail() {
        MaterialTheme {
            MissionDetailsScreen(
                MissionData(
                    "Mission 1",
                    "123123",
                    "https://i.pinimg.com/474x/6d/14/8e/6d148ebbe413a5eae074af403d35a9ce.jpg",
                    "This is Mission 1",
                    null,
                    null,
                    0,
                    null,
                    null,
                    mutableMapOf(),
                    false,
                    5, 1
                )
            )
        }
    }
}