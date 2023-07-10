package com.sobytek.erpsobytek.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.sobytek.erpsobytek.model.Lot
import com.sobytek.erpsobytek.model.Worker
import com.sobytek.erpsobytek.retrofit.ApiRepository

class LotDetailActivityViewModel  : ViewModel() {

    private var workersResponse = MutableLiveData<ArrayList<Worker>?>()
    private var lotIssueResponse = MutableLiveData<JsonObject?>()
    private var lotResponse = MutableLiveData<Lot?>()

    fun callLot(context: Context, lot_id: Int,user_id: String,passo: String) {
        lotResponse = ApiRepository.getInstance(context).lot(lot_id,user_id,passo)
    }

    fun getLotResponse(): MutableLiveData<Lot?> {
        return lotResponse
    }

    fun callWorkers(context: Context,user_id: String,passo: String,op_id: String) {
        workersResponse = ApiRepository.getInstance(context).workers(user_id,passo,op_id)
    }

    fun getWorkersResponse(): MutableLiveData<ArrayList<Worker>?> {
        return workersResponse
    }

    fun callLotIssue(context: Context,worker_id:Int,user_id:String,passo: String,lot_id:Int,op_no:Int){
        lotIssueResponse = ApiRepository.getInstance(context).lotIssue(worker_id,user_id,passo,lot_id,op_no)
    }

    fun getLotIssueResponse():MutableLiveData<JsonObject?>{
        return lotIssueResponse
    }

}