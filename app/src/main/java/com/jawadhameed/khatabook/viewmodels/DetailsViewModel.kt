package com.jawadhameed.khatabook.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jawadhameed.khatabook.models.Detail
import com.jawadhameed.khatabook.repositories.DetailsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsViewModel(private val repository: DetailsRepository): ViewModel() {

    fun getDetails(): LiveData<List<Detail>>{
        return repository.getDetails()
    }

    fun createDetails(detail: Detail){
        viewModelScope.launch(Dispatchers.IO){
            repository.createDetails(detail)
        }
    }


    fun deleteDetails(detail: Detail){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteDetails(detail)
        }
    }


    fun updateDetails(detail: Detail){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateDetails(detail)
        }
    }
}