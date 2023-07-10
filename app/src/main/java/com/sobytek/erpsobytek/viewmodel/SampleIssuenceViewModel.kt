package com.sobytek.erpsobytek.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.sobytek.erpsobytek.model.SampleResponse
import com.sobytek.erpsobytek.retrofit.ApiRepository

class SampleIssuenceViewModel :ViewModel() {

    private var sampleIssueResponse = MutableLiveData<SampleResponse?>()
    private var sampleIssueUpdateResponse = MutableLiveData<JsonObject?>()

    fun callSampleIssueDetails(context: Context,user_id: String,passo: String) {
        sampleIssueResponse = ApiRepository.getInstance(context).sampleIssuenceDetails(user_id,passo)
    }

    fun getSampleIssueDetailsResponse(): MutableLiveData<SampleResponse?> {
        return sampleIssueResponse
    }

    fun callSampleIssueUpdate(context: Context,sampleTransId:Int,userId:String,passo: String) {
        sampleIssueUpdateResponse = ApiRepository.getInstance(context).sampleIssuenceUpdate(sampleTransId,userId,passo)
    }

    fun getSampleIssueUpdateResponse(): MutableLiveData<JsonObject?> {
        return sampleIssueUpdateResponse
    }

}