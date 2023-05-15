package com.sobytek.erpsobytek.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sobytek.erpsobytek.R
import com.sobytek.erpsobytek.databinding.SampleIssuenceDetailItemRowBinding
import com.sobytek.erpsobytek.databinding.SampleReceiveDetailItemRowBinding
import com.sobytek.erpsobytek.model.Sample

class SampleReceiveDetailAdapter(val context: Context, val sampleReceiveDetailList:ArrayList<Sample>):RecyclerView.Adapter<SampleReceiveDetailAdapter.ItemViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mListener = listener
    }

    inner class ItemViewHolder(itemView: View, Listener: OnItemClickListener):RecyclerView.ViewHolder(itemView){
        val binding = SampleReceiveDetailItemRowBinding.bind(itemView)

        init {
            itemView.setOnClickListener {
                mListener!!.onItemClick(layoutPosition)
            }
        }

        fun bindData(sample: Sample,position:Int){
            binding.srdItemSampleId.text = "${sample.SAMPLE_ID}"
            binding.srdItemStoreCode.text = sample.SC_CODE
            binding.srdItemSampleVer.text = sample.SAMPLE_VER
            binding.srdItemRackId.text = sample.RACK_ID
            binding.srdItemUserId.text = sample.USER_ID

            if (position % 2 == 0) {
                itemView.setBackgroundColor(Color.parseColor("#EAEAF6"))
            } else {
                itemView.setBackgroundColor(Color.parseColor("#f2f2f2"))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sample_receive_detail_item_row,parent,false)
        return ItemViewHolder(view,mListener!!)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindData(sampleReceiveDetailList[position],position)
    }

    override fun getItemCount(): Int {
        return sampleReceiveDetailList.size
    }


}