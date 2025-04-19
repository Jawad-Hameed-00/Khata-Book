package com.jawadhameed.khatabook.repositories

import androidx.lifecycle.LiveData
import com.jawadhameed.khatabook.database.CustomerDao
import com.jawadhameed.khatabook.models.Customer

class KhataRepository(private val customerDao: CustomerDao) {

    fun getCustomer() : LiveData<List<Customer>>{
        return customerDao.getCustomer()
    }

    suspend fun createCustomer(customer: Customer){
        return customerDao.createCustomer(customer)
    }

    suspend fun deleteCustomer(customer: Customer){
        return customerDao.deleteCustomer(customer)
    }

    suspend fun updateCustomer(customer: Customer){
        return customerDao.updateCustomer(customer)
    }
}