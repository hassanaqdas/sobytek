package com.sobytek.erpsobytek.retrofit

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.sobytek.erpsobytek.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiRepository {

    private var apiInterface: ApiServices? = null
    companion object {
        private var apiRepository: ApiRepository? = null
        fun getInstance(mContext: Context): ApiRepository {

            if (apiRepository == null) {
                apiRepository = ApiRepository()
            }
            return apiRepository!!
        }
    }

    fun setBaseUrl(url: String) {
        RetrofitClientApi.setBaseUrl(url)
        apiInterface = RetrofitClientApi.createService(ApiServices::class.java)
    }

    // THIS IS THE USER LOGIN FUNCTION
    fun login(user_id: String, passo: String): MutableLiveData<JsonObject?> {
        val res = MutableLiveData<JsonObject?>()

        apiInterface!!.login(user_id, passo).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                res.postValue(response.body())
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                res.postValue(null)
            }
        })
        return res
    }

    fun userAccessButtons(user_id: String,passo: String): MutableLiveData<JsonObject?> {
        val res = MutableLiveData<JsonObject?>()

        apiInterface!!.userAccessButtons(user_id,passo).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                res.postValue(response.body())
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                res.postValue(null)
            }
        })
        return res
    }

    // THIS IS THE LOT DETAIL FUNCTION
    fun lot(lot_id: Int,user_id: String,passo: String): MutableLiveData<Lot?> {
        val res = MutableLiveData<Lot?>()

        apiInterface!!.lot(lot_id,user_id,passo).enqueue(object : Callback<Lot> {
            override fun onResponse(call: Call<Lot>, response: Response<Lot>) {
                res.postValue(response.body())
            }

            override fun onFailure(call: Call<Lot>, t: Throwable) {
                res.postValue(null)
            }
        })
        return res
    }

    fun supplierLot(lot_id: Int,user_id: String,passo: String): MutableLiveData<Lot?> {
        val res = MutableLiveData<Lot?>()

        apiInterface!!.supplierLot(lot_id,user_id,passo).enqueue(object : Callback<Lot> {
            override fun onResponse(call: Call<Lot>, response: Response<Lot>) {
                res.postValue(response.body())
            }

            override fun onFailure(call: Call<Lot>, t: Throwable) {
                res.postValue(null)
            }
        })
        return res
    }

    fun suppliers(user_id: String,passo: String): MutableLiveData<List<Supplier>?> {
        val res = MutableLiveData<List<Supplier>?>()

        apiInterface!!.suppliers(user_id,passo).enqueue(object : Callback<List<Supplier>> {
            override fun onResponse(call: Call<List<Supplier>>, response: Response<List<Supplier>>) {
                res.postValue(response.body())
            }

            override fun onFailure(call: Call<List<Supplier>>, t: Throwable) {
                res.postValue(null)
            }
        })
        return res
    }

    fun igp(igp_id: String,user_id: String,passo: String): MutableLiveData<Igp?> {
        val res = MutableLiveData<Igp?>()

        apiInterface!!.igp(igp_id,user_id,passo).enqueue(object : Callback<Igp> {
            override fun onResponse(call: Call<Igp>, response: Response<Igp>) {
                res.postValue(response.body())
            }

            override fun onFailure(call: Call<Igp>, t: Throwable) {
                res.postValue(null)
            }
        })
        return res
    }

    fun igpIssue(
        worker_id: Int,
        user_id: String,
        passo: String,
        gir_id: String,
        gir_year: String,
        op_no: Int
    ): MutableLiveData<JsonObject?> {
        val res = MutableLiveData<JsonObject?>()

        apiInterface!!.igpIssue(worker_id, user_id,passo, gir_id, gir_year, op_no)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    res.postValue(response.body())
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    res.postValue(null)
                }
            })
        return res
    }

    // THIS IS THE WORKER LIST FUNCTION
    fun workers(user_id: String,passo: String, op_id: String): MutableLiveData<ArrayList<Worker>?> {
        val res = MutableLiveData<ArrayList<Worker>?>()

        apiInterface!!.workers(user_id,passo, op_id).enqueue(object : Callback<ArrayList<Worker>> {
            override fun onResponse(
                call: Call<ArrayList<Worker>>,
                response: Response<ArrayList<Worker>>
            ) {
                res.postValue(response.body())
            }

            override fun onFailure(call: Call<ArrayList<Worker>>, t: Throwable) {
                res.postValue(null)
            }
        })
        return res
    }

    // THIS IS THE LOT ISSUE FUNCTION
    fun lotIssue(
        worker_id: Int,
        user_id: String,
        passo: String,
        lot_id: Int,
        op_no: Int
    ): MutableLiveData<JsonObject?> {
        val res = MutableLiveData<JsonObject?>()

        apiInterface!!.lotIssue(worker_id, user_id,passo, lot_id, op_no)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    res.postValue(response.body())
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    res.postValue(null)
                }
            })
        return res
    }

    fun supplierLotIssue(
        supplier_id: Int,
        user_id: String,
        passo: String,
        lot_id: Int,
        op_no: Int
    ): MutableLiveData<JsonObject?> {
        val res = MutableLiveData<JsonObject?>()

        apiInterface!!.supplierLotIssue(supplier_id, user_id,passo, lot_id, op_no)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    res.postValue(response.body())
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    res.postValue(null)
                }
            })
        return res
    }

    // THIS IS THE LOT ISSUE FUNCTION
    fun lotReceive(
        lot_id: Int,
        rec_qty: Int,
        user_id: String,
        passo: String,
        rej_qty: Int,
        op_no: Int,
        remarks: String,
        rework_qty: Int,
        fgrr_qty: Int,
    ): MutableLiveData<JsonObject?> {
        val res = MutableLiveData<JsonObject?>()

        apiInterface!!.lotReceive(lot_id,rec_qty, user_id,passo, rej_qty, op_no, remarks,rework_qty, fgrr_qty)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    res.postValue(response.body())
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    res.postValue(null)
                }
            })
        return res
    }

    fun supplierLotReceive(
        lot_id: Int,
        rec_qty: Int,
        user_id: String,
        passo: String,
        rej_qty: Int,
        op_no: Int,
        remarks: String,
        rework_qty: Int,
        fgrr_qty: Int,
    ): MutableLiveData<JsonObject?> {
        val res = MutableLiveData<JsonObject?>()

        apiInterface!!.supplierLotReceive(lot_id,rec_qty, user_id,passo, rej_qty, op_no, remarks,rework_qty, fgrr_qty)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    res.postValue(response.body())
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    res.postValue(null)
                }
            })
        return res
    }

    fun igpReceive(
        gir_id: String,
        rec_qty: Int,
        user_id: String,
        passo: String,
        rej_qty: Int,
        op_no: Int,
        remarks: String
    ): MutableLiveData<JsonObject?> {
        val res = MutableLiveData<JsonObject?>()

        apiInterface!!.igpReceive(gir_id, rec_qty, user_id,passo, rej_qty, op_no, remarks)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    res.postValue(response.body())
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    res.postValue(null)
                }
            })
        return res
    }

    // THIS IS THE SAMPLE LOCATION DETAIL FUNCTION
    fun sampleLocationDetail(rack_id: String,user_id: String,passo: String): MutableLiveData<Samples?> {
        val res = MutableLiveData<Samples?>()

        apiInterface!!.sampleLocationDetail(rack_id,user_id,passo).enqueue(object : Callback<Samples> {
            override fun onResponse(call: Call<Samples>, response: Response<Samples>) {
                res.postValue(response.body())
            }

            override fun onFailure(call: Call<Samples>, t: Throwable) {
                res.postValue(null)
            }
        })
        return res
    }

    // THIS IS THE GET TRAY INFO FUNCTION
    fun getTrayInfo(tray_id:String,user_id: String,passo: String): MutableLiveData<ArrayList<TrayInfo>?> {
        val res = MutableLiveData<ArrayList<TrayInfo>?>()

        apiInterface!!.getTrayInfo(tray_id,user_id,passo).enqueue(object : Callback<ArrayList<TrayInfo>> {
            override fun onResponse(
                call: Call<ArrayList<TrayInfo>>,
                response: Response<ArrayList<TrayInfo>>
            ) {
                res.postValue(response.body())
            }

            override fun onFailure(call: Call<ArrayList<TrayInfo>>, t: Throwable) {
                res.postValue(null)
            }
        })
        return res
    }

    // THIS IS THE UPDATE TRAY INFO FUNCTION
    fun updateTrayInfo(trans_id:String,tray_id:String,store_id:String,rack_id:String,user_id: String,passo: String): MutableLiveData<JsonObject?> {
        val res = MutableLiveData<JsonObject?>()

        apiInterface!!.updateTrayInfo(trans_id,tray_id,store_id,rack_id,user_id,passo).enqueue(object : Callback<JsonObject> {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                res.postValue(response.body())
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                res.postValue(null)
            }
        })
        return res
    }

    // THIS IS THE SAMPLE LOCATION DETAIL FUNCTION
    fun updateLocationDetail(
        rack_id: String,
        sample_id: String,
        user_id: String,
        passo: String
    ): MutableLiveData<JsonObject?> {
        val res = MutableLiveData<JsonObject?>()

        apiInterface!!.updateLocationDetail(rack_id, sample_id, user_id,passo)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    res.postValue(response.body())
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    res.postValue(null)
                }
            })
        return res
    }

    fun sampleIssuenceDetails(user_id: String,passo: String): MutableLiveData<SampleResponse?> {
        val res = MutableLiveData<SampleResponse?>()

        apiInterface!!.sampleIssuenceDetails(user_id,passo).enqueue(object : Callback<SampleResponse> {
            override fun onResponse(
                call: Call<SampleResponse>,
                response: Response<SampleResponse>
            ) {
                res.postValue(response.body())
            }

            override fun onFailure(call: Call<SampleResponse>, t: Throwable) {
                res.postValue(null)
            }
        })
        return res
    }

    fun sampleIssuenceUpdate(sampleTransId: Int, userId: String,passo: String): MutableLiveData<JsonObject?> {
        val res = MutableLiveData<JsonObject?>()

        apiInterface!!.sampleIssuenceUpdate(sampleTransId, userId,passo)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    res.postValue(response.body())
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    res.postValue(null)
                }
            })
        return res
    }

    fun sampleReceiveDetails(sampleId: Int,user_id: String,passo: String): MutableLiveData<SampleResponse?> {
        val res = MutableLiveData<SampleResponse?>()

        apiInterface!!.sampleReceiveDetails(sampleId,user_id,passo).enqueue(object : Callback<SampleResponse> {
            override fun onResponse(
                call: Call<SampleResponse>,
                response: Response<SampleResponse>
            ) {
                res.postValue(response.body())
            }

            override fun onFailure(call: Call<SampleResponse>, t: Throwable) {
                res.postValue(null)
            }
        })
        return res
    }

    fun sampleReceiveUpdaste(sampleTransId: Int, userId: String,passo: String): MutableLiveData<JsonObject?> {
        val res = MutableLiveData<JsonObject?>()

        apiInterface!!.sampleReceiveUpdate(sampleTransId, userId,passo)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    res.postValue(response.body())
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    res.postValue(null)
                }
            })
        return res
    }

    fun stockReceiveDetails(user_id: String,passo: String): MutableLiveData<ArrayList<Stock>?> {
        val res = MutableLiveData<ArrayList<Stock>?>()

        apiInterface!!.stockReceiveDetails(user_id,passo).enqueue(object : Callback<ArrayList<Stock>> {
            override fun onResponse(
                call: Call<ArrayList<Stock>>,
                response: Response<ArrayList<Stock>>
            ) {
                res.postValue(response.body())
            }

            override fun onFailure(call: Call<ArrayList<Stock>>, t: Throwable) {
                res.postValue(null)
            }
        })
        return res
    }

    fun stockReceiveUpdate(
        doc_id: String,
        doc_year: String,
        doc_type: String,
        quantity: String,
        rate: String,
        sc_id: String,
        user_id: String,
        passo: String,
        store_id: String,
        rack_id: String,
        tray_id: String
    ): MutableLiveData<JsonObject?> {
        val res = MutableLiveData<JsonObject?>()

        apiInterface!!.stockReceiveUpdate(
            doc_id,
            doc_year,
            doc_type,
            quantity,
            rate,
            sc_id,
            user_id,
            passo,
            store_id,
            rack_id,
            tray_id
        )
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    res.postValue(response.body())
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    res.postValue(null)
                }
            })
        return res
    }

    fun stockIssueAbleDetails(user_id: String,passo: String): MutableLiveData<ArrayList<Stock>?> {
        val res = MutableLiveData<ArrayList<Stock>?>()

        apiInterface!!.stockIssueAbleDetails(user_id,passo).enqueue(object : Callback<ArrayList<Stock>> {
            override fun onResponse(
                call: Call<ArrayList<Stock>>,
                response: Response<ArrayList<Stock>>
            ) {
                res.postValue(response.body())
            }

            override fun onFailure(call: Call<ArrayList<Stock>>, t: Throwable) {
                res.postValue(null)
            }
        })
        return res
    }

    fun stockGirsDetails(sc_id: String,user_id: String,passo: String): MutableLiveData<ArrayList<Stock>?> {
        val res = MutableLiveData<ArrayList<Stock>?>()

        apiInterface!!.stockGirsDetails(sc_id,user_id,passo).enqueue(object : Callback<ArrayList<Stock>> {
            override fun onResponse(
                call: Call<ArrayList<Stock>>,
                response: Response<ArrayList<Stock>>
            ) {
                res.postValue(response.body())
            }

            override fun onFailure(call: Call<ArrayList<Stock>>, t: Throwable) {
                res.postValue(null)
            }
        })
        return res
    }

    fun stockTraysDetails(doc_id: String,doc_year: String,doc_type: String,user_id: String,passo: String): MutableLiveData<ArrayList<Stock>?> {
        val res = MutableLiveData<ArrayList<Stock>?>()

        apiInterface!!.stockTraysDetails(doc_id,doc_year,doc_type,user_id,passo).enqueue(object : Callback<ArrayList<Stock>> {
            override fun onResponse(
                call: Call<ArrayList<Stock>>,
                response: Response<ArrayList<Stock>>
            ) {
                res.postValue(response.body())
            }

            override fun onFailure(call: Call<ArrayList<Stock>>, t: Throwable) {
                res.postValue(null)
            }
        })
        return res
    }

    fun stockIssueUpdate(
        doc_id: String,
        doc_year: String,
        doc_type: String,
        quantity: String,
        rate: String,
        sc_id: String,
        user_id: String,
        passo: String,
        store_id: String,
        rack_id: String,
        tray_id: String,
        lot_id: String
    ): MutableLiveData<JsonObject?> {
        val res = MutableLiveData<JsonObject?>()

        apiInterface!!.stockIssueUpdate(
            doc_id,
            doc_year,
            doc_type,
            quantity,
            rate,
            sc_id,
            user_id,
            passo,
            store_id,
            rack_id,
            tray_id,
            lot_id
        )
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    res.postValue(response.body())
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    res.postValue(null)
                }
            })
        return res
    }
}