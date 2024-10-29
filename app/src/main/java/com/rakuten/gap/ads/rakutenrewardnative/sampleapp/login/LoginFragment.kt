package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.rakuten.gap.ads.mission_core.RakutenAuth
import com.rakuten.gap.ads.mission_core.api.status.RakutenRewardAPIError
import com.rakuten.gap.ads.mission_core.listeners.LogoutResultCallback
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.databinding.FragmentLoginBinding

/**
 *
 * @author zack.keng
 * Created on 2024/08/27
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by activityViewModels<LoginViewModel>()

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkLoginStatus()
        setListener()
    }

    private fun setListener() {
        with(binding) {
            loginButton.setOnClickListener {
                viewModel.login()
            }
            logoutButton.setOnClickListener {
                viewModel.logout()
                RakutenAuth.logout(object : LogoutResultCallback {
                    override fun logoutFailed(e: RakutenRewardAPIError) = Unit

                    override fun logoutSuccess() = Unit
                })
            }
        }
        viewModel.isLoggedInLiveData.observe(viewLifecycleOwner) {
            binding.loginButton.isEnabled = !it
            binding.logoutButton.isEnabled = it
        }
    }

    private fun checkLoginStatus() {
        viewModel.isLoggedIn()
    }
}
