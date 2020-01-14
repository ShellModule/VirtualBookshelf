package com.example.projektzd.database

data class Book (
    val id: String = "none",
    val title: String = "none",
    val rentalDate: String = "",
    val returnDate: String = "",
    val remainingDays: Int = 0,
    val pageCount: Int = 0,
    val thumbnail: String = " "
)