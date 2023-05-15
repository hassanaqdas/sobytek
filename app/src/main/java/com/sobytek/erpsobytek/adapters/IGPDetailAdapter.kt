package com.sobytek.erpsobytek.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sobytek.erpsobytek.R
import com.sobytek.erpsobytek.databinding.IgpDetailItemRowBinding
import com.sobytek.erpsobytek.databinding.LotDetailItemRowBinding
import com.sobytek.erpsobytek.model.IgpDetail
import com.sobytek.erpsobytek.model.LotDetail

class IGPDetailAdapter(val context: Context, val detailList:ArrayList<IgpDetail>):RecyclerView.Adapter<IGPDetailAdapter.ItemViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mListener = listener
    }

    inner class ItemViewHolder(itemView: View, Listener: OnItemClickListener):RecyclerView.ViewHolder(itemView){
        val binding = IgpDetailItemRowBinding.bind(itemView)

        init {
            itemView.setOnClickListener {
                mListener!!.onItemClick(layoutPosition)
            }
        }

        fun bindData(detail: IgpDetail,position:Int){
            binding.igpItemOpNo.text = detail.OPERATION_NO
            binding.igpItemOperation.text = detail.OPERATION_ID
            binding.igpItemIssue.text = detail.ISSUE_QTY
            binding.igpItemReceive.text = detail.REC_QTY
            binding.igpItemWorker.text = detail.NAME

            if (position % 2 == 0) {
                itemView.setBackgroundColor(Color.parseColor("#EAEAF6"))
            } else {
                itemView.setBackgroundColor(Color.parseColor("#f2f2f2"))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.igp_detail_item_row,parent,false)
        return ItemViewHolder(view,mListener!!)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindData(detailList[position],position)
    }

    override fun getItemCount(): Int {
        return detailList.size
    }


}