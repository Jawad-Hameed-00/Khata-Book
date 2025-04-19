package com.jawadhameed.khatabook.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jawadhameed.khatabook.repositories.KhataRepository
import com.jawadhameed.khatabook.viewmodels.CustomerViewModel

class CustomerVMFactory(private val repository: KhataRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CustomerViewModel(repository) as T
    }
}