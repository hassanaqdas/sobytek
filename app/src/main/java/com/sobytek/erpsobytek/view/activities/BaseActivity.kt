package com.sobytek.erpsobytek.view.activities

import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.sobytek.erpsobytek.R
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseActivity : AppCompatActivity() {

       companion object{
           var alert: AlertDialog? = null

           // THIS FUNCTION WILL CHECK THE INTERNET CONNECTION AVAILABLE OR NOT
           fun isNetworkAvailable(context: Context): Boolean
           {
               val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                   val capabilities = connectivityManager.getNetworkCapabilities(
                       connectivityManager.activeNetwork
                   )
                   if (capabilities != null) {
                       when {
                           capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                               return true
                           }
                           capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                               return true
                           }
                           capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                               return true
                           }
                       }
                   }
               } else {
                   val activeNetworkInfo = connectivityManager.activeNetworkInfo
                   if (activeNetworkInfo != null && activeNetworkInfo.isConnected)
                   {
                       return true
                   }
               }
               return false
           }

           // THIS FUNCTION WILL RETURN THE DATE TIME STRING FROM TIMESTAMP
           fun getDateTimeFromTimeStamp(timeStamp: Long): String {
               val c = Date(timeStamp)
               val df = SimpleDateFormat("yyyy-MM-dd-k:mm", Locale.getDefault())
               return df.format(c).uppercase(Locale.ENGLISH)
           }

           // THIS FUNCTION WILL ALERT THE DIFFERENT MESSAGES
           fun showAlert(context: Context, message: String) {
               MaterialAlertDialogBuilder(context)
                   .setMessage(message)
                   .setCancelable(false)
                   .setPositiveButton("Ok") { dialog, which ->
                       dialog.dismiss()
                   }
                   .create().show()
           }

           fun showAlert(context: Context, message: String,listener:DialogInterface.OnClickListener) {
               MaterialAlertDialogBuilder(context)
                   .setMessage(message)
                   .setCancelable(false)
                   .setPositiveButton("Ok") { dialog, which ->
                       dialog.dismiss()
                       listener.onClick(dialog,0)
                   }
                   .create().show()
           }

           fun startLoading(context: Context) {
               val builder = MaterialAlertDialogBuilder(context)
               val layout = LayoutInflater.from(context).inflate(R.layout.custom_loading, null)
               builder.setView(layout)
               builder.setCancelable(false)
               alert = builder.create()
               alert!!.show()
           }

           fun dismiss() {
               if (alert != null) {
                   alert!!.dismiss()
               }
           }

           fun showSnakeBar(message: String, view: View, color: Int)
           {
               val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
               snackBar.view.setBackgroundColor(color)
               snackBar.show()
           }

           fun getCurrentVersion(context: Context): String
           {
               try {
                   val pInfo: PackageInfo = context.packageManager.getPackageInfo(
                       context.packageName,
                       0
                   )
                   return pInfo.versionName
               } catch (e: PackageManager.NameNotFoundException) {
                   e.printStackTrace()
               }
               return ""
           }
       }
}