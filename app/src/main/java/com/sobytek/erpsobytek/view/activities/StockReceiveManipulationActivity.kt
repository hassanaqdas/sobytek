package com.sobytek.erpsobytek.view.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.sobytek.erpsobytek.R
import com.sobytek.erpsobytek.databinding.ActivityStockReceiveManipulationBinding
import com.sobytek.erpsobytek.databinding.StockReceiveDetailItemRowBinding
import com.sobytek.erpsobytek.model.Stock
import com.sobytek.erpsobytek.model.StockObject
import com.sobytek.erpsobytek.model.User
import com.sobytek.erpsobytek.utils.AppSettings
import com.sobytek.erpsobytek.utils.Constants
import com.sobytek.erpsobytek.utils.RuntimePermissionHelper
import com.sobytek.erpsobytek.viewmodel.StockViewModel
import com.sobytek.erpsobytek.viewmodelfactory.ViewModelFactory
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class StockReceiveManipulationActivity : BaseActivity() {

    private lateinit var context: Context
    private lateinit var binding: ActivityStockReceiveManipulationBinding
    private var user: User? = null
    private lateinit var appSettings: AppSettings
    private var stock: Stock? = null
    private var cameraProviderFuture: ListenableFuture<*>? = null
    private var cameraExecutor: ExecutorService? = null
    private var imageAnalyzer: MyImageAnalyzer? = null
    private lateinit var cam: Camera
    private var scannedLocationValue = ""
    private var scannerType = "loc"
    private var previousScanTraySum = 0
    private var acceptedTraysDetailList = mutableListOf<StockObject>()
    private lateinit var viewModel: StockViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockReceiveManipulationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setUpToolbar()

    }

    private fun initViews() {
        context = this
        appSettings = AppSettings(context)
        user = appSettings.getUser("USER")
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(StockViewModel()).createFor()
        )[StockViewModel::class.java]

        if (intent != null && intent.hasExtra("STOCK")) {
            stock = intent.getSerializableExtra("STOCK") as Stock
        }
        binding.noteTextview.text = "Note: Scan the Location!"
        if (stock != null) {
            val itemLayout = StockReceiveDetailItemRowBinding.inflate(LayoutInflater.from(context))
            itemLayout.srdItemDocNo.text = stock!!.DOC_NO
            itemLayout.srdItemStoreCode.text = stock!!.STORE_CODE
            itemLayout.srdItemDateTime.text = stock!!.DOC_DATE
            itemLayout.srdItemQuantity.text = stock!!.QUANTITY
            binding.selectedStockDetailWrapper.addView(itemLayout.root)
            itemLayout.root.setBackgroundColor(Color.parseColor("#EAEAF6"))
        }

        binding.previewview.setOnClickListener {
            initMlScanner()
        }

        binding.saveBtn.setOnClickListener{
            if (acceptedTraysDetailList.size > 0) {
                startLoading(context)
                updateStockReceiveDetails(acceptedTraysDetailList)
            }
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = "Scan Location/Tray"
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
        if (RuntimePermissionHelper.checkCameraPermission(
                this,
                Constants.CAMERA_PERMISSION
            )
        ) {
            initMlScanner()
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
                .setBarcodeFormats(Barcode.FORMAT_CODE_39)
//                .setBarcodeFormats(
//                    Barcode.FORMAT_CODE_128, Barcode.FORMAT_CODE_39,
//                    Barcode.FORMAT_CODE_93, Barcode.FORMAT_CODABAR,
//                    Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8,
//                    Barcode.FORMAT_ITF, Barcode.FORMAT_UPC_A,
//                    Barcode.FORMAT_UPC_E, Barcode.FORMAT_QR_CODE,
//                    Barcode.FORMAT_PDF417, Barcode.FORMAT_AZTEC,
//                    Barcode.FORMAT_DATA_MATRIX
//                )
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
//            showAlert(context, rawValue)
            //val c = "\\b-\\b".toRegex().findAll(rawValue).count()
            if (rawValue.isNotEmpty()){
                if (scannerType == "loc" && !rawValue.contains("/")){
                    showAlert(context,"Scanned Location is not valid!", object: DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            initMlScanner()
                        }

                    })
                }
                else if (scannerType == "tray" && !rawValue.contains("-")){
                    showAlert(context,"Scanned Tray is not valid!", object: DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            initMlScanner()
                        }

                    })
                }
                else{
                    if (scannerType == "loc"){
                        scannedLocationValue = rawValue
                        scannerType = "tray"
                        binding.noteTextview.text = "Note: Scan the Trays!"
                        initMlScanner()
                    }
                    else
                    {
                        val trayId = rawValue
                        showQuantityDialog(trayId)
                    }
                }
            }

