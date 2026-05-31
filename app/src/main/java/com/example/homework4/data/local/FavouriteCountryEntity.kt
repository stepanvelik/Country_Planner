package com.example.homework4.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.homework4.data.model.Country

@Entity(tableName = "favourites")
data class FavouriteCountryEntity(

    @PrimaryKey
    val code: String,

    val name: String,

    val capital: String,

    val region: String,

    val population: Long,

    val area: Double,

    val flagUrl: String
) {
    fun toCountry(): Country = Country(
        code = code,
        name = name,
        capital = capital,
        region = region,
        population = population,
        area = area,
        flagUrl = flagUrl
    )

    companion object {
        fun fromCountry(country: Country): FavouriteCountryEntity = FavouriteCountryEntity(
            code = country.code,
            name = country.name,
            capital = country.capital,
            region = country.region,
            population = country.population,
            area = country.area,
            flagUrl = country.flagUrl
        )
    }
}
