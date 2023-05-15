package com.sobytek.erpsobytek.retrofit

import com.google.gson.JsonObject
import com.sobytek.erpsobytek.model.*
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiServices {

    @FormUrlEncoded
    @POST("login.php")
    fun login(@Field("user_id") user_id: String, @Field("passo") passo: String): Call<JsonObject>

    @FormUrlEncoded
    @POST("button_access.php")
    fun userAccessButtons(@Field("user_id") user_id: String): Call<JsonObject>

    @FormUrlEncoded
    @POST("lot.php")
    fun lot(@Field("lot_id") lot_id: Int): Call<Lot>

    @FormUrlEncoded
    @POST("igp.php")
    fun igp(@Field("gir_id") gir_id: String): Call<Igp>

    @FormUrlEncoded
    @POST("igp_issue.php")
    fun igpIssue(
        @Field("worker_id") worker_id: Int,
        @Field("user_id") user_id: String,
        @Field("gir_id") gir_id: String,
        @Field("gir_year") gir_year: String,
        @Field("op_no") op_no: Int
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("igp_receive.php")
    fun igpReceive(
        @Field("gir_id") gir_id: String,
        @Field("rec_qty") rec_qty: Int,
        @Field("user_id") user_id: String,
        @Field("rej_qty") rej_qty: Int,
        @Field("op_no") op_no: Int,
        @Field("remarks") remarks: String
    ): Call<JsonObject>


    @FormUrlEncoded
    @POST("worker.php")
    fun workers(
        @Field("user_id") user_id: String,
        @Field("op_id") op_id: String
    ): Call<ArrayList<Worker>>

    @FormUrlEncoded
    @POST("lot_issue.php")
    fun lotIssue(
        @Field("worker_id") worker_id: Int,
        @Field("user_id") user_id: String,
        @Field("lot_id") lot_id: Int,
        @Field("op_no") op_no: Int
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("lot_receive.php")
    fun lotReceive(
        @Field("lot_id") lot_id: Int,
        @Field("rec_qty") rec_qty: Int,
        @Field("user_id") user_id: String,
        @Field("rej_qty") rej_qty: Int,
        @Field("op_no") op_no: Int,
        @Field("remarks") remarks: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("location_details.php")
    fun sampleLocationDetail(@Field("rack_id") rack_id: String): Call<Samples>

    @FormUrlEncoded
    @POST("update_location.php")
    fun updateLocationDetail(
        @Field("rack_id") rack_id: String,
        @Field("sample_id") sample_id: String,
        @Field("user_id") user_id: String
    ): Call<JsonObject>

    @GET("sample_issuence_details.php")
    fun sampleIssuenceDetails(): Call<SampleResponse>

    @FormUrlEncoded
    @POST("update_sample_issuence.php")
    fun sampleIssuenceUpdate(
        @Field("sample_trans_id") sample_trans_id: Int,
        @Field("user_id") user_id: String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("sample_rec_details.php")
    fun sampleReceiveDetails(@Field("sample_id") sample_id: Int): Call<SampleResponse>

    @FormUrlEncoded
    @POST("update_sample_rec.php")
    fun sampleReceiveUpdate(
        @Field("sample_trans_id") sample_trans_id: Int,
        @Field("user_id") user_id: String
    ): Call<JsonObject>

    @GET("get_receivables.php")
    fun stockReceiveDetails(): Call<ArrayList<Stock>>

    @FormUrlEncoded
    @POST("update_receivables.php")
    fun stockReceiveUpdate(
        @Field("doc_id") doc_id: String,
        @Field("doc_year") doc_year: String,
        @Field("doc_type") doc_type: String,
        @Field("quantity") quantity: String,
        @Field("rate") rate: String,
        @Field("sc_id") sc_id: String,
        @Field("user_id") user_id: String,
        @Field("store_id") store_id: String,
        @Field("rack_id") rack_id: String,
        @Field("tray_id") tray_id: String
    ): Call<JsonObject>

    @GET("get_issueables.php")
    fun stockIssueAbleDetails(): Call<ArrayList<Stock>>

    @FormUrlEncoded
    @POST("get_issueables_girs.php")
    fun stockGirsDetails(@Field("sc_id") sc_id: String): Call<ArrayList<Stock>>

    @FormUrlEncoded
    @POST("get_issueables_trays.php")
    fun stockTraysDetails(@Field("doc_id") doc_id: String,@Field("doc_year") doc_year: String,
                          @Field("doc_type") doc_type: String,): Call<ArrayList<Stock>>

    @FormUrlEncoded
    @POST("update_issueables.php")
    fun stockIssueUpdate(
        @Field("doc_id") doc_id: String,
        @Field("doc_year") doc_year: String,
        @Field("doc_type") doc_type: String,
        @Field("quantity") quantity: String,
        @Field("rate") rate: String,
        @Field("sc_id") sc_id: String,
        @Field("user_id") user_id: String,
        @Field("store_id") store_id: String,
        @Field("rack_id") rack_id: String,
        @Field("tray_id") tray_id: String,
        @Field("lot_id") lot_id: String
    ): Call<JsonObject>

}