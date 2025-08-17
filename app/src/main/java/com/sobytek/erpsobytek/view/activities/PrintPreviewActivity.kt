package com.sobytek.erpsobytek.view.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import com.sobytek.erpsobytek.R
import com.sobytek.erpsobytek.databinding.ActivityPrintPreviewBinding
import com.sobytek.erpsobytek.model.LotDetail
import com.sobytek.erpsobytek.utils.CustomPrintDocumentAdapter
import com.sobytek.erpsobytek.utils.PdfConversionCallback
import com.sobytek.erpsobytek.utils.PdfConverter

class PrintPreviewActivity : BaseActivity() {
     private lateinit var binding:ActivityPrintPreviewBinding

     private lateinit var context: Context

     private var detail:LotDetail?=null
     private var flag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrintPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this

        if(intent != null && intent.hasExtra("DETAILS")){
            detail = intent.getSerializableExtra("DETAILS") as LotDetail

            if(detail!!.ISSUE_QTY != null && detail!!.REC_QTY == null){
                binding.qtyHeadingView.text = "Issue\nQty"
                binding.workerView.text = detail!!.NAME
                binding.operationView.text =detail!!.OPERATION_ID
                binding.receiveDateView.text =detail!!.ISSUE_DATETIME
                binding.lotIdView.text = detail!!.LOT_ID
                binding.cPositionView.text = detail!!.CPOSITION
                binding.storeCodeView.text = detail!!.STORE_CODE
                binding.itemDescView.text = detail!!.ITEM_DESCRIPTION
                binding.recQtyView.text = detail!!.ISSUE_QTY
                binding.specialInstructionView.text = if(detail!!.SPECIAL_INS == null){""}else{detail!!.SPECIAL_INS.toString()}
                binding.totalView.text = detail!!.ISSUE_QTY
            }
            else{
                binding.qtyHeadingView.text = "Rec\nQty"
                binding.workerView.text = detail!!.NAME
                binding.operationView.text =detail!!.OPERATION_ID
                binding.receiveDateView.text =detail!!.REC_DATETIME
                binding.lotIdView.text = detail!!.LOT_ID
                binding.cPositionView.text = detail!!.CPOSITION
                binding.storeCodeView.text = detail!!.STORE_CODE
                binding.itemDescView.text = detail!!.ITEM_DESCRIPTION
                binding.recQtyView.text = detail!!.REC_QTY
                binding.specialInstructionView.text = if(detail!!.SPECIAL_INS == null){""}else{detail!!.SPECIAL_INS.toString()}
                binding.totalView.text = detail!!.REC_QTY
            }

        }

        Handler().postDelayed({
            if(detail != null) {
                val pdfConverter = PdfConverter.getInstance(context)
                pdfConverter.convertXmlToPdf(
                    this@PrintPreviewActivity,
                    detail!!.LOT_ID,
                    detail!!.OPERATION_NO,
                    binding.printWrapperLayout,
                    object :
                        PdfConversionCallback {
                        override fun onPdfConversionComplete(filePath: String) {
                            // Do something with the PDF file path
                            Log.d("PdfConverter", "PDF saved at: $filePath")

                            val printManager: PrintManager =
                                getSystemService(Context.PRINT_SERVICE) as PrintManager
                            try {
                                flag = true
                                val printAdapter = CustomPrintDocumentAdapter(context, filePath)
                                printManager.print(
                                    "Document",
                                    printAdapter,
                                    PrintAttributes.Builder().build()
                                )

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onPdfConversionFailed(error: String) {
                            // Handle conversion failure
                            Log.e("PdfConverter", "PDF conversion failed: $error")
                        }
                    })
            }
        },300)
    }

    override fun onResume() {
        super.onResume()
        if(flag){
            finish()
        }
    }
}