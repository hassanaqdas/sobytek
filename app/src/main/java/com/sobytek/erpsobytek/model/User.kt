package com.sobytek.erpsobytek.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(
    @SerializedName("ADMIN")
    val ADMIN: String,
    @SerializedName("EMPLOYEE_ID")
    val EMPLOYEE_ID: String?="",
    @SerializedName("FDATETIME")
    val FDATETIME: String,
    @SerializedName("NAME")
    val NAME: String,
    @SerializedName("PASSO")
    val PASSO: String,
    @SerializedName("REMARKS")
    val REMARKS: String?="",
    @SerializedName("STATUS")
    val STATUS: String,
    @SerializedName("USER_ID")
    val USER_ID: String
):Serializable{

}