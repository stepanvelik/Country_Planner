package com.example.homework4

import com.example.homework4.data.model.CountryDto
import com.example.homework4.data.model.FlagsDto
import com.example.homework4.data.model.NameDto
import com.example.homework4.data.model.toDomain
import org.junit.Assert.assertEquals
import org.junit.Test

class CountryDtoMappingTest {

    @Test
    fun toDomain_mapsDtoToCountryFields() {
        val dto = CountryDto(
            cca2 = "US",
            name = NameDto("United States"),
            capital = listOf("Washington"),
            region = "Americas",
            population = 3L,
            area = 9.5,
            flags = FlagsDto("https://flag.png")
        )

        val country = dto.toDomain()

        assertEquals("US", country.code)
        assertEquals("United States", country.name)
        assertEquals("Washington", country.capital)
        assertEquals("Americas", country.region)
        assertEquals(3L, country.population)
        assertEquals(9.5, country.area, 0.0)
        assertEquals("https://flag.png", country.flagUrl)
    }

    @Test
    fun toDomain_emptyCapital_becomesDashPlaceholder() {
        val dto = CountryDto(
            cca2 = "XX",
            name = NameDto("NoCapital"),
            capital = emptyList(),
            region = "R",
            population = 0L,
            area = 0.0,
            flags = FlagsDto("https://x.png")
        )

        assertEquals("—", dto.toDomain().capital)
    }
}
