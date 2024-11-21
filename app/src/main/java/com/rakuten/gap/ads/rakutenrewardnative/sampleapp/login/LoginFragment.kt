package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rakuten.gap.ads.mission_core.RakutenReward
import com.rakuten.gap.ads.mission_core.api.status.RakutenRewardAPIError
import com.rakuten.gap.ads.mission_core.listeners.LogoutResultCallback
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.MainActivity
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth.rae.UserSdkLogoutReceiver
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.databinding.FragmentLoginBinding
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.util.isAndroid14OrNewer
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.util.openDialog

/**
 *
 * @author zack.keng
 * Created on 2024/08/27
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */
class LoginFragment : Fragment() {
    companion object {
        const val REQUEST_CODE = 1012
    }

    private val viewModel: LoginViewModel by lazy {
        (requireActivity() as MainActivity).viewModel
    }

    private lateinit var binding: FragmentLoginBinding

    private var logoutReceiver: UserSdkLogoutReceiver? = null


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
        viewModel.accessTokenLiveData.observe(viewLifecycleOwner) {
            RakutenReward.setRaeToken(it)
            RakutenReward.startSession()
            checkLoginStatus()
        }
    }

    override fun onStop() {
        super.onStop()
        logoutReceiver?.let {
            requireContext().unregisterReceiver(it)
        }
    }

    private fun setListener() {
        with(binding) {
            loginButton.setOnClickListener {
                if (requireContext().isAndroid14OrNewer()) {
                    requireContext().openDialog("User SDK is no longer supported for Android 14 and above")
                } else {
                    viewModel.login()
                }
            }
            logoutButton.setOnClickListener {
                if (requireContext().isAndroid14OrNewer()) {
                    requireContext().openDialog("User SDK is no longer supported for Android 14 and above")
                } else {
                    viewModel.logout()
                }
            }
        }
    }

    private val logoutCallback: LogoutResultCallback by lazy {
        object : LogoutResultCallback {
            override fun logoutFailed(e: RakutenRewardAPIError) {
                requireContext().openDialog("Logout Failed ")
            }

            override fun logoutSuccess() {
                checkLoginStatus()
            }

        }
    }

    private fun checkLoginStatus() {
        val isLoggedIn = viewModel.isLoggedIn()
        binding.loginButton.isEnabled = !isLoggedIn
        binding.logoutButton.isEnabled = isLoggedIn

        if (isLoggedIn && logoutReceiver == null) {
            logoutReceiver =
                UserSdkLogoutReceiver.registerReceiver(requireContext(), logoutCallback)
        }
    }
}
