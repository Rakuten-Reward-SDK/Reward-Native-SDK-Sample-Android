package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.login

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth.IAuthService
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth.internal.SdkAuthService
import kotlinx.coroutines.launch

/**
 *
 * @author zack.keng
 * Created on 2024/08/29
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */
class LoginViewModel(private val authService: IAuthService) : ViewModel() {

    fun login() {
        viewModelScope.launch { authService.login() }
    }

    fun logout() {
        viewModelScope.launch { authService.logout() }
    }

    fun handleActivityResult(data: Intent?) {
        (authService as SdkAuthService).handleActivityResult(data)
    }

    class Factory(private val authService: IAuthService) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(authService) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}