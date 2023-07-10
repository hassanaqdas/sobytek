package com.sobytek.erpsobytek.view.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.zxing.Result
import com.sobytek.erpsobytek.R
import com.sobytek.erpsobytek.databinding.ActivityIgpactivityBinding
import com.sobytek.erpsobytek.model.IgpDetail
import com.sobytek.erpsobytek.model.LotDetail
import com.sobytek.erpsobytek.model.User
import com.sobytek.erpsobytek.utils.AppSettings
import com.sobytek.erpsobytek.utils.Constants
import com.sobytek.erpsobytek.utils.RuntimePermissionHelper
import com.sobytek.erpsobytek.viewmodel.IGPActivityViewModel
import com.sobytek.erpsobytek.viewmodelfactory.ViewModelFactory
import me.dm7.barcodescanner.zxing.ZXingScannerView

class IGPActivity : BaseActivity(), ZXingScannerView.ResultHandler {

    private lateinit var context: Context
    private var user: User? = null
    private lateinit var binding: ActivityIgpactivityBinding
    private lateinit var appSettings: AppSettings
    private lateinit var viewModel:IGPActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityIgpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setUpToolbar()
        initBarcodeScanner()

    }

    private fun initViews() {
        context = this
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(IGPActivityViewModel()).createFor()
        )[IGPActivityViewModel::class.java]
        appSettings = AppSettings(context)
        user  = appSettings.getUser("USER")
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.app_name)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            binding.toolbar.setTitleTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.primary
                )
            )
        }
    }


    private fun initBarcodeScanner() {
        binding.scanner.setBorderColor(ContextCompat.getColor(context, R.color.white))
        binding.scanner.setSquareViewFinder(true)
        binding.scanner.setLaserEnabled(true)
        binding.scanner.setAutoFocus(true)
        binding.scanner.setOnClickListener {
            binding.scanner.resumeCameraPreview(this)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (RuntimePermissionHelper.checkCameraPermission(
                this,
                Constants.CAMERA_PERMISSION
            )
        ) {
            binding.scanner.setResultHandler(this)
            binding.scanner.startCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            Constants.CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    binding.scanner.setResultHandler(this)
                    binding.scanner.startCamera()
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Constants.CAMERA_PERMISSION
                        )
                    ) {
                        RuntimePermissionHelper.checkStoragePermission(
                            this,
                            Constants.CAMERA_PERMISSION
                        )
                    } else {
                        MaterialAlertDialogBuilder(context)
                            .setMessage("Please allow the Camera permission to use the scanner to scan Image.")
                            .setCancelable(false)
                            .setPositiveButton("Ok") { dialog, which ->
                                dialog.dismiss()
                            }
                            .create().show()
                    }
                }
            }
            else -> {

            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.scanner.stopCamera()
    }

    override fun onStop() {
        super.onStop()
        binding.scanner.stopCamera()
    }

    private fun playSound(flag: Boolean) {

        var player: MediaPlayer? = null
        player = if (flag) {
            MediaPlayer.create(context, R.raw.succes_beep)
        } else {
            MediaPlayer.create(context, R.raw.error_beep)
        }

        player.start()
    }

    @SuppressLint("MissingPermission")
    private fun generateVibrate() {

        if (Build.VERSION.SDK_INT >= 26) {
            (getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(
                VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            (getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(150)
        }

    }

    override fun handleResult(result: Result?) {
        val scanText = result!!.text

        if (scanText.isNotEmpty()) {
                playSound(true)
                generateVibrate()

                val igpId = scanText
                startLoading(context)
                viewModel.callIgp(context, igpId,user!!.USER_ID,user!!.PASSO)
                viewModel.getIgpResponse().observe(this, Observer { response ->
                    dismiss()
                    if (response != null) {
                        if (response.status == 200) {
                            Log.d("TEST199",response.toString())
                            if (response.operation == "issue") {
                                val intent = Intent(context, IGPDetailActivity::class.java)
                                intent.putExtra("GIR_ID", igpId)
                                startActivity(intent)
                            }
                            else if (response.operation == "done"){
                                MaterialAlertDialogBuilder(context)
                                    .setMessage("All the operation has been completed!")
                                    .setPositiveButton("Ok"){dialog,which->
                                        dialog.dismiss()
                                        binding.scanner.resumeCameraPreview(this)
                                    }
                                    .setCancelable(false)
                                    .create().show()
                            }
                            else {
                                if(response.igpDetail[0].op_no.isEmpty()){
                                    showAlert(context,"Scanned Igp Id not available!"
                                    ) { dialog, which ->
                                        dialog!!.dismiss()
                                        binding.scanner.resumeCameraPreview(this@IGPActivity)
                                    }
                                }
                                else{
                                    val recDetail = response.igpDetail[0]
                                    displayReceiveIgpDialog(recDetail,igpId)
                                }

                            }
                        } else {
                            showSnakeBar(
                                "Something wrong with server, try again!",
                                binding.parentMainLayout,
                                ContextCompat.getColor(context, R.color.red)
                            )
                        }
                    } else {
                        showSnakeBar(
                            "Something wrong with server, try again!",
                            binding.parentMainLayout,
                            ContextCompat.getColor(context, R.color.red)
                        )
                    }
                })
            } else {
                playSound(false)
                generateVibrate()
                MaterialAlertDialogBuilder(context)
                    .setMessage("IGP ID not correct!")
                    .setCancelable(false)
                    .setPositiveButton("Ok") { dialog, which ->
                        dialog.dismiss()
                        binding.scanner.resumeCameraPreview(this)
                    }
                    .create().show()
            }

    }

    private fun displayReceiveIgpDialog(detail: IgpDetail, igpId:String) {
        val layout = LayoutInflater.from(context).inflate(R.layout.igp_receive_dialog, null)
        val girIdView = layout.findViewById<MaterialTextView>(R.id.rec_gir_id_tv)
        girIdView.text = detail.gir_id
        val igpIssueQtyView = layout.findViewById<MaterialTextView>(R.id.rec_issue_qty_tv)
        igpIssueQtyView.text = detail.issue_qty
        val igpOpIdView = layout.findViewById<MaterialTextView>(R.id.rec_op_id_tv)
        igpOpIdView.text = detail.op_id
        val rejectionQtyInputBox = layout.findViewById<TextInputEditText>(R.id.rejection_qty_input)
        val remarksInputBox = layout.findViewById<TextInputEditText>(R.id.remarks_input)
        val cancelBtn = layout.findViewById<AppCompatButton>(R.id.rec_cancel_btn)
        val submitBtn = layout.findViewById<AppCompatButton>(R.id.rec_submit_btn)
        val builder = MaterialAlertDialogBuilder(context)
        builder.setView(layout)
        builder.setCancelable(false)
        val alert = builder.create()
        alert.show()

        cancelBtn.setOnClickListener {
            binding.scanner.resumeCameraPreview(this)
            alert.dismiss() }
        submitBtn.setOnClickListener {
            alert.dismiss()
            var value = rejectionQtyInputBox.text.toString().trim()
            val remarks = remarksInputBox.text.toString().trim()
            if (value.isEmpty()){
                value = "0"
            }
            if (user != null){
                startLoading(context)
                viewModel.callIgpReceive(context,detail.gir_id,
                    detail.issue_qty.toInt(),user!!.USER_ID,user!!.PASSO,value.toInt(),
                    detail.op_no.toInt(),remarks)
                viewModel.getIgpReceiveResponse().observe(this) { response ->
                    if (response != null) {
                        dismiss()
                        if (response.get("status").asString == "200") {
//                            MaterialAlertDialogBuilder(context)
//                                .setMessage(response.get("message").asString)
//                                .setPositiveButton("Ok"){dialog,which->
//                                    dialog.dismiss()
//                                    binding.scanner.resumeCameraPreview(this)
//                                }
//                                .setCancelable(false)
//                                .create().show()
                            val intent = Intent(context, IGPDetailActivity::class.java)
                            intent.putExtra("GIR_ID", detail.gir_id)
                            startActivity(intent)
                        } else {
                            showAlert(context, response.get("message").asString)
                        }
                    }
                }
            }
        }

    }
}