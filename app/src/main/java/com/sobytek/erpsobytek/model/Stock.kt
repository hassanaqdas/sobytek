package com.sobytek.erpsobytek.model

data class Stock(
    val CAT_DESC: String,
    val DOC_DATE: String,
    val DOC_ID: String,
    val DOC_NO: String,
    val DOC_TYPE: String,
    val DOC_YEAR: String,
    val QUANTITY: String,
    val RATE: String,
    val SC_ID: String,
    val STORE_CODE: String,
    val SUPPLIER_ID: String,
    val SUPPLIER_NAME: String,
    val USER_ID: String,
    val STORE_ID: String,
    val RACK_ID: String,
    val TRAY_ID: String
) : java.io.Serializable {
    constructor() : this("", "", "", "", "", "", "", "", "", "", "", "", "","","","")
}