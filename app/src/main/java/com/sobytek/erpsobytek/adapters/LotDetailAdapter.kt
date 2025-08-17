package com.sobytek.erpsobytek.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sobytek.erpsobytek.R
import com.sobytek.erpsobytek.databinding.LotDetailItemRowBinding
import com.sobytek.erpsobytek.model.LotDetail

class LotDetailAdapter(val context: Context,val detailList:ArrayList<LotDetail>):RecyclerView.Adapter<LotDetailAdapter.ItemViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemLongPress(position: Int,view:View)
    }

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mListener = listener
    }

    inner class ItemViewHolder(itemView: View, Listener: OnItemClickListener):RecyclerView.ViewHolder(itemView){
        val binding = LotDetailItemRowBinding.bind(itemView)

        init {
            itemView.setOnClickListener {
                mListener!!.onItemClick(layoutPosition)
            }

            itemView.setOnLongClickListener {
                mListener!!.onItemLongPress(layoutPosition,itemView)
                return@setOnLongClickListener true
            }
        }

        fun bindData(detail: LotDetail,position:Int){
            binding.ltItemOpNo.text = detail.OPERATION_NO
            binding.ltItemOperation.text = detail.OPERATION_ID
            binding.ltItemIssue.text = detail.ISSUE_QTY
            binding.ltItemReceive.text = detail.REC_QTY
            binding.ltItemWorker.text = detail.NAME

            if (position % 2 == 0) {
                itemView.setBackgroundColor(Color.parseColor("#EAEAF6"))
            } else {
                itemView.setBackgroundColor(Color.parseColor("#f2f2f2"))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lot_detail_item_row,parent,false)
        return ItemViewHolder(view,mListener!!)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindData(detailList[position],position)
    }

    override fun getItemCount(): Int {
        return detailList.size
    }


}