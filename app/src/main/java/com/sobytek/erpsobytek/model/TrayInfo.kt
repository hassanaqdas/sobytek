package com.sobytek.erpsobytek.model

import java.io.Serializable

data class TrayInfo(
    val CAT_DESC: String,
    val DOC_NO: String,
    val QTY: String,
    val RACK_ID: String,
    val SC5_ID: String,
    val SC_ID: String,
    val STORE_CODE: String,
    val STORE_ID: String,
    val TRANS_ID: String,
    val TRAY_ID: String
):Serializable{
    constructor():this("","","","","","","","","","")
}