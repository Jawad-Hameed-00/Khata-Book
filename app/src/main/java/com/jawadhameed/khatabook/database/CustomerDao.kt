package com.jawadhameed.khatabook.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.jawadhameed.khatabook.models.Customer

@Dao
interface CustomerDao {

    @Query("SELECT * FROM customers")
    fun getCustomer() : LiveData<List<Customer>>

    @Insert
    suspend fun createCustomer(customers: Customer)

    @Update
    suspend fun updateCustomer(customers: Customer)

    @Delete
    suspend fun deleteCustomer(customers: Customer)

}