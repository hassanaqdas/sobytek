package com.sobytek.erpsobytek.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.sobytek.erpsobytek.retrofit.ApiRepository

class LoginViewModel : ViewModel() {

    private var userLoginResponse = MutableLiveData<JsonObject?>()
    private var userAccessButtonsResponse = MutableLiveData<JsonObject?>()

    fun callLogin(context: Context, user_id: String, passo: String) {
        userLoginResponse = ApiRepository.getInstance(context).login(user_id, passo)
    }

    fun getLoginResponse(): MutableLiveData<JsonObject?> {
        return userLoginResponse
    }

    fun callUserAccessButtons(context: Context, user_id: String) {
        userAccessButtonsResponse = ApiRepository.getInstance(context).userAccessButtons(user_id)
    }

    fun getUserAccessButtonsResponse(): MutableLiveData<JsonObject?> {
        return userAccessButtonsResponse
    }

}