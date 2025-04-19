package com.jawadhameed.khatabook.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(

    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val initial: String,
    val toGive: Boolean,
    val money: Int
)