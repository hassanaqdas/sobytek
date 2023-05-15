package com.sobytek.erpsobytek.view.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sobytek.erpsobytek.R
import com.sobytek.erpsobytek.adapters.StockDetailAdapter
import com.sobytek.erpsobytek.databinding.ActivityStockReceiveBinding
import com.sobytek.erpsobytek.model.Stock
import com.sobytek.erpsobytek.model.User
import com.sobytek.erpsobytek.utils.AppSettings
import com.sobytek.erpsobytek.utils.Constants
import com.sobytek.erpsobytek.utils.WrapContentLinearLayoutManager
import com.sobytek.erpsobytek.viewmodel.StockViewModel
import com.sobytek.erpsobytek.viewmodelfactory.ViewModelFactory

class StockReceiveActivity : BaseActivity(), StockDetailAdapter.OnItemClickListener {

    private lateinit var context: Context
    private lateinit var binding: ActivityStockReceiveBinding
    private var user: User? = null
    private lateinit var appSettings: AppSettings
    private var stockReceivableList = mutableListOf<Stock>()
    private lateinit var viewModel: StockViewModel
    private lateinit var adapter: StockDetailAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockReceiveBinding.inflate(layoutInflater)
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

        binding.stockReceiveDetailRecyclerview.layoutManager = WrapContentLinearLayoutManager(context,
            RecyclerView.VERTICAL,false)
        binding.stockReceiveDetailRecyclerview.hasFixedSize()
        adapter = StockDetailAdapter(context, stockReceivableList as ArrayList<Stock>)
        binding.stockReceiveDetailRecyclerview.adapter = adapter
        adapter.setOnItemClickListener(this)


    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = "${getString(R.string.stock_receive_text)} Details"
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

        getStockReceivableList()
    }

    private fun getStockReceivableList() {
        startLoading(context)
        viewModel.callStockReceiveDetails(context)
        viewModel.getStockReceiveDetailsResponse()
            .observe(this, Observer { stockReceiveResponse ->
                dismiss()
                if (stockReceiveResponse != null) {
                    if (stockReceivableList.isNotEmpty()) {
                        stockReceivableList.clear()
                    }
                    stockReceivableList.addAll(stockReceiveResponse)
                    adapter.notifyItemRangeChanged(0, stockReceivableList.size)
                }
                else{
                    stockReceivableList.clear()
                    adapter.notifyDataSetChanged()
                }
            })
    }

    override fun onItemClick(position: Int) {
        Constants.stockIssueReqObject = null
        Constants.acceptedTraysDetailList.clear()
        Constants.tempQuantity = 0
        val stock = stockReceivableList[position]
        startActivity(
            Intent(
                context,
                StockReceiveManipulationActivity::class.java
            ).apply {
                putExtra("STOCK", stock)
            }
        )
    }
}