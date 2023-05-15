package com.sobytek.erpsobytek.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sobytek.erpsobytek.R
import com.sobytek.erpsobytek.databinding.WorkerItemRowBinding
import com.sobytek.erpsobytek.model.Worker

class WorkerAdapter(val context: Context, val workerList:ArrayList<Worker>):RecyclerView.Adapter<WorkerAdapter.ItemViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mListener = listener
    }

    inner class ItemViewHolder(itemView: View, listener: OnItemClickListener):RecyclerView.ViewHolder(itemView){
        val binding = WorkerItemRowBinding.bind(itemView)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(layoutPosition)
            }
        }

        fun bindData(worker: Worker,position:Int){
            binding.workerItemIdTv.text = worker.WORKER_ID
            binding.workerItemNameView.text = worker.NAME

            if (position % 2 == 0) {
                itemView.setBackgroundColor(Color.parseColor("#EAEAF6"))
            } else {
                itemView.setBackgroundColor(Color.parseColor("#f2f2f2"))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.worker_item_row,parent,false)
        return ItemViewHolder(view,mListener!!)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindData(workerList[position],position)
    }

    override fun getItemCount(): Int {
        return workerList.size
    }


}