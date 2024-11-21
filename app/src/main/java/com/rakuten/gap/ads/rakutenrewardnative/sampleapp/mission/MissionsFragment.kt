package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.mission

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

/**
 *
 * @author zack.keng
 * Created on 2024/10/29
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */
class MissionsFragment : Fragment() {
    private val viewModel: MissionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    MissionsScreen(viewModel, Modifier.fillMaxSize()) { actionCode ->
                        viewModel.getMissionDetails(actionCode)
                        findNavController().navigate(MissionsFragmentDirections.goToMissionDetailsFragment())
                    }
                }
            }
        }
    }
}