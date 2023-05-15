package com.sobytek.erpsobytek.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Lot(
    @SerializedName("status")
    val status: Int,
    @SerializedName("operation")
    val operation:String,
    @SerializedName("detail")
    val lotDetail: ArrayList<LotDetail>
):Serializable