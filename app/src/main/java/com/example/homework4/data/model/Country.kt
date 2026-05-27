package com.example.homework4.data.model

data class Country(
    val code: String,
    val name: String,
    val capital: String,
    val region: String,
    val population: Long,
    val area: Double,
    val flagUrl: String
)

fun CountryDto.toDomain(): Country =
    Country(
        code = cca2,
        name = name.common,
        capital = capital?.firstOrNull() ?: "—",
        region = region,
        population = population,
        area = area,
        flagUrl = flags.png
    )
