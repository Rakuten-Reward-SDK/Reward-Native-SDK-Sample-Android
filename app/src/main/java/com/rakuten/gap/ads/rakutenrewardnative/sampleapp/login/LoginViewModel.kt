package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.login

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth.IAuthService
import kotlinx.coroutines.launch
import r10.one.auth.session.SessionMediationOptions

/**
 *
 * @author zack.keng
 * Created on 2024/08/29
 * Copyright © 2024 Rakuten Asia. All rights reserved.
 */
class LoginViewModel(private val authService: IAuthService) : ViewModel() {
    private val _exchangeTokenLiveData = MutableLiveData<String>()
    val exchangeTokenLiveData: LiveData<String>
        get() = _exchangeTokenLiveData

    private val _tokenLiveData = MutableLiveData<String>()
    val tokenLiveData: LiveData<String>
        get() = _tokenLiveData

    private val _rzCookieLiveData = MutableLiveData<String>()
    val rzCookieLiveData: LiveData<String>
        get() = _rzCookieLiveData

    private val _isLoggedInLiveData = MutableLiveData<Boolean>()
    val isLoggedInLiveData: LiveData<Boolean>
        get() = _isLoggedInLiveData

    fun login(mediation: SessionMediationOptions) {
        viewModelScope.launch { authService.login(mediation) }
    }

    fun logout() {
        viewModelScope.launch {
            authService.logout()
            _isLoggedInLiveData.postValue(false)
        }
    }

    fun isLoggedIn() {
        viewModelScope.launch {
            _isLoggedInLiveData.value = authService.isLoggedIn()
        }
    }

    fun authenticate(intent: Intent?) {
        viewModelScope.launch {
            authService.getSession(intent) {
                _isLoggedInLiveData.postValue(true)
                getAccessToken(SessionMediationOptions.NO_MEDIATION)
            }
        }
    }

    fun getAccessToken(mediation: SessionMediationOptions) {
        viewModelScope.launch {
            authService.getRzCookie()?.let {
                _rzCookieLiveData.postValue(it)
            }
            authService.getApicAccessToken(mediation)?.let {
                _tokenLiveData.postValue(it.value)
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