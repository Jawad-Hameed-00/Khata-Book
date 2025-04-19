package com.jawadhameed.khatabook.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jawadhameed.khatabook.repositories.DetailsRepository
import com.jawadhameed.khatabook.viewmodels.DetailsViewModel

class DetailsVMFactory(val repository: DetailsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetailsViewModel(repository) as T
    }
}