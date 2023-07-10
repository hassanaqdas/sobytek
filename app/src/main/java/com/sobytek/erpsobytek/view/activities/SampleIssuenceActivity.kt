package com.sobytek.erpsobytek.view.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.util.Size
import android.view.MenuItem
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.sobytek.erpsobytek.R
import com.sobytek.erpsobytek.adapters.SampleIssuenceDetailAdapter
import com.sobytek.erpsobytek.databinding.ActivitySampleIssuenceBinding
import com.sobytek.erpsobytek.model.Sample
import com.sobytek.erpsobytek.model.User
import com.sobytek.erpsobytek.utils.AppSettings
import com.sobytek.erpsobytek.utils.Constants
import com.sobytek.erpsobytek.utils.RuntimePermissionHelper
import com.sobytek.erpsobytek.viewmodel.LocationDetailViewModel
import com.sobytek.erpsobytek.viewmodel.SampleIssuenceViewModel
import com.sobytek.erpsobytek.viewmodelfactory.ViewModelFactory
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SampleIssuenceActivity : BaseActivity(),SampleIssuenceDetailAdapter.OnItemClickListener {

    private lateinit var context: Context
    private lateinit var binding: ActivitySampleIssuenceBinding
    private var user: User? = null
    private lateinit var appSettings: AppSettings
    private var cameraProviderFuture: ListenableFuture<*>? = null
    private var cameraExecutor: ExecutorService? = null
    private var imageAnalyzer: MyImageAnalyzer? = null
    private lateinit var cam: Camera
    private var scanText: String = ""
    private var samplesIssueList = mutableListOf<Sample>()
    private lateinit var viewModel: SampleIssuenceViewModel
    private lateinit var adapter: SampleIssuenceDetailAdapter
    private var selectedSample:Sample?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySampleIssuenceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setUpToolbar()

    }

    private fun initViews() {
        context = this
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(SampleIssuenceViewModel()).createFor()
        )[SampleIssuenceViewModel::class.java]
        appSettings = AppSettings(context)
        user = appSettings.getUser("USER")

        binding.sampleIssuenceDetailRecyclerview.layoutManager  = LinearLayoutManager(context)
        binding.sampleIssuenceDetailRecyclerview.hasFixedSize()
        adapter = SampleIssuenceDetailAdapter(context,samplesIssueList as ArrayList<Sample>)
        binding.sampleIssuenceDetailRecyclerview.adapter = adapter
        adapter.setOnItemClickListener(this)

        binding.previewview.setOnClickListener {
            initMlScanner()
        }

    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.sample_issuence_text)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            binding.toolbar.setTitleTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.primary
                )
            )
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
//        if (RuntimePermissionHelper.checkCameraPermission(
//                this,
//                Constants.CAMERA_PERMISSION
//            )
//        ) {
//            initMlScanner()
//        }
        getSampleIssuenceDetails()
    }

    private fun getSampleIssuenceDetails() {
        startLoading(context)
        viewModel.callSampleIssueDetails(context,user!!.USER_ID,user!!.PASSO)
        viewModel.getSampleIssueDetailsResponse().observe(this, Observer { sampleIssuenceResponse ->
            dismiss()
            if(sampleIssuenceResponse != null){
                if (sampleIssuenceResponse.status == "200"){
                    if (samplesIssueList.isNotEmpty()){
                        samplesIssueList.clear()
                    }
                    samplesIssueList.addAll(sampleIssuenceResponse.samples)
                    adapter.notifyItemRangeChanged(0,samplesIssueList.size)
                }
                else{
                    showAlert(
                        context,
                        "Something went wrong with server, please try again!"
                    )
                }

            }

        })
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            Constants.CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    binding.scanner.setResultHandler(this)
//                    binding.scanner.startCamera()
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

            //val c = "\\b-\\b".toRegex().findAll(rawValue).count()

            if (selectedSample != null) { // if scanText contain a dash - It mean you have scan the sample
//                rackId = rawValue
//                getSampleLocationDetails(rackId)
                val parts = rawValue.split("-")
                // SCAN SAMPLE ID == SELECTED SAMPLE ID THEN UPDATE ELSE SHOW ERROR "YOU HAVE SELECTED THE WRONG SAMPLE"
                if (selectedSample!!.SAMPLE_ID.toString() == parts[parts.size - 1]){
                    updateSampleIssuence(selectedSample!!.SAMPLE_TRANS_ID,user!!.USER_ID,user!!.PASSO)
                }
                else{
                    showAlert(context,"YOU HAVE SELECTED THE WRONG SAMPLE")
                    initMlScanner()
                }

            }
//            else {
////                val parts = rawValue.split("-")
////                updateLsocationDetail(parts[parts.size - 1])
//            }
        }
    }

    private fun updateSampleIssuence(sampleTransId: Int, userId: String,passo:String) {
        startLoading(context)
        viewModel.callSampleIssueUpdate(context,sampleTransId,userId,passo)
        viewModel.getSampleIssueUpdateResponse().observe(this, Observer { response ->
            dismiss()
            if (response != null) {
                if (response.get("status").asInt == 200) {
                    selectedSample = null
                    getSampleIssuenceDetails()
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

    override fun onItemClick(position: Int) {
        if (RuntimePermissionHelper.checkCameraPermission(
                this,
                Constants.CAMERA_PERMISSION
            )
        ) {
            val sample = samplesIssueList[position]
            if (sample.BALANCE_QTY > 0){
                selectedSample = sample
                initMlScanner()
            }
            else{
              showAlert(context,"Balance is not available")
            }

        }

    }

}