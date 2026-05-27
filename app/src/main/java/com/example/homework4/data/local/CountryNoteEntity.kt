package com.example.homework4.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.homework4.data.model.Country
import com.example.homework4.data.model.CountryNote
import com.example.homework4.data.model.TravelStatus

@Entity(tableName = "country_notes")
data class CountryNoteEntity(
    @PrimaryKey
    val countryCode: String,
    val countryName: String,
    val region: String,
    val population: Long,
    val area: Double,
    val flagUrl: String,
    val status: TravelStatus,
    val note: String,
    val updatedAtMillis: Long
) {
    fun toCountryNote(): CountryNote =
        CountryNote(
            countryCode = countryCode,
            countryName = countryName,
            region = region,
            population = population,
            area = area,
            flagUrl = flagUrl,
            status = status,
            note = note,
            updatedAtMillis = updatedAtMillis
        )

    companion object {
        fun fromCountry(
            country: Country,
            status: TravelStatus,
            note: String,
            updatedAtMillis: Long
        ): CountryNoteEntity =
            CountryNoteEntity(
                countryCode = country.code,
                countryName = country.name,
                region = country.region,
                population = country.population,
                area = country.area,
                flagUrl = country.flagUrl,
                status = status,
                note = note,
                updatedAtMillis = updatedAtMillis
            )
    }
}
