package com.example.homework4.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.homework4.data.model.Country
import com.example.homework4.data.model.RecentCountry

@Entity(tableName = "recent_countries")
data class RecentCountryEntity(
    @PrimaryKey
    val code: String,
    val name: String,
    val capital: String,
    val region: String,
    val population: Long,
    val area: Double,
    val flagUrl: String,
    val viewedAtMillis: Long
) {
    fun toRecentCountry(): RecentCountry =
        RecentCountry(
            country = Country(
                code = code,
                name = name,
                capital = capital,
                region = region,
                population = population,
                area = area,
                flagUrl = flagUrl
            ),
            viewedAtMillis = viewedAtMillis
        )

    companion object {
        fun fromCountry(country: Country, viewedAtMillis: Long): RecentCountryEntity =
            RecentCountryEntity(
                code = country.code,
                name = country.name,
                capital = country.capital,
                region = country.region,
                population = country.population,
                area = country.area,
                flagUrl = country.flagUrl,
                viewedAtMillis = viewedAtMillis
            )
    }
}
