package com.sobytek.erpsobytek.view.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.sobytek.erpsobytek.R
import com.sobytek.erpsobytek.databinding.ActivitySampleMenuBinding
import com.sobytek.erpsobytek.databinding.ActivityStockDashboardBinding
import com.sobytek.erpsobytek.utils.Constants

class StockDashboardActivity : BaseActivity() {

    private lateinit var context: Context
    private lateinit var binding: ActivityStockDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setUpToolbar()
    }

    private fun initViews() {
        context = this


        binding.stockIssueButton.setOnClickListener {
            startActivity(Intent(context,StockIssuenceActivity::class.java))
        }

        binding.stockReceiveButton.setOnClickListener {
            startActivity(Intent(context,StockReceiveActivity::class.java))
        }

        binding.changeLocationButton.setOnClickListener {
            startActivity(Intent(context,ChangeLocationActivity::class.java))
        }

        if (Constants.buttonAccessList.isNotEmpty()){
            val list = Constants.buttonAccessList

            val found = list.any { it.first.lowercase() == "stock_issue" && it.second.lowercase() == "no"}
            if(found){
                binding.stockIssueButton.isEnabled = false
                binding.stockIssueButton.alpha = 0.5f
            }
            else{
                binding.stockIssueButton.isEnabled = true
                binding.stockIssueButton.alpha = 1.0f
            }

            val found1 = list.any { it.first.lowercase() == "stock_receive" && it.second.lowercase() == "no"}
            if(found1){
                binding.stockReceiveButton.isEnabled = false
                binding.stockReceiveButton.alpha = 0.5f
            }
            else{
                binding.stockReceiveButton.isEnabled = true
                binding.stockReceiveButton.alpha = 1.0f
            }

            val found2 = list.any { it.first.lowercase() == "stock_change_location" && it.second.lowercase() == "no"}
            if(found2){
                binding.changeLocationButton.isEnabled = false
                binding.changeLocationButton.alpha = 0.5f
            }
            else{
                binding.changeLocationButton.isEnabled = true
                binding.changeLocationButton.alpha = 1.0f
            }
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.stock_module_text)
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
}