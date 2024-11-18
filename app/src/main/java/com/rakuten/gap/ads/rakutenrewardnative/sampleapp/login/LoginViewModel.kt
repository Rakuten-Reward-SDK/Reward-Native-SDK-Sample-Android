package com.rakuten.gap.ads.rakutenrewardnative.sampleapp.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth.IAuthService
import com.rakuten.gap.ads.rakutenrewardnative.sampleapp.auth.idsdk.IdSdkConst
import kotlinx.coroutines.launch
import r10.one.auth.PendingSession

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

    private val _easyIdLiveData = MutableLiveData<String>()
    val easyIdLiveData: LiveData<String>
        get() = _easyIdLiveData

    private val _isLoggedInLiveData = MutableLiveData<Boolean>()
    val isLoggedInLiveData: LiveData<Boolean>
        get() = _isLoggedInLiveData

    fun login() {
        viewModelScope.launch { authService.login() }
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

    fun authenticate(pendingSession: PendingSession) {
        viewModelScope.launch {
            authService.getSession(pendingSession) {
                _isLoggedInLiveData.postValue(true)
                getExchangeToken()
            }
        }
    }

    /**
     * Return exchange token using SPS audience and SPS scopes
     */
    suspend fun getSpsExchangeToken(): String? {
        val spsScopes = IdSdkConst.SCOPES_SPS.split(",").toSet()
        val (exchangeToken, _, _) = authService.getExchangeToken(
            IdSdkConst.AUDIENCE_SPS,
            spsScopes
        )
        return exchangeToken?.value
    }

    fun getExchangeToken() {
        viewModelScope.launch {
            val (exchangeToken, rzCookie, easyId) = authService.getExchangeToken(
                IdSdkConst.AUDIENCE_MISSION,
                setOf(IdSdkConst.SCOPES_MISSION)
            )
            rzCookie?.let { _rzCookieLiveData.postValue(it) }
            easyId?.let { _easyIdLiveData.postValue(it) }
            exchangeToken?.let { _exchangeTokenLiveData.postValue(it.value) }
        }
    }

    fun getAccessToken(context: Context, exchangeToken: String) {
        val request: JsonObjectRequest = object : JsonObjectRequest(Method.GET, IdSdkConst.TOKEN_API, null, Response.Listener {
            _tokenLiveData.postValue(it.getString("access_token"))
        }, Response.ErrorListener {
            Log.d("LoginViewModel", "getAccessToken error: $it")
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = exchangeToken
                return headers
            }
        }

        // Volley request policy, only one time request to avoid duplicate transaction
        request.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            // 0 means no retry
            0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
            1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        // Add the volley post request to the request queue
        Volley.newRequestQueue(context).add(request)
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