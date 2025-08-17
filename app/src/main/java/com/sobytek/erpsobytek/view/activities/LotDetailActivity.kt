package com.sobytek.erpsobytek.view.activities

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sobytek.erpsobytek.R
import com.sobytek.erpsobytek.adapters.LotDetailAdapter
import com.sobytek.erpsobytek.adapters.WorkerAdapter
import com.sobytek.erpsobytek.databinding.ActivityLotDetailBinding
import com.sobytek.erpsobytek.databinding.LotIssueReceiveBottomsheetDialogBinding
import com.sobytek.erpsobytek.model.LotDetail
import com.sobytek.erpsobytek.model.User
import com.sobytek.erpsobytek.model.Worker
import com.sobytek.erpsobytek.utils.AppSettings
import com.sobytek.erpsobytek.utils.CustomPrintDocumentAdapter
import com.sobytek.erpsobytek.utils.PdfConversionCallback
import com.sobytek.erpsobytek.utils.PdfConverter
import com.sobytek.erpsobytek.viewmodel.LotDetailActivityViewModel
import com.sobytek.erpsobytek.viewmodelfactory.ViewModelFactory
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.io.DataOutputStream
import java.io.File
import java.lang.Double.parseDouble
import java.net.Socket


class LotDetailActivity : BaseActivity() {

