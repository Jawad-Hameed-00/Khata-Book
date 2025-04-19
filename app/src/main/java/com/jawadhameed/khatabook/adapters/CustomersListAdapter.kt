package com.jawadhameed.khatabook.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.jawadhameed.khatabook.DetailsActivity
import com.jawadhameed.khatabook.R
import com.jawadhameed.khatabook.models.Customer

class CustomersListAdapter(val context: Context, var customersList: List<Customer>) : RecyclerView.Adapter<CustomersListAdapter.CustomerViewHolder>() {

    var toGive = 0
    var toReceive = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomersListAdapter.CustomerViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        return CustomerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomersListAdapter.CustomerViewHolder, position: Int) {
        val customer = customersList.get(position)
        holder.name.text = customer.name
        holder.initial.text = customer.initial

        if (customer.toGive) {
            holder.money.setTextColor(context.getColor(R.color.green))
            toGive += customer.money
        } else {
            holder.money.setTextColor(context.getColor(R.color.red))
            toReceive += customer.money
        }

        holder.money.text = "Rs. ${customer.money}"

        holder.itemClick.setOnClickListener {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra("name", customer.name)
            intent.putExtra("id", customer.id)
            intent.putExtra("initial", customer.initial)
            intent.putExtra("money", customer.money)
            intent.putExtra("toGive", customer.toGive)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return customersList.size
    }

    inner class CustomerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var name:TextView
        var money: TextView
        var initial: TextView
        var itemClick: ConstraintLayout
        init {
            name = itemView.findViewById(R.id.name)
            money = itemView.findViewById(R.id.money)
            initial = itemView.findViewById(R.id.initial)
            itemClick = itemView.findViewById(R.id.itemClick)
        }
    }


    fun getCustomer(position: Int): Customer{
        return customersList[position]
    }
}