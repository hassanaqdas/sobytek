package com.sobytek.erpsobytek.view.activities

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sobytek.erpsobytek.R
import com.sobytek.erpsobytek.adapters.IGPDetailAdapter
import com.sobytek.erpsobytek.adapters.WorkerAdapter
import com.sobytek.erpsobytek.databinding.ActivityIgpdetailBinding
import com.sobytek.erpsobytek.databinding.LotIssueReceiveBottomsheetDialogBinding
import com.sobytek.erpsobytek.model.IgpDetail
import com.sobytek.erpsobytek.model.User
import com.sobytek.erpsobytek.model.Worker
import com.sobytek.erpsobytek.utils.AppSettings
import com.sobytek.erpsobytek.viewmodel.IGPActivityViewModel
import com.sobytek.erpsobytek.viewmodelfactory.ViewModelFactory
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.lang.Double

class IGPDetailActivity : BaseActivity() {

    private lateinit var bsBinding: LotIssueReceiveBottomsheetDialogBinding
    private lateinit var context: Context
    private var girId: String? = null
    private lateinit var binding: ActivityIgpdetailBinding
    private lateinit var adapter: IGPDetailAdapter
    private var detailList = mutableListOf<IgpDetail>()
    private lateinit var viewModel: IGPActivityViewModel
    private var user: User? = null
    private lateinit var appSettings: AppSettings
    var bottomSheet: BottomSheetDialog? = null
    var workersList = mutableListOf<Worker>()
    var originalWorkersList = mutableListOf<Worker>()
    private lateinit var workerAdapter: WorkerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIgpdetailBinding.inflate(layoutInflater)
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
            ViewModelFactory(IGPActivityViewModel()).createFor()
        )[IGPActivityViewModel::class.java]
        if (intent != null && intent.hasExtra("GIR_ID")) {
            girId = intent.getStringExtra("GIR_ID")
        }

        binding.igpDetailRecyclerview.layoutManager = LinearLayoutManager(context)
        binding.igpDetailRecyclerview.hasFixedSize()
        adapter = IGPDetailAdapter(context, detailList as ArrayList<IgpDetail>)
        binding.igpDetailRecyclerview.adapter = adapter
        getLotDetails()
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = getString(R.string.igp_detail)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(context, R.color.black))
    }

    private fun getLotDetails() {

        startLoading(context)
        viewModel.callIgp(context, girId!!,user!!.USER_ID,user!!.PASSO)
        viewModel.getIgpResponse().observe(this) { response ->
            dismiss()
            if (response != null) {
                if (response.status == 200) {
                    if (response.operation == "issue") {
                        val igp = response.igpDetail
                        val detail = igp[0]
                        binding.lotIdTv.text = girId!!
                        binding.cPositionTv.text = detail.CPOSITION
                        binding.storeCodeTv.text = detail.STORE_CODE
                        binding.itemDescTv.text = detail.ITEM_DESCRIPTION
                        binding.lotQuantityTv.text = if (detail.ISSUE_QTY.isNullOrEmpty()){"0"}else{detail.ISSUE_QTY}
                        binding.orderQuantityTv.text = if (detail.ORDER_QTY.isNullOrEmpty()){"0"}else{detail.ORDER_QTY}
                        binding.specialInstructionTv.text = if (detail.SPECIAL_INS == null){"N/A"}else{detail.SPECIAL_INS.toString()}
                        binding.customerItemDescriptionTv.text = if (detail.CUSTOMER_ITEM_DESC.isNullOrEmpty()){"N/A"}else{detail.CUSTOMER_ITEM_DESC}
                        binding.customerCodeTv.text = if (detail.CUSTOMER_CODE.isNullOrEmpty()){"N/A"}else{detail.CUSTOMER_CODE}
                        displayIgpDetails(igp)
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

    private fun displayIgpDetails(igpList: ArrayList<IgpDetail>) {

        if (igpList.isNotEmpty()) {
            detailList.clear()
        }
        detailList.addAll(igpList)
        adapter.notifyItemRangeChanged(0, detailList.size)
        adapter.setOnItemClickListener(object : IGPDetailAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val detail = detailList[position]
                if (detail.ISSUE_QTY != null && detail.ISSUE_QTY!!.isNotEmpty()){
                    showAlert(context,"This operation is already issued!")
                }
                else{
                    if (workersList.isEmpty()) {
                        startLoading(context)
                        viewModel.callWorkers(context,user!!.USER_ID,user!!.PASSO,detail.OPERATION_ID as String)
                        viewModel.getWorkersResponse().observe(this@IGPDetailActivity, Observer { response ->
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

        })

    }

    private var tempLotDetail:IgpDetail?=null
    private var tempPos:Int?=null
    private fun openBottomSheer(detail: IgpDetail,pos:Int) {
        tempLotDetail = detail
        tempPos = pos

        viewModel.callIgp(context, girId!!,user!!.USER_ID,user!!.PASSO)
        viewModel.getIgpResponse().observe(this, Observer { response ->
            dismiss()
            if (response != null) {
                if (response.status == 200) {
                    if (response.operation == "issue") {
                        val intent = Intent(context,ScanIgpDetailActivity::class.java)
                        resultLauncher.launch(intent)
                    }
                    else{
                        MaterialAlertDialogBuilder(context)
                            .setMessage("IGP not received yet!")
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
                igpIssue(worker,tempLotDetail!!,tempPos!!)
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
            val num = Double.parseDouble(scanText)
        } catch (e: NumberFormatException) {
            numeric = false
        }
        if (numeric){
            val worker:Worker? = getWorker(scanText)
            if(worker != null){
                igpIssue(worker,tempLotDetail!!,tempPos!!)
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

    private fun igpIssue(worker: Worker, detail: IgpDetail, position: Int) {

        //bottomSheet.dismiss()
        if (user != null) {
            startLoading(context)
            viewModel.callIgpIssue(
                context,
                worker.WORKER_ID!!.toInt(),
                user!!.USER_ID,
                user!!.PASSO,
                girId!!,
                detail.GIR_YEAR as String,
                detail.OPERATION_NO!!.toInt()
            )
            viewModel.getIgpIssueResponse().observe(this@IGPDetailActivity) { response ->
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