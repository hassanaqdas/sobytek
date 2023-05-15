package com.sobytek.erpsobytek.view.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.sobytek.erpsobytek.R
import com.sobytek.erpsobytek.databinding.ActivitySampleMenuBinding
import com.sobytek.erpsobytek.utils.Constants

class SampleMenuActivity : BaseActivity() {

    private lateinit var context: Context
    private lateinit var binding:ActivitySampleMenuBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySampleMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setUpToolbar()

    }

    private fun initViews() {
        context = this

        binding.sampleAssignmentButton.setOnClickListener {
            startActivity(Intent(context,LocationDetailActivity::class.java))
        }

        binding.sampleIssueButton.setOnClickListener {
            startActivity(Intent(context,SampleIssuenceActivity::class.java))
        }

        binding.sampleReceiveButton.setOnClickListener {
            startActivity(Intent(context,SampleReceiveActivity::class.java))
        }

        if (Constants.buttonAccessList.isNotEmpty()){
            val list = Constants.buttonAccessList

            val found = list.any { it.first.lowercase() == "sample_assignment" && it.second.lowercase() == "no"}
            if(found){
                binding.sampleAssignmentButton.isEnabled = false
                binding.sampleAssignmentButton.alpha = 0.5f
            }
            else{
                binding.sampleAssignmentButton.isEnabled = true
                binding.sampleAssignmentButton.alpha = 1.0f
            }

            val found1 = list.any { it.first.lowercase() == "sample_issue" && it.second.lowercase() == "no"}
            if(found1){
                binding.sampleIssueButton.isEnabled = false
                binding.sampleIssueButton.alpha = 0.5f
            }
            else{
                binding.sampleIssueButton.isEnabled = true
                binding.sampleIssueButton.alpha = 1.0f
            }

            val found2 = list.any { it.first.lowercase() == "sample_receive" && it.second.lowercase() == "no"}
            if(found2){
                binding.sampleReceiveButton.isEnabled = false
                binding.sampleReceiveButton.alpha = 0.5f
            }
            else{
                binding.sampleReceiveButton.isEnabled = true
                binding.sampleReceiveButton.alpha = 1.0f
            }
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.sample_module_text)
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