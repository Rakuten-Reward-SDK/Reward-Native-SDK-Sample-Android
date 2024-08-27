package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.rakuten.gap.ads.mission_core.RakutenAuth
import com.rakuten.gap.ads.mission_core.api.status.RakutenRewardAPIError
import com.rakuten.gap.ads.mission_core.listeners.LoginResultCallback
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.databinding.FragmentLoginBinding
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            RakutenAuth.handleActivityResult(data, object : LoginResultCallback {
                override fun loginFailed(e: RakutenRewardAPIError) {
                    requireContext().openDialog("Login failed: $e")
                }

                override fun loginSuccess() {
                    checkLoginStatus()
                    findNavController().navigateUp()
                }

            })
        }
    }

    private fun setListener() {
        with(binding) {
            loginButton.setOnClickListener {
                login()
            }
        }
    }

    private fun login() {
        RakutenAuth.openLoginPage(this, REQUEST_CODE)
    }

    private fun checkLoginStatus() {
        val isLoggedIn = RakutenAuth.hasUserSignedIn()
        binding.loginButton.isEnabled = !isLoggedIn
        binding.logoutButton.isEnabled = isLoggedIn
    }
}
