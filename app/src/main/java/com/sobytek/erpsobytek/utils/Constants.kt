package com.sobytek.erpsobytek.utils

import com.sobytek.erpsobytek.model.Stock
import com.sobytek.erpsobytek.model.StockObject
import com.sobytek.erpsobytek.model.User

class Constants {

    companion object{
        val splashDelayTime:Long = 2000
//        var ip:String = "192.168.10.25"
//        var port:String = "8080"
//        val baseUrl:String = "http://${ip}:${port}/sobytek/api/" // server=> 192.168.10.25:8080
        var user:User?=null
        const val READ_STORAGE_REQUEST_CODE = 100
        const val CAMERA_REQUEST_CODE = 101
        const val REQUEST_BIOMETRIC_ENROLL_CODE = 102
        const val READ_STORAGE_PERMISSION = "android.permission.READ_EXTERNAL_STORAGE"
        const val CAMERA_PERMISSION = "android.permission.CAMERA"
        var stockIssueReqObject:Stock?=null
        var acceptedTraysDetailList = mutableListOf<StockObject>()
        var tempQuantity = 0
        var buttonAccessList = mutableListOf<Pair<String,String>>()
    }

}