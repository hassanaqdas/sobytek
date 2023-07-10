package com.sobytek.erpsobytek.view.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sobytek.erpsobytek.R
import com.sobytek.erpsobytek.adapters.StockDetailAdapter
import com.sobytek.erpsobytek.adapters.StockTraysDetailAdapter
import com.sobytek.erpsobytek.databinding.ActivityGirsSelectedBinding
import com.sobytek.erpsobytek.databinding.ActivityStockTraysBinding
import com.sobytek.erpsobytek.model.Stock
import com.sobytek.erpsobytek.model.User
import com.sobytek.erpsobytek.utils.AppSettings
import com.sobytek.erpsobytek.utils.WrapContentLinearLayoutManager
import com.sobytek.erpsobytek.viewmodel.StockViewModel
import com.sobytek.erpsobytek.viewmodelfactory.ViewModelFactory

class StockTraysActivity : BaseActivity(),StockTraysDetailAdapter.OnItemClickListener {

    private lateinit var context: Context
    private lateinit var binding: ActivityStockTraysBinding
    private var user: User? = null
    private lateinit var appSettings: AppSettings
    private var stockTraysList = mutableListOf<Stock>()
    private lateinit var viewModel: StockViewModel
    private lateinit var adapter: StockTraysDetailAdapter
    private var selectedItem: Stock? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockTraysBinding.inflate(layoutInflater)
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

        if (intent != null && intent.hasExtra("ITEM")) {
            selectedItem = intent.getSerializableExtra("ITEM") as Stock
        }

        binding.stockTraysDetailRecyclerview.layoutManager = WrapContentLinearLayoutManager(context,
            RecyclerView.VERTICAL,false)
        binding.stockTraysDetailRecyclerview.hasFixedSize()
        adapter = StockTraysDetailAdapter(context, stockTraysList as ArrayList<Stock>)
        binding.stockTraysDetailRecyclerview.adapter = adapter
        adapter.setOnItemClickListener(this)

        if (selectedItem != null){
            getStockTraysList(selectedItem!!.DOC_ID,selectedItem!!.DOC_YEAR,selectedItem!!.DOC_TYPE)
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = "${getString(R.string.stock_trays_text)} Details"
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

    private fun getStockTraysList(doc_id: String,doc_year: String,doc_type: String) {
        startLoading(context)
        viewModel.callStockTraysDetails(context,doc_id,doc_year,doc_type,user!!.USER_ID,user!!.PASSO)
        viewModel.getStockTraysDetailsResponse()
            .observe(this, Observer { stockTraysResponse ->
                dismiss()
                if (stockTraysResponse != null) {
                    if (stockTraysList.isNotEmpty()) {
                        stockTraysList.clear()
                    }
                    stockTraysList.addAll(stockTraysResponse)
                    adapter.notifyItemRangeChanged(0, stockTraysList.size)
                }
            })
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onItemClick(position: Int) {
        val item = stockTraysList[position]
        val intent = Intent(
            context,
            StockScannerActivity::class.java
        ).apply {
            putExtra("ITEM", item)
        }
        resultLauncher.launch(intent)
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data!!.hasExtra("FLAG") && data.getStringExtra("FLAG") == "2") {
                setResult(RESULT_OK, Intent().apply { putExtra("FLAG", "2") })
                finish()
            }
            else{
                setResult(RESULT_OK, Intent().apply { putExtra("FLAG", "1") })
                    finish()

            }
        }
    }

}