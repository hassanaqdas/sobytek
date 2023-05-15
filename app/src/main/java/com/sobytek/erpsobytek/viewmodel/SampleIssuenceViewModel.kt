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

    fun callSampleIssueDetails(context: Context) {
        sampleIssueResponse = ApiRepository.getInstance(context).sampleIssuenceDetails()
    }

    fun getSampleIssueDetailsResponse(): MutableLiveData<SampleResponse?> {
        return sampleIssueResponse
    }

    fun callSampleIssueUpdate(context: Context,sampleTransId:Int,userId:String) {
        sampleIssueUpdateResponse = ApiRepository.getInstance(context).sampleIssuenceUpdate(sampleTransId,userId)
    }

    fun getSampleIssueUpdateResponse(): MutableLiveData<JsonObject?> {
        return sampleIssueUpdateResponse
    }

}