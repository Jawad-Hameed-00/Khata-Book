package com.jawadhameed.khatabook.repositories

import androidx.lifecycle.LiveData

import com.jawadhameed.khatabook.database.DetailsDao
import com.jawadhameed.khatabook.models.Detail

class DetailsRepository(private val detailsDao: DetailsDao) {


    fun getDetails(): LiveData<List<Detail>>{
        return detailsDao.getDetails()
    }

    suspend fun createDetails(detail: Detail){
        return detailsDao.createDetails(detail)
    }

    suspend fun deleteDetails(detail: Detail){
        return detailsDao.deleteDetails(detail)
    }

    suspend fun updateDetails(detail: Detail){
        return detailsDao.updateDetails(detail)
    }
}