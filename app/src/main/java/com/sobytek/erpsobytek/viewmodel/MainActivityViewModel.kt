package com.sobytek.erpsobytek.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.sobytek.erpsobytek.model.Lot
import com.sobytek.erpsobytek.retrofit.ApiRepository

class MainActivityViewModel : ViewModel() {

    private var lotResponse = MutableLiveData<Lot?>()
    private var supplierLotResponse = MutableLiveData<Lot?>()
    private var lotReceiveResponse = MutableLiveData<JsonObject?>()
    private var supplierLotReceiveResponse = MutableLiveData<JsonObject?>()

    fun callLot(context: Context, lot_id: Int,user_id: String,passo: String) {
        lotResponse = ApiRepository.getInstance(context).lot(lot_id,user_id,passo)
    }

    fun getLotResponse(): MutableLiveData<Lot?> {
        return lotResponse
    }

    fun callSupplierLot(context: Context, lot_id: Int,user_id: String,passo: String) {
        supplierLotResponse = ApiRepository.getInstance(context).supplierLot(lot_id,user_id,passo)
    }

    fun getSupplierLotResponse(): MutableLiveData<Lot?> {
        return supplierLotResponse
    }

    fun callLotReceive(context: Context,lot_id:Int,rec_qty:Int,user_id: String,passo: String,rej_qty:Int,op_no:Int,remarks:String,rework_qty:Int,fgrr_qty:Int){
        lotReceiveResponse = ApiRepository.getInstance(context).lotReceive(lot_id,rec_qty,user_id,passo,rej_qty,op_no,remarks,rework_qty,fgrr_qty)
    }

    fun getLotReceiveResponse():MutableLiveData<JsonObject?>{
        return lotReceiveResponse
    }

    fun callSupplierLotReceive(context: Context,lot_id:Int,rec_qty:Int,user_id: String,passo: String,rej_qty:Int,op_no:Int,remarks:String,rework_qty:Int,fgrr_qty:Int){
        supplierLotReceiveResponse = ApiRepository.getInstance(context).supplierLotReceive(lot_id,rec_qty,user_id,passo,rej_qty,op_no,remarks,rework_qty,fgrr_qty)
    }

    fun getSupplierLotReceiveResponse():MutableLiveData<JsonObject?>{
        return supplierLotReceiveResponse
    }

}