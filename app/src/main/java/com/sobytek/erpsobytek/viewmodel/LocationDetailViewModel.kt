package com.sobytek.erpsobytek.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.sobytek.erpsobytek.model.Samples
import com.sobytek.erpsobytek.retrofit.ApiRepository

class LocationDetailViewModel: ViewModel() {

    private var locationDetailResponse = MutableLiveData<Samples?>()
    private var updateLocationResponse = MutableLiveData<JsonObject?>()

    fun callSampleLocationDetail(context: Context, rack_id: String) {
        locationDetailResponse = ApiRepository.getInstance(context).sampleLocationDetail(rack_id)
    }

    fun getSampleLocationDetailResponse(): MutableLiveData<Samples?> {
        return locationDetailResponse
    }

    fun callUpdateLocationDetail(context: Context, rack_id: String,sample_id:String,user_id:String) {
        updateLocationResponse = ApiRepository.getInstance(context).updateLocationDetail(rack_id,sample_id,user_id)
    }

    fun getUpdateLocationDetailResponse(): MutableLiveData<JsonObject?> {
        return updateLocationResponse
    }

}