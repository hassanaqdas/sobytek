package com.sobytek.erpsobytek.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Supplier(
    @SerializedName("SUPPLIER_ID")
    val SUPPLIER_ID: String,
    @SerializedName("SUPPLIER_NAME")
    val SUPPLIER_NAME: String
):Serializable{
    constructor():this("","")
}