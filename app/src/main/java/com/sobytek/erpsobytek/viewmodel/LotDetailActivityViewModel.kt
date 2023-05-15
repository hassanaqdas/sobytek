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

    fun callLot(context: Context, lot_id: Int) {
        lotResponse = ApiRepository.getInstance(context).lot(lot_id)
    }

    fun getLotResponse(): MutableLiveData<Lot?> {
        return lotResponse
    }

    fun callWorkers(context: Context,user_id: String,op_id: String) {
        workersResponse = ApiRepository.getInstance(context).workers(user_id,op_id)
    }

    fun getWorkersResponse(): MutableLiveData<ArrayList<Worker>?> {
        return workersResponse
    }

    fun callLotIssue(context: Context,worker_id:Int,user_id:String,lot_id:Int,op_no:Int){
        lotIssueResponse = ApiRepository.getInstance(context).lotIssue(worker_id,user_id,lot_id,op_no)
    }

    fun getLotIssueResponse():MutableLiveData<JsonObject?>{
        return lotIssueResponse
    }

}