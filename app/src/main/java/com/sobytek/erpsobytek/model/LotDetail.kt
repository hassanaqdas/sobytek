package com.sobytek.erpsobytek.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LotDetail(
    @SerializedName("CDP_SEQ")
    val CDP_SEQ: String,
    @SerializedName("CPOSITION")
    val CPOSITION: String,
    @SerializedName("CUSTOMER_CODE")
    val CUSTOMER_CODE: String?,
    @SerializedName("CUSTOMER_ITEM_DESC")
    val CUSTOMER_ITEM_DESC: String?,
    @SerializedName("EMPLOYEE_ID")
    val EMPLOYEE_ID: String,
    @SerializedName("FDATETIME")
    val FDATETIME: String,
    @SerializedName("ISSUE_DATETIME")
    val ISSUE_DATETIME: String,
    @SerializedName("ISSUE_QTY")
    var ISSUE_QTY: String?=null,
    @SerializedName("ISSUE_USER_ID")
    val ISSUE_USER_ID: String,
    @SerializedName("ITEM_DESCRIPTION")
    val ITEM_DESCRIPTION: String,
    @SerializedName("JC_ID")
    val JC_ID: Any,
    @SerializedName("JC_STYLE_ID")
    val JC_STYLE_ID: String,
    @SerializedName("JC_STYLE_ID2")
    val JC_STYLE_ID2: String,
    @SerializedName("JC_STYLE_ID2_1")
    val JC_STYLE_ID2_1: Any,
    @SerializedName("LOT")
    val LOT: String,
    @SerializedName("LOT_ID")
    val LOT_ID: String,
    @SerializedName("MGIR_ID")
    val MGIR_ID: Any,
    @SerializedName("MGIR_YEAR")
    val MGIR_YEAR: Any,
    @SerializedName("NAME")
    var NAME: String,
    @SerializedName("OPERATION_ID")
    val OPERATION_ID: String,
    @SerializedName("OPERATION_NO")
    val OPERATION_NO: String,
    @SerializedName("ORDER_QTY")
    val ORDER_QTY: String?,
    @SerializedName("PAYABLE")
    val PAYABLE: String,
    @SerializedName("PRIORITY")
    val PRIORITY: String,
    @SerializedName("PRODUCTION_INS")
    val PRODUCTION_INS: Any,
    @SerializedName("RATE")
    val RATE: Any,
    @SerializedName("REC_DATETIME")
    val REC_DATETIME: String,
    @SerializedName("REC_QTY")
    val REC_QTY: String,
    @SerializedName("REC_USER_ID")
    val REC_USER_ID: String,
    @SerializedName("REJ_QTY")
    val REJ_QTY: Any,
    @SerializedName("REMARKS")
    val REMARKS: Any,
    @SerializedName("REWORK")
    val REWORK: Any,
    @SerializedName("SC1_ID")
    val SC1_ID: String,
    @SerializedName("SC2_ID")
    val SC2_ID: String,
    @SerializedName("SC3_ID")
    val SC3_ID: String,
    @SerializedName("SC4_ID")
    val SC4_ID: String,
    @SerializedName("SC5_ID")
    val SC5_ID: String,
    @SerializedName("SC5_ID_1")
    val SC5_ID_1: String,
    @SerializedName("SC_ID")
    val SC_ID: String,
    @SerializedName("SPECIAL_INS")
    val SPECIAL_INS: Any?,
    @SerializedName("SRC_ITEM_DESCRIPTION")
    val SRC_ITEM_DESCRIPTION: Any,
    @SerializedName("SRC_LOT_ID")
    val SRC_LOT_ID: Any,
    @SerializedName("SRC_REMARKS")
    val SRC_REMARKS: String,
    @SerializedName("SRC_STORE_CODE")
    val SRC_STORE_CODE: Any,
    @SerializedName("STAGE_NAME")
    val STAGE_NAME: String,
    @SerializedName("STATUS_ID")
    val STATUS_ID: String,
    @SerializedName("STORE_CODE")
    val STORE_CODE: String,
    @SerializedName("status")
    val STORE_CODE_1: String,
    @SerializedName("SUPPLIER_ID")
    val SUPPLIER_ID: Any,
    @SerializedName("SUPPLIER_NAME")
    val SUPPLIER_NAME: String,
    @SerializedName("TAKEN_QTY")
    val TAKEN_QTY: String,
    @SerializedName("UNIT_ID")
    val UNIT_ID: String,
    @SerializedName("USER_ID")
    val USER_ID: String,
    @SerializedName("WORKER_ID")
    val WORKER_ID: String,
    @SerializedName("YGP")
    val YGP: String,
    @SerializedName("lot_id")
    val lot_id:String,
    @SerializedName("op_no")
    val op_no:String,
    @SerializedName("issue_qty")
    val issue_qty:String,
    @SerializedName("op_id")
    val op_id:String
):Serializable{

}