package com.sobytek.erpsobytek.view.activities

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sobytek.erpsobytek.R
import com.sobytek.erpsobytek.adapters.LotDetailAdapter
import com.sobytek.erpsobytek.adapters.SuppliersAdapter
import com.sobytek.erpsobytek.databinding.ActivitySuppliersBinding
import com.sobytek.erpsobytek.model.LotDetail
import com.sobytek.erpsobytek.model.Supplier
import com.sobytek.erpsobytek.model.User
import com.sobytek.erpsobytek.utils.AppSettings
import com.sobytek.erpsobytek.viewmodel.LotDetailActivityViewModel
import com.sobytek.erpsobytek.viewmodel.SupplierViewModel
import com.sobytek.erpsobytek.viewmodelfactory.ViewModelFactory

class SuppliersActivity : BaseActivity() {

    private lateinit var context: Context
    private lateinit var binding:ActivitySuppliersBinding
    private var user: User? = null
    private lateinit var appSettings: AppSettings
    private lateinit var viewModel: SupplierViewModel
    private lateinit var adapter:SuppliersAdapter
    private var supplierList = mutableListOf<Supplier>()
    private var lotId:Int = 0
    private var opNo:Int = 0
    private var filteredList: List<Supplier> = supplierList
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuppliersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setUpToolbar()
        getSuppliers()
    }

    private fun initViews(){
        context = this
        appSettings = AppSettings(context)
        user = appSettings.getUser("USER")
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(SupplierViewModel()).createFor()
        )[SupplierViewModel::class.java]

        if(intent != null && intent.hasExtra("LOT_ID")){
            lotId = intent.getIntExtra("LOT_ID",0)
        }

        if(intent != null && intent.hasExtra("OP_NO")){
            opNo = intent.getIntExtra("OP_NO",0)
        }

        binding.suppliersRecyclerview.layoutManager = LinearLayoutManager(context)
        binding.suppliersRecyclerview.hasFixedSize()
        adapter = SuppliersAdapter(context, filteredList as ArrayList<Supplier>)
        binding.suppliersRecyclerview.adapter = adapter

        binding.searchBox.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    fun filter(query: String) {
       filteredList = supplierList.filter { supplier ->
            supplier.SUPPLIER_ID.toLowerCase().contains(query.toLowerCase()) || supplier.SUPPLIER_NAME.toLowerCase().contains(query.toLowerCase())
        }
        adapter.updateList(filteredList)
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title =
            getString(R.string.suppliers)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(context, R.color.black))
    }

    private fun getSuppliers(){
        startLoading(context)
        viewModel.callSuppliers(context,user!!.USER_ID,user!!.PASSO)
        viewModel.getSuppliersResponse().observe(this) { response ->
            dismiss()
            if (response != null) {
                if (supplierList.isNotEmpty()) {
                    supplierList.clear()
                }
                supplierList.addAll(response)
                filteredList = supplierList
                adapter.notifyItemRangeChanged(0, filteredList.size)
                adapter.setOnItemClickListener(object :SuppliersAdapter.OnItemClickListener{
                    override fun onItemClick(position: Int) {
                        val supplier = filteredList[position]
//                        Toast.makeText(context, supplier.SUPPLIER_NAME,Toast.LENGTH_SHORT).show()
                        supplierLotIssue(supplier)
                    }
                })
            } else {
                showSnakeBar(
                    "Something wrong with server, try again!",
                    binding.parentMainLayout,
                    ContextCompat.getColor(context, R.color.red)
                )
            }
        }
    }

    private fun supplierLotIssue(supplier: Supplier) {
        startLoading(context)
        viewModel.callSupplierLotIssue(context, supplier.SUPPLIER_ID.toInt(),user!!.USER_ID,user!!.PASSO,lotId,opNo)
        viewModel.getSupplierLotIssueResponse().observe(this){response->
            if (response != null) {
                dismiss()

                if (response.get("status").asString == "200") {
                    showAlert(context, response.get("message").asString,object : DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            finish()
                        }
                    })
                } else {
                    showAlert(context, response.get("message").asString)
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