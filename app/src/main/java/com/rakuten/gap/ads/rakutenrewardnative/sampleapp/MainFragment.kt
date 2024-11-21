package com.rakuten.gap.ads.rakutenrewardnative.sampleapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.ListFragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.rakuten.gap.ads.mission_core.Failed
import com.rakuten.gap.ads.mission_core.RakutenReward
import com.rakuten.gap.ads.mission_core.status.RakutenRewardSDKStatus
import com.rakuten.gap.ads.mission_ui.api.activity.openSDKPortal
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.databinding.FragmentMainBinding
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.start.Option2StartSessionActivity
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.start.Option3StartSessionActivity
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.util.justStart
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.util.showToast

/**
 *
 * @author zack.keng
 * Created on 2024/08/27
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */
class MainFragment : ListFragment() {
    private lateinit var binding: FragmentMainBinding

    private val features: List<FeatureItem> by lazy {
        listOf(
            FeatureItem("Login") { navigate(MainFragmentDirections.goToLoginFragment()) },
            FeatureItem("SDK Portal") { checkSdkStatus { launchSDKPortal() } },
            FeatureItem("Missions") { checkSdkStatus { navigate(MainFragmentDirections.goToMissionsFragment()) } },
            FeatureItem(getString(R.string.title_option2)) {
                checkSdkStatus {
                    requireContext().justStart(Option2StartSessionActivity::class.java)
                }
            },
            FeatureItem(getString(R.string.title_option3)) {
                checkSdkStatus {
                    requireContext().justStart(Option3StartSessionActivity::class.java)
                }
            }
        )
    }

    private fun launchSDKPortal() {
        RakutenReward.openSDKPortal(
            isPortalOpenedCallback = {
                // this callback is to check whether the portal is opened or not
                if (it is Failed) {
                    requireContext().showToast("Failed to open SDK Portal [${it.error}]")
                }
            },
            activityResultCallback = {
                // if you have a mission which require user to open the Reward SDK portal,
                // we recommend to log action after user close the portal
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val labels = features.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, labels)
        listAdapter = adapter
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        val selectedFeature = features[position]
        selectedFeature.directions.invoke()
    }

    private fun navigate(directions: NavDirections) {
        findNavController().navigate(directions)
    }

    private inline fun checkSdkStatus(callback: () -> Unit) {
        if (RakutenReward.status == RakutenRewardSDKStatus.ONLINE) {
            callback()
        } else {
            requireContext().showToast("Please login first")
        }
    }
}

typealias ClickEvent = () -> Unit

data class FeatureItem(
    val name: String,
    val directions: ClickEvent
)