package com.sobytek.erpsobytek.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.sobytek.erpsobytek.model.Igp
import com.sobytek.erpsobytek.model.Lot
import com.sobytek.erpsobytek.model.Worker
import com.sobytek.erpsobytek.retrofit.ApiRepository

class IGPActivityViewModel : ViewModel() {

    private var workersResponse = MutableLiveData<ArrayList<Worker>?>()
    private var ipgResponse = MutableLiveData<Igp?>()
    private var igpReceiveResponse = MutableLiveData<JsonObject?>()
    private var igpIssueResponse = MutableLiveData<JsonObject?>()

    fun callIgp(context: Context, gir_id: String) {
        ipgResponse = ApiRepository.getInstance(context).igp(gir_id)
    }

    fun getIgpResponse(): MutableLiveData<Igp?> {
        return ipgResponse
    }

    fun callWorkers(context: Context,user_id: String,op_id: String) {
        workersResponse = ApiRepository.getInstance(context).workers(user_id,op_id)
    }

    fun getWorkersResponse(): MutableLiveData<ArrayList<Worker>?> {
        return workersResponse
    }

    fun callIgpIssue(context: Context,worker_id:Int,user_id:String,gir_id: String,gir_year: String,op_no:Int){
        igpIssueResponse = ApiRepository.getInstance(context).igpIssue(worker_id,user_id,gir_id,gir_year,op_no)
    }

    fun getIgpIssueResponse():MutableLiveData<JsonObject?>{
        return igpIssueResponse
    }

    fun callIgpReceive(context: Context,gir_id:String,rec_qty:Int,user_id: String,rej_qty:Int,op_no:Int,remarks:String){
        igpReceiveResponse = ApiRepository.getInstance(context).igpReceive(gir_id,rec_qty,user_id,rej_qty,op_no,remarks)
    }

    fun getIgpReceiveResponse():MutableLiveData<JsonObject?>{
        return igpReceiveResponse
    }

}