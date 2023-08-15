package com.sobytek.erpsobytek.view.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sobytek.erpsobytek.R
import com.sobytek.erpsobytek.databinding.ActivityDashboardBinding
import com.sobytek.erpsobytek.model.User
import com.sobytek.erpsobytek.utils.AppSettings
import com.sobytek.erpsobytek.utils.Constants

class DashboardActivity : BaseActivity() {

    private lateinit var context: Context
    private lateinit var binding:ActivityDashboardBinding
    private var user: User? = null
    private lateinit var appSettings: AppSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setUpToolbar()
    }


    private fun initViews(){
        context = this
        appSettings = AppSettings(context)
        user = appSettings.getUser("USER")

        binding.lotScanButton.setOnClickListener {
            startActivity(Intent(context,MainActivity::class.java))
        }

        binding.sampleLocationScanButton.setOnClickListener {
            startActivity(Intent(context,SampleMenuActivity::class.java))
        }

        binding.igpScanButton.setOnClickListener {
            startActivity(Intent(context,IGPActivity::class.java))
        }

        binding.stockScanButton.setOnClickListener {
            startActivity(Intent(context,StockDashboardActivity::class.java))
        }

        if (user != null){
            binding.loggedByView.text = user!!.USER_ID
        }

        binding.appVersionView.text = "v_${getCurrentVersion(context)}"

        if (Constants.buttonAccessList.isNotEmpty()){
            val list = Constants.buttonAccessList

            val found = list.any { it.first.lowercase() == "lot_module" && it.second.lowercase() == "no"}
            if(found){
                binding.lotScanButton.isEnabled = false
                binding.lotScanButton.alpha = 0.5f
            }
            else{
                binding.lotScanButton.isEnabled = true
                binding.lotScanButton.alpha = 1.0f
            }


            val found1 = list.any { it.first.lowercase() == "sample_module" && it.second.lowercase() == "no"}
            if(found1){
                binding.sampleLocationScanButton.isEnabled = false
                binding.sampleLocationScanButton.alpha = 0.5f
            }
            else{
                binding.sampleLocationScanButton.isEnabled = true
                binding.sampleLocationScanButton.alpha = 1.0f
            }

            val found2 = list.any { it.first.lowercase() == "igp_module" && it.second.lowercase() == "no"}
            if(found2){
                binding.igpScanButton.isEnabled = false
                binding.igpScanButton.alpha = 0.5f
            }
            else{
                binding.igpScanButton.isEnabled = true
                binding.igpScanButton.alpha = 1.0f
            }

            val found3 = list.any { it.first.lowercase() == "stock_module" && it.second.lowercase() == "no"}
            if(found3){
                binding.stockScanButton.isEnabled = false
                binding.stockScanButton.alpha = 0.5f
            }
            else{
                binding.stockScanButton.isEnabled = true
                binding.stockScanButton.alpha = 1.0f
            }
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = getString(R.string.app_name)
            binding.toolbar.setTitleTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.primary
                )
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                logout()
                return true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun logout() {
        val builder = MaterialAlertDialogBuilder(context)
        builder.setTitle("Logout!")
        builder.setMessage("Are you sure, you want to exit from app?")
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        builder.setPositiveButton("Logout") { dialog, which ->
            appSettings.remove("STATUS")
            appSettings.remove("USER")
            Constants.buttonAccessList.clear()
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
        val alert = builder.create()
        alert.show()
    }
}