    private lateinit var bsBinding: LotIssueReceiveBottomsheetDialogBinding
    private lateinit var context: Context
    private var lot_id: Int? = null
    private lateinit var binding: ActivityLotDetailBinding
    private lateinit var adapter: LotDetailAdapter
    private var detailList = mutableListOf<LotDetail>()
    private lateinit var viewModel: LotDetailActivityViewModel
    private var user: User? = null
    private lateinit var appSettings: AppSettings
    var bottomSheet: BottomSheetDialog? = null
    var workersList = mutableListOf<Worker>()
    var originalWorkersList = mutableListOf<Worker>()
    private lateinit var workerAdapter: WorkerAdapter
    private var from = "lot"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityLotDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setUpToolbar()
        //displayLotDetails()
    }

    private fun initViews() {
        context = this
        appSettings = AppSettings(context)
        user = appSettings.getUser("USER")
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(LotDetailActivityViewModel()).createFor()
        )[LotDetailActivityViewModel::class.java]
        if (intent != null && intent.hasExtra("LOT_ID")) {
            lot_id = intent.getIntExtra("LOT_ID", 0)
        }
        if (intent != null && intent.hasExtra("FROM")){
            from = intent.getStringExtra("FROM") as String
        }

        binding.lotDetailRecyclerview.layoutManager = LinearLayoutManager(context)
        binding.lotDetailRecyclerview.hasFixedSize()
        adapter = LotDetailAdapter(context, detailList as ArrayList<LotDetail>)
        binding.lotDetailRecyclerview.adapter = adapter

    }

    override fun onResume() {
        super.onResume()
        getLotDetails()
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = if(from == "lot"){
            getString(R.string.lot_detail)
        }
        else{
            getString(R.string.supplier_lot_detail)
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(context, R.color.black))
    }

    private fun getLotDetails() {

            startLoading(context)
            if(from == "lot"){
                viewModel.callLot(context, lot_id!!,user!!.USER_ID,user!!.PASSO)
                viewModel.getLotResponse().observe(this) { response ->
                    dismiss()
                    if (response != null) {
                        if (response.status == 200) {
                            if (response.operation == "issue") {
                                val lot = response.lotDetail
                                val detail = lot[0]
                                binding.lotIdTv.text = detail.LOT_ID
                                binding.cPositionTv.text = detail.CPOSITION
                                binding.storeCodeTv.text = detail.STORE_CODE
                                binding.itemDescTv.text = detail.ITEM_DESCRIPTION
                                binding.lotQuantityTv.text = if (detail.ISSUE_QTY.isNullOrEmpty()){"0"}else{detail.ISSUE_QTY}
                                binding.orderQuantityTv.text = if (detail.ORDER_QTY.isNullOrEmpty()){"0"}else{detail.ORDER_QTY}
                                binding.specialInstructionTv.text = if (detail.SPECIAL_INS == null){"N/A"}else{detail.SPECIAL_INS.toString()}
                                binding.customerItemDescriptionTv.text = if (detail.CUSTOMER_ITEM_DESC.isNullOrEmpty()){"N/A"}else{detail.CUSTOMER_ITEM_DESC}
                                binding.customerCodeTv.text = if (detail.CUSTOMER_CODE.isNullOrEmpty()){"N/A"}else{detail.CUSTOMER_CODE}
                                displayLotDetails(lot)
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
                }
            }
        else{
                viewModel.callSupplierLot(context, lot_id!!,user!!.USER_ID,user!!.PASSO)
                viewModel.getSupplierLotResponse().observe(this) { response ->
                    dismiss()
                    if (response != null) {
                        if (response.status == 200) {
                            if (response.operation == "issue") {
                                val lot = response.lotDetail
                                val detail = lot[0]
                                binding.lotIdTv.text = detail.LOT_ID
                                binding.cPositionTv.text = detail.CPOSITION
                                binding.storeCodeTv.text = detail.STORE_CODE
                                binding.itemDescTv.text = detail.ITEM_DESCRIPTION
                                binding.lotQuantityTv.text = if (detail.ISSUE_QTY.isNullOrEmpty()){"0"}else{detail.ISSUE_QTY}
                                binding.orderQuantityTv.text = if (detail.ORDER_QTY.isNullOrEmpty()){"0"}else{detail.ORDER_QTY}
                                binding.specialInstructionTv.text = if (detail.SPECIAL_INS == null){"N/A"}else{detail.SPECIAL_INS.toString()}
                                binding.customerItemDescriptionTv.text = if (detail.CUSTOMER_ITEM_DESC.isNullOrEmpty()){"N/A"}else{detail.CUSTOMER_ITEM_DESC}
                                binding.customerCodeTv.text = if (detail.CUSTOMER_CODE.isNullOrEmpty()){"N/A"}else{detail.CUSTOMER_CODE}
                                displayLotDetails(lot)
                            }
                            else if (response.operation == "receive"){
                                val lot = response.outputDetail
                                val detail = lot[0]
                                binding.lotIdTv.text = detail.LOT_ID
                                binding.cPositionTv.text = detail.CPOSITION
                                binding.storeCodeTv.text = detail.STORE_CODE
                                binding.itemDescTv.text = detail.ITEM_DESCRIPTION
                                binding.lotQuantityTv.text = if (detail.ISSUE_QTY.isNullOrEmpty()){"0"}else{detail.ISSUE_QTY}
                                binding.orderQuantityTv.text = if (detail.ORDER_QTY.isNullOrEmpty()){"0"}else{detail.ORDER_QTY}
                                binding.specialInstructionTv.text = if (detail.SPECIAL_INS == null){"N/A"}else{detail.SPECIAL_INS.toString()}
                                binding.customerItemDescriptionTv.text = if (detail.CUSTOMER_ITEM_DESC.isNullOrEmpty()){"N/A"}else{detail.CUSTOMER_ITEM_DESC}
                                binding.customerCodeTv.text = if (detail.CUSTOMER_CODE.isNullOrEmpty()){"N/A"}else{detail.CUSTOMER_CODE}
                                displayLotDetails(lot)
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
                }
            }

    }

    lateinit var outToServer: DataOutputStream
    lateinit var clientSocket: Socket
    private fun displayLotDetails(lot: ArrayList<LotDetail>) {

            if (lot.isNotEmpty()) {
                detailList.clear()
            }
            detailList.addAll(lot)
            adapter.notifyItemRangeChanged(0, detailList.size)
            adapter.setOnItemClickListener(object : LotDetailAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val detail = detailList[position]
                    if (detail.ISSUE_QTY != null && detail.ISSUE_QTY!!.isNotEmpty()){
                       showAlert(context,"This operation is already issued!")
                    }
                    else{
                        if (workersList.isEmpty()) {
                            startLoading(context)
                            viewModel.callWorkers(context,user!!.USER_ID,user!!.PASSO,detail.OPERATION_ID)
                            viewModel.getWorkersResponse().observe(this@LotDetailActivity, Observer { response ->
                                dismiss()
                                if (response != null) {
                                    originalWorkersList.clear()
                                    workersList.clear()
                                    originalWorkersList.addAll(response)
                                    if (originalWorkersList.size > 0) {
                                        workersList.addAll(originalWorkersList)
//                                        workerAdapter.notifyItemRangeChanged(0, workersList.size)
                                    }
                                    openBottomSheer(detail,position)
                                } else {
                                    showAlert(context, "Something wrong with server, try again!")
                                }
                            })
                        } else {
                            //workerAdapter.notifyItemRangeChanged(0, workersList.size)
                            openBottomSheer(detail,position)
                        }
                        //openBottomSheer(detail,position)
                    }

                }

                override fun onItemLongPress(position: Int,parentView:View) {
                    val detail = detailList[position]
                    if(from != "lot"){
                        if((detail.ISSUE_QTY != null && detail.REC_QTY == null) || detail.REC_QTY !=null) {
                            showPrintPopupMenu(detail, parentView, onPrintClicked = {
                                startActivity(
                                    Intent(
                                        context,
                                        PrintPreviewActivity::class.java
                                    ).apply {
                                        putExtra("DETAILS", detail)
                                    })

                            })
                        }
                        else{

                        }
                    }
                }

            })

    }

    fun showPrintPopupMenu(detail: LotDetail,view: View, onPrintClicked: () -> Unit) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.print_menu, popupMenu.menu)
        popupMenu.menu.findItem(R.id.menu_print).setTitle("Print Operation No: ${detail.OPERATION_NO}")
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_print -> {
                    onPrintClicked.invoke()
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private var tempLotDetail:LotDetail?=null
    private var tempPos:Int?=null
    private fun openBottomSheer(detail: LotDetail,pos:Int) {
            tempLotDetail = detail
            tempPos = pos
        if (from == "lot") {
            viewModel.callLot(context, detail.LOT_ID.toInt(), user!!.USER_ID, user!!.PASSO)
            viewModel.getLotResponse().observe(this, Observer { response ->
                dismiss()
                if (response != null) {
                    if (response.status == 200) {
                        if (response.operation == "issue") {
                            val intent = Intent(context, ScanLotDetailActivity::class.java)
                            resultLauncher.launch(intent)
                        } else {
                            MaterialAlertDialogBuilder(context)
                                .setMessage("Lot not received yet!")
                                .setPositiveButton("Ok") { dialog, which ->
                                    dialog.dismiss()
                                }
                                .setCancelable(false)
                                .create().show()
                        }
//                    else if (response.operation == "done"){
//                        MaterialAlertDialogBuilder(context)
//                            .setMessage("All the operation has been completed!")
//                            .setPositiveButton("Ok"){dialog,which->
//                                dialog.dismiss()
//                                //bindingMainContent.scanner.resumeCameraPreview(this)
//                            }
//                            .setCancelable(false)
//                            .create().show()
//                    }
//                    else {
//                        if(response.lotDetail[0].op_id == null){
//                            showAlert(context,"Scanned Lot Id not available!",object :DialogInterface.OnClickListener{
//                                override fun onClick(dialog: DialogInterface?, which: Int) {
//                                    dialog!!.dismiss()
//                                    //bindingMainContent.scanner.resumeCameraPreview(this@MainActivity)
//                                }
//
//                            })
//                        }
//                        else{
//                            val recDetail = response.lotDetail[0]
//                            //displayReceiveLotDialog(recDetail,lotId)
//                        }
//
//                    }
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
        }
        else{
            viewModel.callSupplierLot(context, detail.LOT_ID.toInt(),user!!.USER_ID,user!!.PASSO)
            viewModel.getSupplierLotResponse().observe(this, Observer { response ->
                dismiss()
                if (response != null) {
                    if (response.status == 200) {
                        if (response.operation == "issue") {
                          startActivity(Intent(context,SuppliersActivity::class.java).apply {
                              putExtra("LOT_ID",detail.LOT_ID.toInt())
                              putExtra("OP_NO",detail.OPERATION_NO.toInt())
                          })
                        }
                        else{
                            MaterialAlertDialogBuilder(context)
                                .setMessage("Lot not received yet!")
                                .setPositiveButton("Ok"){dialog,which->
                                    dialog.dismiss()
                                }
                                .setCancelable(false)
                                .create().show()
                        }
//                    else if (response.operation == "done"){
//                        MaterialAlertDialogBuilder(context)
//                            .setMessage("All the operation has been completed!")
//                            .setPositiveButton("Ok"){dialog,which->
//                                dialog.dismiss()
//                                //bindingMainContent.scanner.resumeCameraPreview(this)
//                            }
//                            .setCancelable(false)
//                            .create().show()
//                    }
//                    else {
//                        if(response.lotDetail[0].op_id == null){
//                            showAlert(context,"Scanned Lot Id not available!",object :DialogInterface.OnClickListener{
//                                override fun onClick(dialog: DialogInterface?, which: Int) {
//                                    dialog!!.dismiss()
//                                    //bindingMainContent.scanner.resumeCameraPreview(this@MainActivity)
//                                }
//
//                            })
//                        }
//                        else{
//                            val recDetail = response.lotDetail[0]
//                            //displayReceiveLotDialog(recDetail,lotId)
//                        }
//
//                    }
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
        }

            //bottomSheet = BottomSheetDialog(context)

//            bsBinding =
//                LotIssueReceiveBottomsheetDialogBinding.inflate(layoutInflater, null, false)
//            bottomSheet!!.setContentView(bsBinding.root)
//            bottomSheet!!.behavior.state = BottomSheetBehavior.STATE_EXPANDED
//        bsBinding.scanner.setBorderColor(ContextCompat.getColor(context, R.color.white))
//        bsBinding.scanner.setSquareViewFinder(true)
//        bsBinding.scanner.setLaserEnabled(true)
//        bsBinding.scanner.setAutoFocus(true)
//        bsBinding.scanner.setOnClickListener {
//            bsBinding.scanner.resumeCameraPreview(scannerCallbackHandler)
//        }
//            bsBinding.scanner.setBorderColor(ContextCompat.getColor(context, R.color.white))
//            bsBinding.scanner.setSquareViewFinder(true)
//            bsBinding.scanner.setLaserEnabled(true)
//            bsBinding.scanner.setOnClickListener {
//                bsBinding.scanner.resumeCameraPreview(scannerCallbackHandler)
//                }
//            bsBinding.scanner.resumeCameraPreview(scannerCallbackHandler)
//            bsBinding.scanner.startCamera()
//            bsBinding.bsLotIdTv.text = detail.LOT_ID
//            bsBinding.bsLotOperationTv.text = detail.OPERATION_ID
//            bsBinding.bsCloseView.setOnClickListener { bottomSheet!!.dismiss() }
//            bsBinding.workerRecyclerview.layoutManager = WrapContentLinearLayoutManager(context,RecyclerView.VERTICAL,false)
//            bsBinding.workerRecyclerview.hasFixedSize()
//            workerAdapter = WorkerAdapter(context, workersList as ArrayList<Worker>)
//            bsBinding.workerRecyclerview.adapter = workerAdapter
//            workerAdapter.setOnItemClickListener(object : WorkerAdapter.OnItemClickListener {
//                override fun onItemClick(position: Int) {
//                    val worker = workersList[position]
//                    lotIssue(bottomSheet!!, worker, detail,pos)
//                }
//            })

//            bottomSheet!!.setOnDismissListener {
//                bsBinding.scanner.resumeCameraPreview(scannerCallbackHandler)
//                bsBinding.scanner.startCamera()
//            }


//            bsBinding.searchInput.addTextChangedListener(object :TextWatcher{
//                override fun beforeTextChanged(
//                    s: CharSequence?,
//                    start: Int,
//                    count: Int,
//                    after: Int
//                ) {
//
//                }
//
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//                }
//
//                override fun afterTextChanged(s: Editable?) {
//                    if (s.toString().isEmpty()) {
//                        workersList.clear()
//                        workersList.addAll(originalWorkersList)
//                        workerAdapter.notifyItemRangeChanged(0, workersList.size)
//                    } else {
//                        val query = s.toString()
//                        search(query)
//                    }
//                }
//
//            })
//            if (workersList.isEmpty()) {
//                startLoading(context)
//                viewModel.callWorkers(context,user!!.USER_ID,detail.OPERATION_ID)
//                viewModel.getWorkersResponse().observe(this, Observer { response ->
//                    dismiss()
//                    if (response != null) {
//                        bottomSheet!!.show()
//                        originalWorkersList.clear()
//                        workersList.clear()
//                        originalWorkersList.addAll(response)
//                        if (originalWorkersList.size > 0) {
//                            workersList.addAll(originalWorkersList)
//                            workerAdapter.notifyItemRangeChanged(0, workersList.size)
//                        }
//                    } else {
//                        showAlert(context, "Something wrong with server, try again!")
//                    }
//                })
//            } else {
//                workerAdapter.notifyItemRangeChanged(0, workersList.size)
//            }

    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val scanText = data!!.getStringExtra("TEXT") as String
            val worker:Worker? = getWorker(scanText)
            if(worker != null){
                lotIssue(worker,tempLotDetail!!,tempPos!!)
            } else{
                showAlert(context,"Scanned EmployeeId not found!") { dialog, which ->
                    dialog!!.dismiss()
                }
            }
        }
    }

    private fun search(text: String?) {
        val matchedWorkers = mutableListOf<Worker>()

        text?.let {
            workersList.forEach { item ->
                if (item.NAME.contains(text, true)) {
                    matchedWorkers.add(item)
                }
            }

            if (matchedWorkers.isEmpty()) {
                Toast.makeText(this, "No match found!", Toast.LENGTH_SHORT).show()
            } else {
                workersList.clear()
                workersList.addAll(matchedWorkers)
                workerAdapter.notifyItemRangeChanged(0, workersList.size)
            }
        }
    }

    private var scannerCallbackHandler = ZXingScannerView.ResultHandler { result ->
            val scanText = result!!.text
            var numeric = true
            try {
                val num = parseDouble(scanText)
            } catch (e: NumberFormatException) {
                numeric = false
            }
            if (numeric){
                val worker:Worker? = getWorker(scanText)
                if(worker != null){
                    lotIssue(worker,tempLotDetail!!,tempPos!!)
                } else{
                    showAlert(context,"Scanned EmployeeId not found!") { dialog, which ->
                        dialog!!.dismiss()
                       // bsBinding.scanner.resumeCameraPreview(this)
                        bsBinding.scanner.startCamera()
                    }
                }
            } else{
                showAlert(context,"Scanned text not a EmployeeId", object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog!!.dismiss()
                        //bsBinding.scanner.resumeCameraPreview(scannerCallbackHandler as ZXingScannerView.ResultHandler)
                        bsBinding.scanner.startCamera()
                    }

                })
            }
        }

    private fun getWorker(employeeId: String?): Worker? {
        return try {
            var foundWorker:Worker?=null
            if (workersList.isNotEmpty()){
                for (i in 0 until workersList.size){
                    val item = workersList[i]
                    val empId:Int = employeeId!!.trim().toInt()
                    if (item.WORKER_ID != null && item.WORKER_ID.toInt() == empId){
                        foundWorker = item
                        break
                    }
                }
            }
            foundWorker!!
        }
        catch (e:Exception){
            e.printStackTrace()
            null
        }

    }

    private fun lotIssue(worker: Worker, detail: LotDetail,position: Int) {

            //bottomSheet.dismiss()
            if (user != null) {
                startLoading(context)
                viewModel.callLotIssue(
                    context,
                    worker.WORKER_ID!!.toInt(),
                    user!!.USER_ID,
                    user!!.PASSO,
                    detail.LOT_ID.toInt(),
                    detail.OPERATION_NO.toInt()
                )
                viewModel.getLotIssueResponse().observe(this@LotDetailActivity) { response ->
                    if (response != null) {
                        dismiss()

                        if (response.get("status").asString == "200") {
                            val issueQty = response.get("issue_qty").asInt
                            detail.ISSUE_QTY = "$issueQty"
                            detail.NAME = worker.NAME
                            detailList.removeAt(position)
                            detailList.add(position, detail)
                            adapter.notifyItemChanged(position)
                        } else {
                            showAlert(context, response.get("message").asString)
                        }
                    }
                }
            }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}