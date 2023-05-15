package com.sobytek.erpsobytek.view.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.zxing.Result
import com.sobytek.erpsobytek.R
import com.sobytek.erpsobytek.databinding.ActivityScanIgpDetailBinding
import com.sobytek.erpsobytek.databinding.ActivityScanLotDetailBinding
import com.sobytek.erpsobytek.utils.AppSettings
import com.sobytek.erpsobytek.utils.Constants
import com.sobytek.erpsobytek.utils.RuntimePermissionHelper
import com.sobytek.erpsobytek.viewmodel.IGPActivityViewModel
import com.sobytek.erpsobytek.viewmodel.LotDetailActivityViewModel
import com.sobytek.erpsobytek.viewmodelfactory.ViewModelFactory
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.lang.Double

class ScanIgpDetailActivity : BaseActivity(), ZXingScannerView.ResultHandler  {

    private lateinit var context: Context
    private lateinit var binding: ActivityScanIgpDetailBinding
    private lateinit var appSettings: AppSettings
    private lateinit var viewModel: IGPActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityScanIgpDetailBinding.inflate(layoutInflater)
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

    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = "Scan IGP Detail"
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
        var numeric = true
        try {
            val num = Double.parseDouble(scanText)
        } catch (e: NumberFormatException) {
            numeric = false
        }

        if (numeric){
            val intent = Intent().apply {
                putExtra("TEXT",scanText)
            }
            setResult(RESULT_OK,intent)
            finish()
//            val worker: Worker? = getWorker(scanText)
//            if(worker != null){
//                lotIssue(worker,tempLotDetail!!,tempPos!!)
//            } else{
//                showAlert(context,"Scanned EmployeeId not found!") { dialog, which ->
//                    dialog!!.dismiss()
//                     binding.scanner.resumeCameraPreview(this)
//                    binding.scanner.startCamera()
//                }
//            }
        } else{
            showAlert(context,"Scanned text not a EmployeeId", object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.dismiss()
                    binding.scanner.resumeCameraPreview(this@ScanIgpDetailActivity)
                    binding.scanner.startCamera()
                }

            })
        }

    }
}