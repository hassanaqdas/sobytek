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
import android.view.Menu
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
import com.sobytek.erpsobytek.databinding.ActivityMainBinding
import com.sobytek.erpsobytek.databinding.ContentMainBinding
import com.sobytek.erpsobytek.model.LotDetail
import com.sobytek.erpsobytek.model.User
import com.sobytek.erpsobytek.utils.AppSettings
import com.sobytek.erpsobytek.utils.Constants
import com.sobytek.erpsobytek.utils.RuntimePermissionHelper
import com.sobytek.erpsobytek.viewmodel.MainActivityViewModel
import com.sobytek.erpsobytek.viewmodelfactory.ViewModelFactory
import me.dm7.barcodescanner.zxing.ZXingScannerView

class MainActivity : BaseActivity(), ZXingScannerView.ResultHandler {

    private lateinit var context: Context
    private var user: User? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var bindingMainContent: ContentMainBinding
    private lateinit var appSettings: AppSettings
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindingMainContent = ContentMainBinding.bind(binding.root)


        initViews()
        setUpToolbar()
        initBarcodeScanner()
    }

    private fun initViews() {
        context = this
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(MainActivityViewModel()).createFor()
        )[MainActivityViewModel::class.java]
        appSettings = AppSettings(context)
        user  = appSettings.getUser("USER")
    }

    private fun setUpToolbar() {
        setSupportActionBar(bindingMainContent.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.app_name)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            bindingMainContent.toolbar.setTitleTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.primary
                )
            )
        }
    }


    private fun initBarcodeScanner() {
        bindingMainContent.scanner.setBorderColor(ContextCompat.getColor(context, R.color.white))
        bindingMainContent.scanner.setSquareViewFinder(true)
        bindingMainContent.scanner.setLaserEnabled(true)
        bindingMainContent.scanner.setAutoFocus(true)
        bindingMainContent.scanner.setOnClickListener {
            bindingMainContent.scanner.resumeCameraPreview(this)
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.main_menu, menu)
//        return true
//    }
//
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

//    private fun logout() {
//        val builder = MaterialAlertDialogBuilder(context)
//        builder.setTitle("Logout!")
//        builder.setMessage("Are you sure, you want to exit from app?")
//        builder.setNegativeButton("Cancel") { dialog, which ->
//            dialog.dismiss()
//        }
//        builder.setPositiveButton("Logout") { dialog, which ->
//            appSettings.remove("STATUS")
//            val intent = Intent(context, LoginActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//            startActivity(intent)
//            finish()
//        }
//        val alert = builder.create()
//        alert.show()
//    }

    override fun onResume() {
        super.onResume()
        if (RuntimePermissionHelper.checkCameraPermission(
                this,
                Constants.CAMERA_PERMISSION
            )
        ) {
            bindingMainContent.scanner.setResultHandler(this)
            bindingMainContent.scanner.startCamera()
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
                    bindingMainContent.scanner.setResultHandler(this)
                    bindingMainContent.scanner.startCamera()
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
        bindingMainContent.scanner.stopCamera()
    }

    override fun onStop() {
        super.onStop()
        bindingMainContent.scanner.stopCamera()
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
            if (scanText.length <= 6) {
                playSound(true)
                generateVibrate()

                val lotId = scanText.toInt()
                startLoading(context)
                viewModel.callLot(context, lotId,user!!.USER_ID,user!!.PASSO)
                viewModel.getLotResponse().observe(this, Observer { response ->
                    dismiss()
                    if (response != null) {
                        if (response.status == 200) {
                            if (response.operation == "issue") {
                                val intent = Intent(context, LotDetailActivity::class.java)
                                intent.putExtra("LOT_ID", lotId)
                                startActivity(intent)
                            }
                            else if (response.operation == "done"){
                                MaterialAlertDialogBuilder(context)
                                    .setMessage("All the operation has been completed!")
                                    .setPositiveButton("Ok"){dialog,which->
                                        dialog.dismiss()
                                        bindingMainContent.scanner.resumeCameraPreview(this)
                                    }
                                    .setCancelable(false)
                                    .create().show()
                            }
                            else {
                                if(response.lotDetail[0].op_id == null){
                                    showAlert(context,"Scanned Lot Id not available!",object :DialogInterface.OnClickListener{
                                        override fun onClick(dialog: DialogInterface?, which: Int) {
                                            dialog!!.dismiss()
                                            bindingMainContent.scanner.resumeCameraPreview(this@MainActivity)
                                        }

                                    })
                                }
                                else{
                                    val recDetail = response.lotDetail[0]
                                    displayReceiveLotDialog(recDetail,lotId)
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
                    .setMessage("Lot ID not correct!")
                    .setCancelable(false)
                    .setPositiveButton("Ok") { dialog, which ->
                        dialog.dismiss()
                        bindingMainContent.scanner.resumeCameraPreview(this)
                    }
                    .create().show()
            }
        }

    }

    private fun displayReceiveLotDialog(detail: LotDetail,lotId:Int) {
        val layout = LayoutInflater.from(context).inflate(R.layout.lot_receive_dialog, null)
        val lotIdView = layout.findViewById<MaterialTextView>(R.id.rec_lot_id_tv)
        lotIdView.text = detail.lot_id
        val lotIssueQtyView = layout.findViewById<MaterialTextView>(R.id.rec_issue_qty_tv)
        lotIssueQtyView.text = detail.issue_qty
        val lotOpIdView = layout.findViewById<MaterialTextView>(R.id.rec_op_id_tv)
        lotOpIdView.text = detail.op_id
        val reworkQtyInputBox = layout.findViewById<TextInputEditText>(R.id.rework_qty_input)
        val fgrrQtyInputBox = layout.findViewById<TextInputEditText>(R.id.fgrr_qty_input)
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
            bindingMainContent.scanner.resumeCameraPreview(this)
            alert.dismiss() }
        submitBtn.setOnClickListener {
            alert.dismiss()
            var reworkqty = reworkQtyInputBox.text.toString().trim()
            var fgrrqty = fgrrQtyInputBox.text.toString().trim()
            var value = rejectionQtyInputBox.text.toString().trim()
            val remarks = remarksInputBox.text.toString().trim()
            if (reworkqty.isEmpty()){
                reworkqty = "0"
            }
            if (fgrrqty.isEmpty()){
                fgrrqty = "0"
            }
            if (value.isEmpty()){
                value = "0"
            }

            if (user != null){
                startLoading(context)
                viewModel.callLotReceive(context,detail.lot_id.toInt(),detail.issue_qty.toInt(),user!!.USER_ID,user!!.PASSO,value.toInt(),detail.op_no.toInt(),remarks,reworkqty.toInt(),fgrrqty.toInt())
                viewModel.getLotReceiveResponse().observe(this) { response ->
                    if (response != null) {
                        dismiss()
                        if (response.get("status").asString == "200") {
//                            MaterialAlertDialogBuilder(context)
//                                .setMessage(response.get("message").asString)
//                                .setPositiveButton("Ok"){dialog,which->
//                                    dialog.dismiss()
//                                    bindingMainContent.scanner.resumeCameraPreview(this)
//                                }
//                                .setCancelable(false)
//                                .create().show()
                            val intent = Intent(context, LotDetailActivity::class.java)
                            intent.putExtra("LOT_ID", lotId)
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