package com.jawadhameed.khatabook.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.jawadhameed.khatabook.AdapterListener
import com.jawadhameed.khatabook.R
import com.jawadhameed.khatabook.models.Detail

class DetailsListAdapter(val context: Context, var detailList: List<Detail>, val adapterListener: AdapterListener) : RecyclerView.Adapter<DetailsListAdapter.DetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsListAdapter.DetailsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.details_list_item, parent, false)
        return DetailsViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailsListAdapter.DetailsViewHolder, position: Int) {
        val detail = detailList.get(position)
        holder.dateAndTime.text = detail.dateTime
        holder.details.text = detail.details
        if (detail.gave){
            holder.iGaveAmount.text = "Rs. ${detail.money}"
            holder.iReceiveAmount.text = ""  // Reset "I Receive" amount
        }else{
            holder.iGaveAmount.text = ""  // Reset "I Gave" amount
            holder.iReceiveAmount.text = "Rs. ${detail.money}"
        }

        holder.detailItem.setOnClickListener {
            adapterListener.onCLick(position, detail)
        }
    }

    override fun getItemCount(): Int {
        return detailList.size
    }

    inner class DetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val dateAndTime: TextView
        val details: TextView
        val iGaveAmount: TextView
        val iReceiveAmount: TextView
        val detailItem: ConstraintLayout
        init {
            dateAndTime = itemView.findViewById(R.id.dateAndTime)
            details = itemView.findViewById(R.id.details)
            iGaveAmount = itemView.findViewById(R.id.iGaveAmount)
            iReceiveAmount = itemView.findViewById(R.id.iReceiveAmount)
            detailItem = itemView.findViewById(R.id.detailsItem)
        }
    }

    fun getDetail(position: Int): Detail {
        return detailList[position]
    }
    fun reverseList() {
        detailList = detailList.reversed()
        notifyDataSetChanged() // Notify adapter about the data change
    }
}