package com.sobytek.erpsobytek.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.sobytek.erpsobytek.model.SampleResponse
import com.sobytek.erpsobytek.retrofit.ApiRepository

class SampleReceiveViewModel :ViewModel() {

    private var sampleReceiveResponse = MutableLiveData<SampleResponse?>()
    private var sampleReceiveUpdateResponse = MutableLiveData<JsonObject?>()

    fun callSampleReceiveDetails(context: Context,sampleId:Int,user_id: String,passo: String) {
        sampleReceiveResponse = ApiRepository.getInstance(context).sampleReceiveDetails(sampleId,user_id,passo)
    }

    fun getSampleReceiveDetailsResponse(): MutableLiveData<SampleResponse?> {
        return sampleReceiveResponse
    }

    fun callSampleReceiveUpdate(context: Context,sampleTransId:Int,userId:String,passo: String) {
        sampleReceiveUpdateResponse = ApiRepository.getInstance(context).sampleReceiveUpdaste(sampleTransId,userId,passo)
    }

    fun getSampleReceiveUpdateResponse(): MutableLiveData<JsonObject?> {
        return sampleReceiveUpdateResponse
    }

}