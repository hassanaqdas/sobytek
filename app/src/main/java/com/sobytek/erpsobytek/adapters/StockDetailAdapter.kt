package com.sobytek.erpsobytek.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sobytek.erpsobytek.R
import com.sobytek.erpsobytek.databinding.StockReceiveDetailItemRowBinding
import com.sobytek.erpsobytek.model.Stock

class StockDetailAdapter(val context: Context, val stockReceiveDetailList:ArrayList<Stock>,var type:Int=0):RecyclerView.Adapter<StockDetailAdapter.ItemViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mListener = listener
    }

    inner class ItemViewHolder(itemView: View, Listener: OnItemClickListener):RecyclerView.ViewHolder(itemView){
        val binding = StockReceiveDetailItemRowBinding.bind(itemView)

        init {
            itemView.setOnClickListener {
                mListener!!.onItemClick(layoutPosition)
            }
        }

        fun bindData(stock: Stock,position:Int){
            binding.srdItemDocNo.text = stock.DOC_NO
            binding.srdItemStoreCode.text = stock.STORE_CODE
            binding.srdItemDateTime.text = stock.DOC_DATE
            binding.srdItemQuantity.text = stock.QUANTITY

            if (position % 2 == 0) {
                itemView.setBackgroundColor(Color.parseColor("#EAEAF6"))
            } else {
                itemView.setBackgroundColor(Color.parseColor("#f2f2f2"))
            }

            if (type ==1 ){
                itemView.isEnabled = position == 0
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.stock_receive_detail_item_row,parent,false)
        return ItemViewHolder(view,mListener!!)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindData(stockReceiveDetailList[position],position)
    }

    override fun getItemCount(): Int {
        return stockReceiveDetailList.size
    }


}