//            if (rawValue.isNotEmpty() && scannerType == "loc") { // if scanText contain a dash - It mean you have scan the sample
////                rackId = rawValue
////                getSampleLocationDetails(rackId)
//                scannedLocationValue = rawValue
////                if (selectedSample!!.RACK_ID == rawValue) {
////                    updateSampleReceive(selectedSample!!.SAMPLE_TRANS_ID, user!!.USER_ID)
////                } else {
////                    showAlert(context, "SCANNED WRONG LOCATION")
//                scannerType = "tray"
//                binding.noteTextview.text = "Note: Scan the Trays!"
//                initMlScanner()
////                }
//
//            } else if (rawValue.isNotEmpty() && scannerType == "tray") {
//                val trayId = rawValue
//                showQuantityDialog(trayId)
//            }
        }
    }

    private fun showQuantityDialog(trayId: String) {
        val layout = LayoutInflater.from(context)
            .inflate(R.layout.tray_qunatity_receive_dialog, null)
        val rejectionQtyInputBox =
            layout.findViewById<TextInputEditText>(R.id.tray_qty_input)
        val cancelBtn = layout.findViewById<AppCompatButton>(R.id.rec_cancel_btn)
        val saveBtn = layout.findViewById<AppCompatButton>(R.id.rec_save_btn)
        val builder = MaterialAlertDialogBuilder(context)
        builder.setView(layout)
        builder.setCancelable(false)
        val alert = builder.create()
        alert.show()

        cancelBtn.setOnClickListener {
            initMlScanner()
            alert.dismiss()
        }
        saveBtn.setOnClickListener {
            alert.dismiss()
            val value = rejectionQtyInputBox.text.toString().trim()
            previousScanTraySum += value.toInt()
            if (previousScanTraySum < stock!!.QUANTITY.toInt()) {
                if(binding.saveBtn.visibility == View.VISIBLE){
                    binding.saveBtn.visibility = View.GONE
                }
                    initMlScanner()

                showScanTrayDetails(trayId, value)
                scannerType = "loc"
                binding.noteTextview.text = "Note: Scan the Location!"
            } else if (previousScanTraySum > stock!!.QUANTITY.toInt()) {
                previousScanTraySum -= value.toInt()
                showAlert(
                    context, "Trays Quantity cannot be greater then receive Quantity!"
                ) { dialog, which -> showQuantityDialog(trayId) }
            }
            else{
                showScanTrayDetails(trayId, value)
                if(binding.saveBtn.visibility == View.GONE){
                    binding.saveBtn.visibility = View.VISIBLE
                }
            }
        }
    }

    var index = 0
    private fun updateStockReceiveDetails(list: MutableList<StockObject>) {
        val item = list[index]
        viewModel.callStockReceiveUpdate(
            context,
            item.DOC_ID,
            item.DOC_YEAR,
            item.DOC_TYPE,
            item.QUANTITY,
            item.RATE,
            item.SC_ID,
            item.USER_ID,
            user!!.PASSO,
            item.STORE_ID,
            item.RACK_ID,
            item.TRAY_ID
        )
        viewModel.getStockReceiveUpdateResponse().observe(this, Observer { response ->

            if (response != null) {
                if (response.get("status").asInt == 200) {
                    if (index == list.size-1){
                        dismiss()
                        index = 0
                        finish()
                    }
                    else{
                        index ++
                        updateStockReceiveDetails(list)
                    }
                }
                else {
                    dismiss()
                    showAlert(
                        context,
                        response.get("message").asString
                    )
                }
            } else {
                dismiss()
                showAlert(
                    context,
                    "Something went wrong with server, please try again!"
                ) { dialog, which ->
                    dialog!!.dismiss()
                }
            }
        })
    }

    private fun showScanTrayDetails(trayId: String, value: String) {
        val item = scannedLocationValue.split("/")
        acceptedTraysDetailList.add(
            StockObject(
                stock!!.DOC_ID,
                stock!!.DOC_YEAR,
                stock!!.DOC_TYPE,
                value,
                stock!!.RATE,
                stock!!.SC_ID,
                user!!.USER_ID,
                item[0],
                item[1],
                trayId
            )
        )
        if (binding.table1HeadingsLayout.visibility == View.GONE) {
            binding.table1HeadingsLayout.visibility = View.VISIBLE
        }
        if (binding.scannedTrayDetailWrapper.visibility == View.GONE) {
            binding.scannedTrayDetailWrapper.visibility = View.VISIBLE
        }
        val itemLayout = StockReceiveDetailItemRowBinding.inflate(LayoutInflater.from(context))

        itemLayout.srdItemDocNo.text = item[0]
        itemLayout.srdItemStoreCode.text = trayId
        itemLayout.srdItemDateTime.text = item[1]
        itemLayout.srdItemQuantity.text = value
        binding.scannedTrayDetailWrapper.addView(itemLayout.root)
        if (binding.scannedTrayDetailWrapper.childCount % 2 == 0) {
            itemLayout.root.setBackgroundColor(Color.parseColor("#EAEAF6"))
        } else {
            itemLayout.root.setBackgroundColor(Color.parseColor("#f2f2f2"))
        }


    }
}