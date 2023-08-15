package com.sobytek.erpsobytek.view.activities

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.sobytek.erpsobytek.R
import com.sobytek.erpsobytek.databinding.ActivityLoginBinding
import com.sobytek.erpsobytek.model.User
import com.sobytek.erpsobytek.utils.AppSettings
import com.sobytek.erpsobytek.utils.Constants
import com.sobytek.erpsobytek.viewmodel.LoginViewModel
import com.sobytek.erpsobytek.viewmodelfactory.ViewModelFactory
import java.util.concurrent.Executor


class LoginActivity : BaseActivity(), View.OnClickListener {

    private lateinit var context: Context
    private lateinit var appSettings: AppSettings
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var isBiometric = false
    private var user:User?=null
//    private var keyStore: KeyStore? = null
//    private val KEY_NAME = "erp"
//    private var cipher: Cipher? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() {
        context = this
        appSettings = AppSettings(context)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(LoginViewModel()).createFor()
        )[LoginViewModel::class.java]
        // SET CLICK LISTENER
        binding.loginButton.setOnClickListener(this)
        binding.loginFingerprintImageview.setOnClickListener(this)
        binding.appVersionView.text = "v_${getCurrentVersion(context)}"
        executor = ContextCompat.getMainExecutor(context)
        biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    if (appSettings.getBoolean("BIOMETRIC_STATUS") || isBiometric) {
                        Toast.makeText(context, "Authentication success", Toast.LENGTH_SHORT).show()
                        appSettings.putBoolean("BIOMETRIC_STATUS", true)
                        appSettings.putBoolean("STATUS", true)
//                        val intent = Intent(context, DashboardActivity::class.java)
//                        intent.flags =
//                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                        startActivity(intent)
//                        finish()
                        getUserAccessButtonsDetails(user!!)
                    } else {
                        showAlert(
                            context,
                            "You didn't enable the Biometric authentication with this app!"
                        )
                    }

                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Login")
            .setNegativeButtonText("use UserId and Password")
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .build()

        // CHECK IF SYSTEM HAVE THE BIOMETRIC AVAILABLE
        if (biometricAvailable() && !appSettings.getBoolean("BIOMETRIC_STATUS")) {
            binding.biometricOptionWrapperLayout.visibility = View.VISIBLE
            binding.loginFingerprintImageview.visibility = View.VISIBLE
        } else if (biometricAvailable() && appSettings.getBoolean("BIOMETRIC_STATUS")) {
            binding.loginFingerprintImageview.visibility = View.VISIBLE
            biometricPrompt.authenticate(promptInfo)
        } else {
            binding.loginFingerprintImageview.visibility = View.GONE
            binding.biometricOptionWrapperLayout.visibility = View.GONE
        }

        // BIOMETRIC SWITCH LISTENER
        binding.biometricSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            isBiometric = isChecked
            if (isBiometric) {
                showAlert(
                    context,
                    "Please Login using your User Id and Password for the first time!"
                )
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.login_button -> {
                if (validation()) {
                    startLoading(context)
                    val userId = binding.loginUserIdBox.text.toString().trim()
                    val passo = binding.loginPasswordBox.text.toString().trim()
                    viewModel.callLogin(context, userId, passo)
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
                            user = User(
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
                            appSettings.putUser("USER", user!!)
                            appSettings.putString("CREDENTIAL", "$userId:$passo")
                            if (!appSettings.getBoolean("BIOMETRIC_STATUS") && !isBiometric) {
//                                val intent = Intent(context, DashboardActivity::class.java)
//                                intent.flags =
//                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                                startActivity(intent)
//                                finish()
                                getUserAccessButtonsDetails(user!!)
                            } else {
                                biometricPrompt.authenticate(promptInfo)
                            }
                        } else if (response != null && response.get("status").asString == "201") {
                            showSnakeBar(
                                response.get("message").asString,
                                binding.loginParentLayout,
                                ContextCompat.getColor(context, R.color.red)
                            )
                        } else {
                            showSnakeBar(
                                "Something wrong with server connectivity!",
                                binding.loginParentLayout,
                                ContextCompat.getColor(context, R.color.red)
                            )
                        }
                    })
                }
            }
            R.id.login_fingerprint_imageview -> {

                biometricPrompt.authenticate(promptInfo)

            }
            else -> {

            }
        }
    }

    private fun getUserAccessButtonsDetails(user: User) {
        viewModel.callUserAccessButtons(context, user.USER_ID, user.PASSO)
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

    private fun validation(): Boolean {

        if (binding.loginUserIdBox.text.toString().trim().isEmpty()) {
            showSnakeBar(
                "Please enter the USER ID",
                binding.loginParentLayout,
                ContextCompat.getColor(context, R.color.red)
            )
            return false
        } else if (binding.loginPasswordBox.text.toString().trim().isEmpty()) {
            showSnakeBar(
                "Please enter the Password",
                binding.loginParentLayout,
                ContextCompat.getColor(context, R.color.red)
            )
            return false
        }
        return true
    }

    private fun biometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(this)
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
                true
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.e("MY_APP_TAG", "No biometric features available on this device.")
                false
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
                false
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Prompts the user to create credentials that your app accepts.
                MaterialAlertDialogBuilder(context)
                    .setTitle("Fingerprint Enrolled!")
                    .setMessage("System have no fingerprint enrolled, are you want to enroll fingerprint?")
                    .setNegativeButton("Cancel"){dialog,which->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Setting"){dialog,which->
                        dialog.dismiss()
                        val enrollIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                putExtra(
                                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                    BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                                )
                            }
                        } else {
                            Intent(Settings.ACTION_SECURITY_SETTINGS)
                        }
                        startActivityForResult(enrollIntent, Constants.REQUEST_BIOMETRIC_ENROLL_CODE)
                    }.create().show()

                false
            }
            else -> {
                false
            }
        }
    }
}