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
import com.sobytek.erpsobytek.databinding.ActivityGirsSelectedBinding
import com.sobytek.erpsobytek.databinding.ActivityStockIssuenceBinding
import com.sobytek.erpsobytek.model.Stock
import com.sobytek.erpsobytek.model.User
import com.sobytek.erpsobytek.utils.AppSettings
import com.sobytek.erpsobytek.utils.WrapContentLinearLayoutManager
import com.sobytek.erpsobytek.viewmodel.StockViewModel
import com.sobytek.erpsobytek.viewmodelfactory.ViewModelFactory

class GirsSelectedActivity : BaseActivity(), StockDetailAdapter.OnItemClickListener {

    private lateinit var context: Context
    private lateinit var binding: ActivityGirsSelectedBinding
    private var user: User? = null
    private lateinit var appSettings: AppSettings
    private var stockGirsList = mutableListOf<Stock>()
    private lateinit var viewModel: StockViewModel
    private lateinit var adapter: StockDetailAdapter
    private var selectedItem: Stock? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGirsSelectedBinding.inflate(layoutInflater)
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

        binding.stockGirsDetailRecyclerview.layoutManager = WrapContentLinearLayoutManager(
            context,
            RecyclerView.VERTICAL, false
        )
        binding.stockGirsDetailRecyclerview.hasFixedSize()
        adapter = StockDetailAdapter(context, stockGirsList as ArrayList<Stock>,1)
        binding.stockGirsDetailRecyclerview.adapter = adapter
        adapter.setOnItemClickListener(this)

        if (selectedItem != null) {
            getStockGirsList(selectedItem!!.SC_ID,user!!.USER_ID,user!!.PASSO)
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = "${getString(R.string.stock_girs_text)} Details"
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

    private fun getStockGirsList(sc_id: String,user_id: String,passo: String) {
        startLoading(context)
        viewModel.callStockGirsDetails(context, sc_id,user_id,passo)
        viewModel.getStockGirsDetailsResponse()
            .observe(this, Observer { stockGirsResponse ->
                dismiss()
                if (stockGirsResponse != null) {
                    if (stockGirsList.isNotEmpty()) {
                        stockGirsList.clear()
                    }
                    stockGirsList.addAll(stockGirsResponse)
                    adapter.notifyItemRangeChanged(0, stockGirsList.size)
                }
            })
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onItemClick(position: Int) {
        val item = stockGirsList[position]
        val intent = Intent(
            context,
            StockTraysActivity::class.java
        ).apply {
            putExtra("ITEM", item)
        }
        resultLauncher.launch(intent)
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data!!.hasExtra("FLAG") && data.getStringExtra("FLAG") == "2") {
                    setResult(RESULT_OK, Intent().apply { putExtra("FLAG", "1") })
                    finish()
                }

            }
        }
}