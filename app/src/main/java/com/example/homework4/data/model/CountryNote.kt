package com.example.homework4.data.model

data class CountryNote(
    val countryCode: String,
    val countryName: String,
    val region: String,
    val population: Long,
    val area: Double,
    val flagUrl: String,
    val status: TravelStatus,
    val note: String,
    val updatedAtMillis: Long
)
