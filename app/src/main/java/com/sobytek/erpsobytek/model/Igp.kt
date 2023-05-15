package com.sobytek.erpsobytek.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Igp(
    @SerializedName("status")
    val status: Int,
    @SerializedName("operation")
    val operation:String,
    @SerializedName("detail")
    val igpDetail: ArrayList<IgpDetail>
):Serializable