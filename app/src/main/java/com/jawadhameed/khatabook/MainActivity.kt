package com.jawadhameed.khatabook

import android.app.Dialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.jawadhameed.khatabook.adapters.CustomersListAdapter
import com.jawadhameed.khatabook.database.CustomerDatabase
import com.jawadhameed.khatabook.databinding.ActivityMainBinding
import com.jawadhameed.khatabook.factories.CustomerVMFactory
import com.jawadhameed.khatabook.models.Customer
import com.jawadhameed.khatabook.repositories.KhataRepository
import com.jawadhameed.khatabook.viewmodels.CustomerViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var adapter: CustomersListAdapter
    var customerList: ArrayList<Customer>? = null
    lateinit var customerViewModel: CustomerViewModel
    var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        createDialog()

        val customerDao = CustomerDatabase.getDatabase(application).customerDao()
        val repository = KhataRepository(customerDao)
        customerViewModel = ViewModelProvider(
            this,
            CustomerVMFactory(repository)
        ).get(CustomerViewModel::class.java)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = CustomersListAdapter(this, emptyList<Customer>())
        binding.recyclerView.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(simpleCallBack)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        customerViewModel.getCustomer().observe(this, Observer {
            customerList = it as ArrayList<Customer>
            adapter.customersList = customerList!!
            adapter.notifyDataSetChanged()

            var toGiveTotal = 0
            var toReceiveTotal = 0
            for (customer in customerList!!) {
                if (customer.toGive) {
                    toGiveTotal += customer.money
                } else {
                    toReceiveTotal += customer.money
                }
            }
            binding.toGive.text = "Rs. $toGiveTotal"
            binding.toReceive.text = "Rs. $toReceiveTotal"
        })


        val addCustomerBtn = dialog?.findViewById<CardView>(R.id.addCustomerBtn)
        val customerName = dialog?.findViewById<EditText>(R.id.customerName)

        addCustomerBtn?.setOnClickListener {
            val name = customerName?.text.toString()
            if (name.isNotEmpty()) {
                val customer = Customer(0, name, name.elementAt(0).toString(), false, 0)
                customerViewModel.createCustomer(customer)
                customerName?.setText("")
                dialog?.dismiss()
            }
        }

        binding.addCustomer.setOnClickListener {
            dialog?.show()
        }
    }
    override fun deleteDatabase(name: String?): Boolean {
        return application.deleteDatabase(name)
    }

    val simpleCallBack: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val customer = adapter.getCustomer(position)

            when (direction) {
                ItemTouchHelper.LEFT -> {
                    customerViewModel.deleteCustomer(customer)
                    deleteDatabase(customer.name)
                }
            }
        }

        override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

            RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.red))
                .addSwipeLeftActionIcon(R.drawable.delete) // You can add an icon for left swipe if needed
                .create()
                .decorate()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    fun createDialog() {
        dialog = Dialog(this)
        dialog?.setContentView(R.layout.add_customer_dialog)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.create()


    }

}