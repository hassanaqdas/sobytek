package com.sobytek.erpsobytek.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class IgpDetail(
    @SerializedName("GIR_ID")
    val GIR_ID: String,
    @SerializedName("GIR_YEAR")
    val GIR_YEAR: String?,
    @SerializedName("OPERATION_ID")
    val OPERATION_ID: String?,
    @SerializedName("NAME")
    var NAME: String?,
    @SerializedName("ISSUE_DATETIME")
    val ISSUE_DATETIME: String?,
    @SerializedName("REJ_QTY")
    val REJ_QTY: String,
    @SerializedName("REWORK_QTY")
    val REWORK_QTY: String,
    @SerializedName("REC_DATETIME")
    val REC_DATETIME: String,
    @SerializedName("EMPLOYEE_ID")
    var EMPLOYEE_ID: String?=null,
    @SerializedName("ISSUE_USER_ID")
    val ISSUE_USER_ID: String,
    @SerializedName("REC_USER_ID")
    val REC_USER_ID: String,
    @SerializedName("OPERATION_NO")
    val OPERATION_NO: String?,
    @SerializedName("ISSUE_QTY")
    var ISSUE_QTY: String?,
    @SerializedName("REC_QTY")
    val REC_QTY: String,
    @SerializedName("TAKEN_QTY")
    val TAKEN_QTY: String?,
    @SerializedName("CPOSITION")
    val CPOSITION: String,
    @SerializedName("CUSTOMER_CODE")
    val CUSTOMER_CODE: String?,
    @SerializedName("CUSTOMER_ITEM_DESC")
    val CUSTOMER_ITEM_DESC: String?,
    @SerializedName("ITEM_DESCRIPTION")
    val ITEM_DESCRIPTION: String,
    @SerializedName("ORDER_QTY")
    val ORDER_QTY: String?,
    @SerializedName("STORE_CODE")
    val STORE_CODE: String,
    @SerializedName("SPECIAL_INS")
    val SPECIAL_INS: String?="None",
    @SerializedName("op_no")
    val op_no:String,
    @SerializedName("gir_id")
    val gir_id:String,
    @SerializedName("issue_qty")
    val issue_qty:String,
    @SerializedName("op_id")
    val op_id:String
):Serializable{

}