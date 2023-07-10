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
import com.sobytek.erpsobytek.databinding.ActivityStockIssuenceBinding
import com.sobytek.erpsobytek.model.Stock
import com.sobytek.erpsobytek.model.User
import com.sobytek.erpsobytek.utils.AppSettings
import com.sobytek.erpsobytek.utils.Constants
import com.sobytek.erpsobytek.utils.WrapContentLinearLayoutManager
import com.sobytek.erpsobytek.viewmodel.StockViewModel
import com.sobytek.erpsobytek.viewmodelfactory.ViewModelFactory

class StockIssuenceActivity : BaseActivity(),StockDetailAdapter.OnItemClickListener{

    private lateinit var context: Context
    private lateinit var binding: ActivityStockIssuenceBinding
    private var user: User? = null
    private lateinit var appSettings: AppSettings
    private var stockIssueAbleList = mutableListOf<Stock>()
    private lateinit var viewModel: StockViewModel
    private lateinit var adapter: StockDetailAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockIssuenceBinding.inflate(layoutInflater)
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

        binding.stockIssueDetailRecyclerview.layoutManager = WrapContentLinearLayoutManager(context,RecyclerView.VERTICAL,false)
        binding.stockIssueDetailRecyclerview.hasFixedSize()
        adapter = StockDetailAdapter(context, stockIssueAbleList as ArrayList<Stock>)
        binding.stockIssueDetailRecyclerview.adapter = adapter
        adapter.setOnItemClickListener(this)


    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = "${getString(R.string.stock_issue_text)} Details"
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
        getStockIssueAbleList()
    }

    private fun getStockIssueAbleList() {
        startLoading(context)
        viewModel.callStockIssueAbleDetails(context,user!!.USER_ID,user!!.PASSO)
        viewModel.getStockIssueAbleDetailsResponse()
            .observe(this, Observer { stockIssueAbleResponse ->
                dismiss()
                if (stockIssueAbleResponse != null) {
                    if (stockIssueAbleList.isNotEmpty()) {
                        stockIssueAbleList.clear()
                    }
                    stockIssueAbleList.addAll(stockIssueAbleResponse)
                    adapter.notifyItemRangeChanged(0, stockIssueAbleList.size)
                }
                else{
                    stockIssueAbleList.clear()
                    adapter.notifyDataSetChanged()
                }
            })
    }

    override fun onItemClick(position: Int) {
        Constants.stockIssueReqObject = null
        Constants.acceptedTraysDetailList.clear()
        Constants.tempQuantity = 0
        val stock = stockIssueAbleList[position]
        Constants.stockIssueReqObject = stock
        startActivity(
            Intent(
                context,
                GirsSelectedActivity::class.java
            ).apply {
                putExtra("ITEM", stock)
            }
        )
    }

}