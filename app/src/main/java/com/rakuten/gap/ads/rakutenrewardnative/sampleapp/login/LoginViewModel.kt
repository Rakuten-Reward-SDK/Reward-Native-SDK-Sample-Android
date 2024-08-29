package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth.IAuthService
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth.rae.UserSdkAuth
import kotlinx.coroutines.launch

/**
 *
 * @author zack.keng
 * Created on 2024/08/29
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */
class LoginViewModel(private val authService: IAuthService) : ViewModel() {
    private val _accessTokenLiveData = MutableLiveData<String>()
    val accessTokenLiveData: LiveData<String>
        get() = _accessTokenLiveData

    fun login() {
        viewModelScope.launch { authService.login() }
    }

    fun logout() {
        viewModelScope.launch { authService.logout() }
    }

    fun isLoggedIn() = authService.isLoggedIn()

    fun getAccessToken() {
        viewModelScope.launch {
            (authService as UserSdkAuth).getAccessToken()?.let {
                _accessTokenLiveData.value = it
            }
        }
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