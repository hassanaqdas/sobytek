package com.sobytek.erpsobytek.view.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.sobytek.erpsobytek.R
import com.sobytek.erpsobytek.model.User
import com.sobytek.erpsobytek.utils.AppSettings
import com.sobytek.erpsobytek.utils.Constants
import com.sobytek.erpsobytek.viewmodel.LoginViewModel
import com.sobytek.erpsobytek.viewmodelfactory.ViewModelFactory

class SplashActivity : BaseActivity() {

    private lateinit var context: Context
    private lateinit var appSettings: AppSettings
    private lateinit var viewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        context = this
        appSettings = AppSettings(context)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(LoginViewModel()).createFor()
        )[LoginViewModel::class.java]
        startApp()
    }

    private fun startApp() {
        val status = appSettings.getBoolean("STATUS")
        Handler(Looper.myLooper()!!).postDelayed({
            if (status) {
                val user = appSettings.getUser("USER")
                if (user != null) {
//                    val intent = Intent(context, DashboardActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                    startActivity(intent)
//                    finish()
                    getUserAccessButtonsDetails(user)
                } else {
                    val credential = appSettings.getString("CREDENTIAL")
                    val parts = credential!!.split(":")
                    if (parts.isNotEmpty() && parts.size == 2) {
                        viewModel.callLogin(context, parts[0], parts[1])
                        viewModel.getLoginResponse().observe(this, Observer { response ->
                            dismiss()
                            if (response != null && response.get("status").asString == "200") {
                                val jsonObject = response.get("user").asJsonObject
                                val employeeId =
                                    if (jsonObject.get("EMPLOYEE_ID").isJsonNull) {
                                        ""
                                    } else {
                                        jsonObject.get("EMPLOYEE_ID").asString
                                    }
                                val remarks =
                                    if (jsonObject.get("REMARKS").isJsonNull) {
                                        ""
                                    } else {
                                        jsonObject.get("REMARKS").asString
                                    }
                                val user1 = User(
                                    jsonObject.get("ADMIN").asString,
                                    employeeId,
                                    jsonObject.get("FDATETIME").asString,
                                    jsonObject.get("NAME").asString,
                                    jsonObject.get("PASSO").asString,
                                    remarks,
                                    jsonObject.get("STATUS").asString,
                                    jsonObject.get("USER_ID").asString
                                )
                                appSettings.putBoolean("STATUS", true)
                                appSettings.putUser("USER", user1)
                                appSettings.putString("CREDENTIAL", "${parts[0]}:${parts[1]}")
//                                val intent = Intent(context, DashboardActivity::class.java)
//                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                                startActivity(intent)
//                                finish()
                                getUserAccessButtonsDetails(user1)
                            }
                        })
                    } else {
                        val user1 = appSettings.getUser("USER") as User
//                        val intent = Intent(context, DashboardActivity::class.java)
//                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                        startActivity(intent)
//                        finish()
                        getUserAccessButtonsDetails(user1)
                    }
                }
            } else {
                startActivity(Intent(context, LoginActivity::class.java))
                finish()
            }

        }, Constants.splashDelayTime)
    }

    private fun getUserAccessButtonsDetails(user: User) {
        viewModel.callUserAccessButtons(context, user.USER_ID,user.PASSO)
        viewModel.getUserAccessButtonsResponse().observe(this, Observer { response ->
            dismiss()
            if (response != null && response.get("status").asString == "200") {
                val data = response.getAsJsonArray("details")
                if (data.size() > 0) {
                    for (i in 0 until data.size()) {
                        val item = data.get(i).asJsonObject
                        Constants.buttonAccessList.add(
                            Pair(
                                item.get("BUTTON_NAME").asString,
                                item.get("ALLOWED").asString
                            )
                        )
                    }
                    val intent = Intent(context, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                else{
                    val intent = Intent(context, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            }
            else{
                val intent = Intent(context, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }

        })
    }
}