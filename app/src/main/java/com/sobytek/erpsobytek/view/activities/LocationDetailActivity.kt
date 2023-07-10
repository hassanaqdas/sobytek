package com.sobytek.erpsobytek.view.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.Result
import com.sobytek.erpsobytek.R
import com.sobytek.erpsobytek.databinding.ActivityLocationDetailBinding
import com.sobytek.erpsobytek.model.SampleItem
import com.sobytek.erpsobytek.model.User
import com.sobytek.erpsobytek.utils.AppSettings
import com.sobytek.erpsobytek.utils.Constants
import com.sobytek.erpsobytek.utils.RuntimePermissionHelper
import com.sobytek.erpsobytek.viewmodel.LocationDetailViewModel
import com.sobytek.erpsobytek.viewmodelfactory.ViewModelFactory
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class LocationDetailActivity : BaseActivity(), ZXingScannerView.ResultHandler {


    private lateinit var context: Context
    private lateinit var binding: ActivityLocationDetailBinding
    private var user: User? = null
    private lateinit var appSettings: AppSettings
    private var rackId = ""
    private lateinit var viewModel: LocationDetailViewModel
    private var cameraProviderFuture: ListenableFuture<*>? = null
    private var cameraExecutor: ExecutorService? = null
    private var imageAnalyzer: MyImageAnalyzer? = null
    private lateinit var cam: Camera
    private var scanText:String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setUpToolbar()
        initBarcodeScanner()
    }

    private fun initViews() {
        context = this
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(LocationDetailViewModel()).createFor()
        )[LocationDetailViewModel::class.java]
        appSettings = AppSettings(context)
        user = appSettings.getUser("USER")

        binding.scanSampleBtn.setOnClickListener {
            binding.scanner.resumeCameraPreview(this)
            initMlScanner()
        }

        binding.previewview.setOnClickListener {
            initMlScanner()
        }

    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.location_detail_text)
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
        binding.scanner.setOnClickListener {
            binding.scanner.resumeCameraPreview(this)
        }
    }

    private fun initMlScanner() {

        imageAnalyzer = MyImageAnalyzer(supportFragmentManager)
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        (cameraProviderFuture as ListenableFuture<ProcessCameraProvider>).addListener(Runnable {
            try {
                val processCameraProvider =
                    (cameraProviderFuture as ListenableFuture<ProcessCameraProvider>).get() as ProcessCameraProvider
                bindPreview(processCameraProvider)
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))

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
            initMlScanner()
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
                    initMlScanner()
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
        scanText = result!!.text
        if (scanText.isNotEmpty()) {
            playSound(true)
            generateVibrate()

            val c = "\\b-\\b".toRegex().findAll(scanText).count()
            if (c <= 2) { // if scanText contain a dash - It mean you have scan the sample
                rackId = scanText
                getSampleLocationDetails(rackId)
            } else {
                val parts = scanText.split("-")
                updateLocationDetail(parts[parts.size - 1])
            }
        }
    }

    private fun getSampleLocationDetails(rack_id: String) {

        startLoading(context)
        viewModel.callSampleLocationDetail(context, rack_id,user!!.USER_ID,user!!.PASSO)
        viewModel.getSampleLocationDetailResponse().observe(this, Observer { response ->
            if (response != null) {
                if (response.sampleItems.isNotEmpty()) {
                    displaySamplesDetails(response.sampleItems)
                } else {
                    dismiss()
                    initMlScanner()
                }

            } else {
                dismiss()
                showAlert(
                    context,
                    "Something went wrong with server, please scan the sample again!"
                ){ dialog, which ->
                    dialog!!.dismiss()
                    initMlScanner()
                }
            }
        })

    }

    private fun displaySamplesDetails(sampleItems: List<SampleItem>) {
        if (sampleItems.isNotEmpty() && binding.sampleDetailsLayout.childCount > 0) {
            binding.sampleDetailsLayout.removeAllViews()
        }
        for (i in 0 until sampleItems.size) {
            val item = sampleItems[i]
            val sampleItemLayout =
                LayoutInflater.from(context).inflate(R.layout.sample_location_detail_item_row, null)
            val storeCodeView =
                sampleItemLayout.findViewById<MaterialTextView>(R.id.sld_item_store_code)
            val sampleVerView =
                sampleItemLayout.findViewById<MaterialTextView>(R.id.sld_item_sample_ver)
            val sampleQtyView = sampleItemLayout.findViewById<MaterialTextView>(R.id.sld_item_oqty)
            storeCodeView.text = item.SC_CODE
            sampleVerView.text = item.SAMPLE_VER
            sampleQtyView.text = item.BALANCE_QTY
            binding.sampleDetailsLayout.addView(sampleItemLayout)
        }
        changeScanMode()
        dismiss()
    }

    private fun changeScanMode(){

        if (rackId.isNotEmpty()) {
           binding.scanModeView.text = "Scan to add Sample Location"
        } else {
            binding.scanModeView.text = "Scan to add Sample Location"
        }
    }


    private fun updateLocationDetail(sampleId: String) {
        if (rackId.isEmpty()) {
            showAlert(context, "Please scan the Rack Location before update the sample location!") { dialog, which ->
                dialog!!.dismiss()
                initMlScanner()
            }
        } else {
            startLoading(context)
            viewModel.callUpdateLocationDetail(context, rackId, sampleId,user!!.USER_ID,user!!.PASSO)
            viewModel.getUpdateLocationDetailResponse().observe(this, Observer { response ->
                dismiss()
                if (response != null) {
                    if (response.get("status").asInt == 200) {
                        getSampleLocationDetails(rackId)
                    } else {
                        showAlert(
                            context,
                            response.get("message").asString
                        )
                    }

                } else {
                    showAlert(
                        context,
                        "Something went wrong with server, please scan the sample again!"
                    ){ dialog, which ->
                        dialog!!.dismiss()
                        initMlScanner()
                    }
                }
            })
        }
        changeScanMode()
    }

    private fun bindPreview(processCameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(binding.previewview.surfaceProvider)
        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        val imageCapture = ImageCapture.Builder().build()


        val imageAnalysis =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ImageAnalysis.Builder()
                    .setTargetResolution(Size(1200, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build().also {
                        cameraExecutor?.let { it1 ->
                            imageAnalyzer?.let { it2 ->
                                it.setAnalyzer(
                                    it1,
                                    it2
                                )
                            }
                        }
                    }
            } else {
                TODO("VERSION.SDK_INT < LOLLIPOP")
            }

        processCameraProvider.unbindAll()
        cam = processCameraProvider.bindToLifecycle(
            this,
            cameraSelector,
            preview,
            imageCapture,
            imageAnalysis
        )
    }

    inner class MyImageAnalyzer(supportFragmentManager: FragmentManager) : ImageAnalysis.Analyzer {
        var count = 0
        private var fragmentManager: FragmentManager? = null

        init {
            this.fragmentManager = supportFragmentManager
        }

        override fun analyze(image: ImageProxy) {
            scanBarCode(image)
        }


        private fun scanBarCode(image: ImageProxy) {
            @SuppressLint("UnsafeOptInUsageError") val image1 = image.image!!
            val inputImage = InputImage.fromMediaImage(image1, image.imageInfo.rotationDegrees)
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_CODE_128, Barcode.FORMAT_CODE_39,
                    Barcode.FORMAT_CODE_93, Barcode.FORMAT_CODABAR,
                    Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8,
                    Barcode.FORMAT_ITF, Barcode.FORMAT_UPC_A,
                    Barcode.FORMAT_UPC_E, Barcode.FORMAT_QR_CODE,
                    Barcode.FORMAT_PDF417, Barcode.FORMAT_AZTEC,
                    Barcode.FORMAT_DATA_MATRIX
                )
                .build()
            val scanner = BarcodeScanning.getClient(options)
            val result = scanner.process(inputImage)
                .addOnSuccessListener { barcodes -> // Task completed successfully

                    readBarCodeData(barcodes)
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    // ...
                }.addOnCompleteListener { barcodes ->
                    image.close()
                }

        }

        private fun readBarCodeData(barcodes: List<Barcode>) {
            if (barcodes.isNotEmpty()) {
//                val bounds = barcodes[0].boundingBox
//                val corners = barcodes[0].cornerPoints
                    val rawValue = barcodes[0].rawValue
                    val valueType = barcodes[0].valueType
                    // See API reference for complete list of supported types
                    Log.d("TEST199", "readBarCodeData: $rawValue")
                    if (barcodes[0].rawValue != null) {
                        cameraExecutor!!.shutdownNow()

                        onScanResult(rawValue!!)
                    }
            }
        }
    }

    private fun onScanResult(rawValue: String) {
        if (rawValue.isNotEmpty()) {
            playSound(true)
            generateVibrate()

            val c = "\\b-\\b".toRegex().findAll(rawValue).count()
            if (c <= 2) { // if scanText contain a dash - It mean you have scan the sample
                rackId = rawValue
                getSampleLocationDetails(rackId)
            } else {
                val parts = rawValue.split("-")
                updateLocationDetail(parts[parts.size - 1])
            }
        }
    }

}