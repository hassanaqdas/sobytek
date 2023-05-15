package com.sobytek.erpsobytek.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.sobytek.erpsobytek.model.SampleResponse
import com.sobytek.erpsobytek.model.Stock
import com.sobytek.erpsobytek.retrofit.ApiRepository

class StockViewModel : ViewModel() {

    private var stockReceiveResponse = MutableLiveData<ArrayList<Stock>?>()
    private var stockReceiveUpdateResponse = MutableLiveData<JsonObject?>()
    private var stockIssueUpdateResponse = MutableLiveData<JsonObject?>()
    private var stockGirsResponse = MutableLiveData<ArrayList<Stock>?>()
    private var stockTraysResponse = MutableLiveData<ArrayList<Stock>?>()
    private var stockIssueAbleResponse = MutableLiveData<ArrayList<Stock>?>()

    fun callStockReceiveDetails(context: Context) {
        stockReceiveResponse = ApiRepository.getInstance(context).stockReceiveDetails()
    }

    fun getStockReceiveDetailsResponse(): MutableLiveData<ArrayList<Stock>?> {
        return stockReceiveResponse
    }

    fun callStockReceiveUpdate(
        context: Context, doc_id: String,
        doc_year: String,
        doc_type: String,
        quantity: String,
        rate: String,
        sc_id: String,
        user_id: String,
        store_id: String,
        rack_id: String,
        tray_id: String
    ) {
        stockReceiveUpdateResponse =
            ApiRepository.getInstance(context).stockReceiveUpdate(doc_id,
                doc_year,
                doc_type,
                quantity,
                rate,
                sc_id,
                user_id,
                store_id,
                rack_id,
                tray_id)
    }

    fun getStockReceiveUpdateResponse(): MutableLiveData<JsonObject?> {
        return stockReceiveUpdateResponse
    }

    fun callStockIssueAbleDetails(context: Context) {
        stockIssueAbleResponse = ApiRepository.getInstance(context).stockIssueAbleDetails()
    }

    fun getStockIssueAbleDetailsResponse(): MutableLiveData<ArrayList<Stock>?> {
        return stockIssueAbleResponse
    }

    fun callStockGirsDetails(context: Context,sc_id: String) {
        stockGirsResponse = ApiRepository.getInstance(context).stockGirsDetails(sc_id)
    }

    fun getStockGirsDetailsResponse(): MutableLiveData<ArrayList<Stock>?> {
        return stockGirsResponse
    }

    fun callStockTraysDetails(context: Context,doc_id: String,doc_year: String,doc_type: String) {
        stockTraysResponse = ApiRepository.getInstance(context).stockTraysDetails(doc_id,doc_year,doc_type)
    }

    fun getStockTraysDetailsResponse(): MutableLiveData<ArrayList<Stock>?> {
        return stockTraysResponse
    }

    fun callStockIssueUpdate(
        context: Context, doc_id: String,
        doc_year: String,
        doc_type: String,
        quantity: String,
        rate: String,
        sc_id: String,
        user_id: String,
        store_id: String,
        rack_id: String,
        tray_id: String,
        lot_id:String
    ) {
        stockIssueUpdateResponse =
            ApiRepository.getInstance(context).stockIssueUpdate(doc_id,
                doc_year,
                doc_type,
                quantity,
                rate,
                sc_id,
                user_id,
                store_id,
                rack_id,
                tray_id,lot_id)
    }

    fun getStockIssueUpdateResponse(): MutableLiveData<JsonObject?> {
        return stockIssueUpdateResponse
    }
}