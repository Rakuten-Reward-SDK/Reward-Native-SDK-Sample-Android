package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.sps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.MainActivity
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.R
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.databinding.FragmentSpsApiBinding

/**
 *
 * @author zack.keng
 * Created on 2024/11/29
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */
class MissionSpsApiFragment: Fragment() {
    private lateinit var binding: FragmentSpsApiBinding

    private val missionSpsManager by lazy {
        (requireContext() as MainActivity).missionSpsManager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpsApiBinding.inflate(inflater, container, false)
        binding.currentTheme.text = missionSpsManager.currentTheme
        binding.outOutSwitch.isChecked = missionSpsManager.isMissionFeatureOptedOut
        setOptOutLabel(missionSpsManager.isMissionFeatureOptedOut)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setListener()
    }

    private fun setListener() {
        with(binding) {
            outOutSwitch.setOnCheckedChangeListener { _, isChecked ->
                missionSpsManager.setOptOutMissionFeature(isChecked)
                setOptOutLabel(isChecked)
            }
            buttonPanda.setOnClickListener {
                missionSpsManager.setMissionTheme("Panda")
                currentTheme.text = missionSpsManager.currentTheme
            }
            buttonSimple.setOnClickListener {
                missionSpsManager.setMissionTheme("Simple")
                currentTheme.text = missionSpsManager.currentTheme
            }
        }
    }

    private fun setOptOutLabel(optOut: Boolean) {
        binding.optOutLabel.setText(if (optOut) R.string.opt_out_on_label else R.string.opt_out_off_label)
    }
}