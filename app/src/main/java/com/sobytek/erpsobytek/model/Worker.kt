package com.sobytek.erpsobytek.model

import com.google.gson.annotations.SerializedName

data class Worker(
    @SerializedName("ADDRESS")
    val ADDRESS: String,
    @SerializedName("CELL")
    val CELL: String,
    @SerializedName("COA_ID")
    val COA_ID: Any,
    @SerializedName("COA_MAIN_ID")
    val COA_MAIN_ID: Any,
    @SerializedName("COA_SUB_ID")
    val COA_SUB_ID: Any,
    @SerializedName("DOB")
    val DOB: Any,
    @SerializedName("DOJ")
    val DOJ: Any,
    @SerializedName("EMPLOYEE_ID")
    val EMPLOYEE_ID: Int?,
    @SerializedName("FATHER_NAME")
    val FATHER_NAME: Any,
    @SerializedName("FDATETIME")
    val FDATETIME: String,
    @SerializedName("NAME")
    val NAME: String,
    @SerializedName("PHOTO")
    val PHOTO: Any,
    @SerializedName("STATUS_ID")
    val STATUS_ID: String,
    @SerializedName("SUPPLIER_ID")
    val SUPPLIER_ID: String,
    @SerializedName("USER_ID")
    val USER_ID: String,
    @SerializedName("WORKER_ID")
    val WORKER_ID: String?
)