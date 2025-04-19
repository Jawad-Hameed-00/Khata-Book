package com.jawadhameed.khatabook

import com.jawadhameed.khatabook.models.Detail

interface AdapterListener {
    fun onCLick(position: Int, id: Detail)
}