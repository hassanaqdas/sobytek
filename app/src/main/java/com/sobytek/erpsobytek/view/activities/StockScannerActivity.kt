package com.sobytek.erpsobytek.view.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.graphics.RectF
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
import com.google.android.material.textview.MaterialTextView
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.sobytek.erpsobytek.R
import com.sobytek.erpsobytek.databinding.ActivityStockScannerBinding
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

class StockScannerActivity : BaseActivity() {

    private lateinit var context: Context
    private lateinit var binding: ActivityStockScannerBinding
    private var user: User? = null
    private lateinit var appSettings: AppSettings
    private var trayItem: Stock? = null
    private var cameraProviderFuture: ListenableFuture<*>? = null
    private var cameraExecutor: ExecutorService? = null
    private var imageAnalyzer: MyImageAnalyzer? = null
    private lateinit var cam: Camera
    private lateinit var viewModel: StockViewModel
    private var tempQuantity = 0
//    private lateinit var barcodeBoxView: BarcodeBoxView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setUpToolbar()
//        initBarcodeScanner()
    }

    private fun initViews() {
        context = this
//        barcodeBoxView = BarcodeBoxView(this)
//        addContentView(barcodeBoxView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        appSettings = AppSettings(context)
        user = appSettings.getUser("USER")
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(StockViewModel()).createFor()
        )[StockViewModel::class.java]

        if (intent != null && intent.hasExtra("ITEM")) {
            trayItem = intent.getSerializableExtra("ITEM") as Stock
            tempQuantity =
                if (Constants.tempQuantity == 0 || Constants.tempQuantity == Constants.stockIssueReqObject!!.QUANTITY.toInt()) {
                    Constants.stockIssueReqObject!!.QUANTITY.toInt()
                } else {
                    Constants.tempQuantity
                }

        }


        binding.previewview.setOnClickListener {
            initMlScanner()
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = "Scan the Tray"
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

        imageAnalyzer = MyImageAnalyzer(this)
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
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build().also {
                        cameraExecutor?.let { it1 ->
                            imageAnalyzer?.let { it2 ->
                                it.setAnalyzer(
                                    it1,
                                    MyImageAnalyzer(
                                        this
                                    )
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

    inner class MyImageAnalyzer(private val context: Context) : ImageAnalysis.Analyzer {
        var count = 0

        private var scaleX = 1f
        private var scaleY = 1f

        private fun translateX(x: Float) = x * scaleX
        private fun translateY(y: Float) = y * scaleY

        private fun adjustBoundingRect(rect: Rect) = RectF(
            translateX(rect.left.toFloat()),
            translateY(rect.top.toFloat()),
            translateX(rect.right.toFloat()),
            translateY(rect.bottom.toFloat())
        )

        private var fragmentManager: FragmentManager? = null

        init {
            this.fragmentManager = supportFragmentManager
        }

        override fun analyze(image: ImageProxy) {
            scanBarCode(image)
        }


        private fun scanBarCode(image: ImageProxy) {

            @SuppressLint("UnsafeOptInUsageError") val img = image.image!!

//                scaleX = previewViewWidth / img.height.toFloat()
//                scaleY = previewViewHeight / img.width.toFloat()

            val inputImage = InputImage.fromMediaImage(img, image.imageInfo.rotationDegrees)
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
                    if (barcodes.isNotEmpty()) {
                        // Update bounding rect
                        for (barcode in barcodes) {
                            val rawValue = barcodes[0].rawValue
                            val valueType = barcodes[0].valueType
                            // See API reference for complete list of supported types
                            Log.d("TEST199", "readBarCodeData: $rawValue")
                            if (barcodes[0].rawValue != null) {
                                cameraExecutor!!.shutdownNow()
                                onScanResult(rawValue!!)
                            }
                            // Update bounding rect
//                            barcode.boundingBox?.let { rect ->
//                                barcodeBoxView.setRect(
//                                    adjustBoundingRect(
//                                        rect
//                                    )
//                                )
//                            }
                        }
                        //readBarCodeData(barcodes)
                    } else {
//                        barcodeBoxView.setRect(RectF())
                    }
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

            }
        }
    }

    private fun onScanResult(rawValue: String) {
        if (rawValue.isNotEmpty()) {
            playSound(true)
            generateVibrate()

            if (trayItem!!.TRAY_ID == rawValue) {
                   showQuantityDialog(trayItem!!)
//                if (tempQuantity > trayItem!!.QUANTITY.toInt()) {
//                    tempQuantity -= trayItem!!.QUANTITY.toInt()
//                    Constants.tempQuantity = tempQuantity
//                    Constants.acceptedTraysDetailList.add(
//                        StockObject(
//                            Constants.stockIssueReqObject!!.DOC_ID,
//                            Constants.stockIssueReqObject!!.DOC_YEAR,
//                            Constants.stockIssueReqObject!!.DOC_TYPE,
//                            trayItem!!.QUANTITY,
//                            Constants.stockIssueReqObject!!.RATE,
//                            Constants.stockIssueReqObject!!.SC_ID,
//                            user!!.USER_ID,
//                            trayItem!!.STORE_ID,
//                            trayItem!!.RACK_ID,
//                            trayItem!!.TRAY_ID
//                        )
//                    )
//                    setResult(RESULT_OK, Intent().apply { putExtra("FLAG", "1") })
//                    finish()
//                }
//                else
//                {
//                    Constants.acceptedTraysDetailList.add(
//                        StockObject(
//                            Constants.stockIssueReqObject!!.DOC_ID,
//                            Constants.stockIssueReqObject!!.DOC_YEAR,
//                            Constants.stockIssueReqObject!!.DOC_TYPE,
//                            "$tempQuantity",
//                            Constants.stockIssueReqObject!!.RATE,
//                            Constants.stockIssueReqObject!!.SC_ID,
//                            user!!.USER_ID,
//                            trayItem!!.STORE_ID,
//                            trayItem!!.RACK_ID,
//                            trayItem!!.TRAY_ID
//                        )
//                    )
//
//                    // CALL API UPDATE ISSUEABLE
//                    if (Constants.acceptedTraysDetailList.size > 0) {
//                        startLoading(context)
//                        updateStockIssueDetails(Constants.acceptedTraysDetailList)
//                    }
//                }
            } else {
                showAlert(
                    context,
                    "You scanned the wrong tray, please scan the right tray.",
                    object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            initMlScanner()
                        }

                    })
            }
        }
    }

    private fun showQuantityDialog(trayItem:Stock) {
        val layout = LayoutInflater.from(context)
            .inflate(R.layout.tray_qunatity_receive_dialog, null)
        val rejectionQtyInputBox =
            layout.findViewById<TextInputEditText>(R.id.tray_qty_input)
        val heading = layout.findViewById<MaterialTextView>(R.id.heading)
        heading.text = "TRAY ISSUE QUANTITY"
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
            //val finalTrayQuantity = trayItem.QUANTITY.toInt() - value.toInt()
            if (value.toInt() > trayItem.QUANTITY.toInt())
            {
                showAlert(
                    context,
                    "Entered quantity should not be greater than Tray Quantity!"
                ) { dialog, which ->
                    object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            showQuantityDialog(trayItem)
                        }

                    }
                }
            }
            else{
                if (tempQuantity > value.toInt()) {
                    tempQuantity -= value.toInt()
                    Constants.tempQuantity = tempQuantity
                    Constants.acceptedTraysDetailList.add(
                        StockObject(
                            Constants.stockIssueReqObject!!.DOC_ID,
                            Constants.stockIssueReqObject!!.DOC_YEAR,
                            Constants.stockIssueReqObject!!.DOC_TYPE,
                            value,
                            Constants.stockIssueReqObject!!.RATE,
                            Constants.stockIssueReqObject!!.SC_ID,
                            user!!.USER_ID,
                            trayItem.STORE_ID,
                            trayItem.RACK_ID,
                            trayItem.TRAY_ID
                        )
                    )
                    setResult(RESULT_OK, Intent().apply { putExtra("FLAG", "1") })
                    finish()
                }
                else
                {
                    if (tempQuantity == value.toInt())
                    {
                        Constants.acceptedTraysDetailList.add(
                            StockObject(
                                Constants.stockIssueReqObject!!.DOC_ID,
                                Constants.stockIssueReqObject!!.DOC_YEAR,
                                Constants.stockIssueReqObject!!.DOC_TYPE,
                                value,
                                Constants.stockIssueReqObject!!.RATE,
                                Constants.stockIssueReqObject!!.SC_ID,
                                user!!.USER_ID,
                                trayItem.STORE_ID,
                                trayItem.RACK_ID,
                                trayItem.TRAY_ID
                            )
                        )

                        // CALL API UPDATE ISSUEABLE
                        if (Constants.acceptedTraysDetailList.size > 0) {
                            startLoading(context)
                            updateStockIssueDetails(Constants.acceptedTraysDetailList)
                        }
                    }
                    else{
                        showAlert(
                            context,
                            "Entered quantity should not be greater than Required Quantity!"
                        ) { dialog, which ->
                            object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface?, which: Int) {
                                    showQuantityDialog(trayItem)
                                }

                            }
                        }
                    }
                }

            }

        }
    }

    var index = 0
    private fun updateStockIssueDetails(list: MutableList<StockObject>) {
        val item = list[index]
        viewModel.callStockIssueUpdate(
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
            item.TRAY_ID,
            ""
        )
        viewModel.getStockIssueUpdateResponse().observe(this, Observer { response ->

            if (response != null) {
                if (response.get("status").asInt == 200) {
                    if (index == list.size - 1) {
                        dismiss()
                        index = 0
                        Constants.stockIssueReqObject = null
                        Constants.acceptedTraysDetailList.clear()
                        Constants.tempQuantity = 0
                        setResult(RESULT_OK, Intent().apply { putExtra("FLAG", "2") })
                        finish()
                    } else {
                        index++
                        updateStockIssueDetails(list)
                    }
                } else {
                    dismiss()
                    showAlert(
                        context,
                        response.get("message").asString
                    ) { dialog, which ->
                        object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                initMlScanner()
                            }

                        }
                    }
                }
            } else {
                dismiss()
                showAlert(
                    context,
                    "Something went wrong with server, please try again!"
                ) { dialog, which ->
                    object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            initMlScanner()
                        }

                    }
                }
            }
        })
    }
}