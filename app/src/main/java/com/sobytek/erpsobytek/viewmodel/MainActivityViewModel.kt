package com.sobytek.erpsobytek.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.sobytek.erpsobytek.model.Lot
import com.sobytek.erpsobytek.retrofit.ApiRepository

class MainActivityViewModel : ViewModel() {

    private var lotResponse = MutableLiveData<Lot?>()
    private var lotReceiveResponse = MutableLiveData<JsonObject?>()

    fun callLot(context: Context, lot_id: Int) {
        lotResponse = ApiRepository.getInstance(context).lot(lot_id)
    }

    fun getLotResponse(): MutableLiveData<Lot?> {
        return lotResponse
    }

    fun callLotReceive(context: Context,lot_id:Int,rec_qty:Int,user_id: String,rej_qty:Int,op_no:Int,remarks:String){
        lotReceiveResponse = ApiRepository.getInstance(context).lotReceive(lot_id,rec_qty,user_id,rej_qty,op_no,remarks)
    }

    fun getLotReceiveResponse():MutableLiveData<JsonObject?>{
        return lotReceiveResponse
    }

}