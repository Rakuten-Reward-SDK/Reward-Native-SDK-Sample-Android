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
import com.rakuten.gap.ads.mission_core.RakutenReward
import com.rakuten.gap.ads.mission_ui.api.activity.openSDKPortal
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.databinding.FragmentMainBinding

/**
 *
 * @author zack.keng
 * Created on 2024/08/27
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */
class MainFragment : ListFragment() {
    private lateinit var binding: FragmentMainBinding

    private val features = listOf(
        FeatureItem("Login") { navigate(MainFragmentDirections.goToLoginFragment()) },
        FeatureItem("SDK Portal") { RakutenReward.openSDKPortal() }
    )

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
}

typealias ClickEvent = () -> Unit

data class FeatureItem(
    val name: String,
    val directions: ClickEvent
)