package com.sobytek.erpsobytek.model

data class StockObject(
    val DOC_ID: String,
    val DOC_YEAR: String,
    val DOC_TYPE: String,
    val QUANTITY: String,
    val RATE: String,
    val SC_ID: String,
    val USER_ID: String,
    val STORE_ID: String,
    val RACK_ID: String,
    val TRAY_ID: String
):java.io.Serializable{

}