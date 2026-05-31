package com.example.homework4.data.model

data class CountryDto(
    val cca2: String,
    val name: NameDto,
    val capital: List<String>?,
    val region: String,
    val population: Long,
    val area: Double,
    val flags: FlagsDto
)

data class NameDto(
    val common: String
)

data class FlagsDto(
    val png: String
)
