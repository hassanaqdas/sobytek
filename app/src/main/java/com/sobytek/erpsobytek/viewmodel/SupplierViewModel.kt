package com.sobytek.erpsobytek.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.sobytek.erpsobytek.model.Supplier
import com.sobytek.erpsobytek.retrofit.ApiRepository

class SupplierViewModel : ViewModel() {

    private var suppliersResponse = MutableLiveData<List<Supplier>?>()
    private var supplierLotIssueResponse = MutableLiveData<JsonObject?>()
    fun callSuppliers(context: Context, user_id: String, passo: String) {
        suppliersResponse = ApiRepository.getInstance(context).suppliers(user_id,passo)
    }

    fun getSuppliersResponse(): MutableLiveData<List<Supplier>?> {
        return suppliersResponse
    }

    fun callSupplierLotIssue(context: Context,supplier_id:Int,user_id:String,passo: String,lot_id:Int,op_no:Int){
        supplierLotIssueResponse = ApiRepository.getInstance(context).supplierLotIssue(supplier_id,user_id,passo,lot_id,op_no)
    }

    fun getSupplierLotIssueResponse():MutableLiveData<JsonObject?>{
        return supplierLotIssueResponse
    }
}