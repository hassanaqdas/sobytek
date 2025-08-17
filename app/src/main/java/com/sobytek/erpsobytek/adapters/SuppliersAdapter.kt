package com.sobytek.erpsobytek.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sobytek.erpsobytek.R
import com.sobytek.erpsobytek.databinding.SupplierItemRowBinding
import com.sobytek.erpsobytek.model.Supplier

class SuppliersAdapter(val context: Context, val suppliers:ArrayList<Supplier>):RecyclerView.Adapter<SuppliersAdapter.ItemViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var mListener: OnItemClickListener? = null
    private var filteredList: List<Supplier> = suppliers

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mListener = listener
    }

    inner class ItemViewHolder(itemView: View, Listener: OnItemClickListener):RecyclerView.ViewHolder(itemView){
        val binding = SupplierItemRowBinding.bind(itemView)

//        init {
//            itemView.setOnClickListener {
//                mListener!!.onItemClick(layoutPosition)
//            }
//        }

        fun bindData(supplier: Supplier,position:Int){
            binding.supplierItemId.text = supplier.SUPPLIER_ID
            binding.supplierItemName.text = supplier.SUPPLIER_NAME

            if (position % 2 == 0) {
                itemView.setBackgroundColor(Color.parseColor("#EAEAF6"))
            } else {
                itemView.setBackgroundColor(Color.parseColor("#f2f2f2"))
            }

            itemView.setOnClickListener {
                mListener!!.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.supplier_item_row,parent,false)
        return ItemViewHolder(view,mListener!!)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindData(filteredList[position],position)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    public fun updateList(list: List<Supplier>) {
       filteredList = list
        notifyDataSetChanged()
    }

}