package com.jawadhameed.khatabook

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.jawadhameed.khatabook.adapters.DetailsListAdapter
import com.jawadhameed.khatabook.database.CustomerDatabase
import com.jawadhameed.khatabook.database.DetailsDatabase
import com.jawadhameed.khatabook.databinding.ActivityDetailsBinding
import com.jawadhameed.khatabook.factories.CustomerVMFactory
import com.jawadhameed.khatabook.factories.DetailsVMFactory
import com.jawadhameed.khatabook.models.Customer
import com.jawadhameed.khatabook.models.Detail
import com.jawadhameed.khatabook.repositories.DetailsRepository
import com.jawadhameed.khatabook.repositories.KhataRepository
import com.jawadhameed.khatabook.viewmodels.CustomerViewModel
import com.jawadhameed.khatabook.viewmodels.DetailsViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DetailsActivity : AppCompatActivity(), AdapterListener {
    lateinit var customer: Customer
    private lateinit var binding: ActivityDetailsBinding
    lateinit var detailsViewModel: DetailsViewModel
    private lateinit var customerViewModel: CustomerViewModel
    private var detailList: ArrayList<Detail>? = null
    private var dialog: Dialog? = null
    private var totalAmountToShow:Long = 0
    private var updateID = 0
    lateinit var adapter: DetailsListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id  = intent.getIntExtra("id", 0)
        val name  = intent.getStringExtra("name")
        val initial  = intent.getStringExtra("initial")
        val money  = intent.getIntExtra("money", 0)
        val toGive  = intent.getBooleanExtra("toGive", false)
        customer = Customer(id, name!!, initial!!, toGive, money)

        val detailsDao = DetailsDatabase.getDatabase(application, customer.name).detailsDao()
        val repository = DetailsRepository(detailsDao)

        val customerDao = CustomerDatabase.getDatabase(application).customerDao()
        val customerRepository = KhataRepository(customerDao)

        detailsViewModel = ViewModelProvider(this, DetailsVMFactory(repository))[DetailsViewModel::class.java]
        customerViewModel = ViewModelProvider(this, CustomerVMFactory(customerRepository))[CustomerViewModel::class.java]

        createDialog()

        adapter = DetailsListAdapter(this, emptyList(), this)
        binding.detailsRecyclerView.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(simpleCallBack)
        itemTouchHelper.attachToRecyclerView(binding.detailsRecyclerView)

        detailsViewModel.getDetails().observe(this) {
            detailList = it as ArrayList<Detail>
            adapter.detailList = detailList!!
            countTotalAmount()
            adapter.reverseList()
        }

        binding.backBtn.setOnClickListener{
            finish()
        }

        binding.iGave.setOnClickListener {
            dialog?.findViewById<TextView>(R.id.ButtonText)?.setText("Add")
            showDialog("I Gave")
        }

        binding.iGot.setOnClickListener {
            dialog?.findViewById<TextView>(R.id.ButtonText)?.setText("Add")
            showDialog("I Got")


        }

        val addAmountBtn = dialog?.findViewById<CardView>(R.id.addAmountBtn)
        val ButtonText = dialog?.findViewById<TextView>(R.id.ButtonText)
        val Amount = dialog?.findViewById<EditText>(R.id.amountToAdd)
        val detailsOptional = dialog?.findViewById<EditText>(R.id.detailsOptional)

        addAmountBtn?.setOnClickListener {
            if (Amount!!.text.isNotEmpty()){

                val amount  = Amount.text.toString().toLong()
                val details  = detailsOptional?.text.toString()

                if (amount > Int.MAX_VALUE){
                    Toast.makeText(this, "Too Large Value", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (ButtonText?.text.toString().equals("Update")){
                    if (dialog?.findViewById<TextView>(R.id.purposeText)!!.text.equals("I Got")){
                        val detail = Detail(updateID, getDateAndTime(), amount, false,details)
                        detailsViewModel.updateDetails(detail)
                    }else{
                        val detail = Detail(updateID, getDateAndTime(), amount, true,details)
                        detailsViewModel.updateDetails(detail)
                    }
                }else{
                    if (dialog?.findViewById<TextView>(R.id.purposeText)!!.text.equals("I Got")){
                        val detail = Detail(0, getDateAndTime(), amount, false,details)
                        detailsViewModel.createDetails(detail)
                    }else{
                        val detail = Detail(0, getDateAndTime(), amount, true,details)
                        detailsViewModel.createDetails(detail)
                    }
                }



                Amount.setText("")
                detailsOptional?.setText("")
                dialog?.dismiss()
            }
        }


        binding.customerName.text = customer.name
        binding.initialName.text = customer.initial
    }

    private fun countTotalAmount(){
        totalAmountToShow = 0
        for (detail in detailList!!){
            if (detail.gave){
                totalAmountToShow += detail.money
            }else{
                totalAmountToShow -= detail.money
            }
        }
        if (totalAmountToShow< 0){
            binding.totalAmount.text = totalAmountToShow.toString().removePrefix("-")
        }else{
            binding.totalAmount.text = totalAmountToShow.toString()
        }

        changeRedOrGreen()
        updateDatabase()
    }

    private fun updateDatabase() {
        val customer = Customer(customer.id, customer.name, customer.initial, (totalAmountToShow<0), totalAmountToShow.toString().removePrefix("-").toInt())
        customerViewModel.updateCustomer(customer)
    }

    fun changeRedOrGreen(){
        if (totalAmountToShow<0){

            binding.totalAmount.setTextColor(getColor(R.color.green))
            binding.gave.setTextColor(getColor(R.color.green))
            binding.gave.text = "I have to give"
            binding.cardViewBG.setCardBackgroundColor(getColor(R.color.light_green))
            binding.imageView9.backgroundTintList = ColorStateList.valueOf(getColor(R.color.green))
            binding.imageView9.rotation = 0F
            binding.imageView9.setImageDrawable(getDrawable(R.drawable.arrow))

        }else if (totalAmountToShow>0){

            binding.totalAmount.setTextColor(getColor(R.color.red))
            binding.gave.setTextColor(getColor(R.color.red))
            binding.gave.text = "I have to got"
            binding.cardViewBG.setCardBackgroundColor(getColor(R.color.light_red))
            binding.imageView9.backgroundTintList = ColorStateList.valueOf(getColor(R.color.red))
            binding.imageView9.rotation = 180F
            binding.imageView9.setImageDrawable(getDrawable(R.drawable.arrow))

        }else{

            binding.totalAmount.setTextColor(getColor(R.color.green))
            binding.gave.setTextColor(getColor(R.color.green))
            binding.gave.text = "Clear"
            binding.cardViewBG.setCardBackgroundColor(getColor(R.color.light_green))
            binding.imageView9.backgroundTintList = ColorStateList.valueOf(getColor(R.color.green))
            binding.imageView9.setImageDrawable(getDrawable(R.drawable.minus))

        }
    }


    fun createDialog() {
        dialog = Dialog(this)
        dialog?.setContentView(R.layout.add_details_dialog)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.create()
    }

    fun showDialog(purpose: String){
        dialog?.findViewById<TextView>(R.id.purposeText)?.setText(purpose)
        dialog?.show()
    }

    private val simpleCallBack: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val detail = adapter.getDetail(position)

            when (direction) {
                ItemTouchHelper.LEFT -> {
                    detailsViewModel.deleteDetails(detail)
                }
            }
        }

        override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

            RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(this@DetailsActivity, R.color.red))
                .addSwipeLeftActionIcon(R.drawable.delete)
                .create()
                .decorate()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun onCLick(position: Int, id: Detail) {
        updateID = id.id

        val detail = adapter.getDetail(position)

        if (detail.gave){
            dialog?.findViewById<TextView>(R.id.purposeText)?.setText("I Gave")
        }else{
            dialog?.findViewById<TextView>(R.id.purposeText)?.setText("I Got")
        }
        dialog?.findViewById<TextView>(R.id.ButtonText)?.setText("Update")
        dialog?.findViewById<EditText>(R.id.amountToAdd)?.setText(detail.money.toString())
        dialog?.findViewById<EditText>(R.id.detailsOptional)?.setText(detail.details)

        dialog?.show()
    }
    private fun getDateAndTime(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd-MM hh:mm a", Locale.ENGLISH)
        val dateTimeWithAMPM: String = dateFormat.format(currentDate)

        return dateTimeWithAMPM
    }
}