package com.sobytek.erpsobytek.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sobytek.erpsobytek.R
import com.sobytek.erpsobytek.databinding.StockReceiveDetailItemRowBinding
import com.sobytek.erpsobytek.databinding.StockTraysDetailItemRowBinding
import com.sobytek.erpsobytek.model.Stock

class StockTraysDetailAdapter(val context: Context, val stockTraysDetailList:ArrayList<Stock>):RecyclerView.Adapter<StockTraysDetailAdapter.ItemViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mListener = listener
    }

    inner class ItemViewHolder(itemView: View, Listener: OnItemClickListener):RecyclerView.ViewHolder(itemView){
        val binding = StockTraysDetailItemRowBinding.bind(itemView)

        init {
            itemView.setOnClickListener {
                mListener!!.onItemClick(layoutPosition)
            }
        }

        fun bindData(stock: Stock,position:Int){
            binding.srdItemTraysId.text = stock.TRAY_ID
            binding.srdItemLocation.text = "${stock.STORE_ID}/${stock.RACK_ID}"
            binding.srdItemQuantity.text = stock.QUANTITY

            if (position % 2 == 0) {
                itemView.setBackgroundColor(Color.parseColor("#EAEAF6"))
            } else {
                itemView.setBackgroundColor(Color.parseColor("#f2f2f2"))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.stock_trays_detail_item_row,parent,false)
        return ItemViewHolder(view,mListener!!)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindData(stockTraysDetailList[position],position)
    }

    override fun getItemCount(): Int {
        return stockTraysDetailList.size
    }


}