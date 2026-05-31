package com.example.homework4.fakes

import com.example.homework4.data.model.CountryDto
import com.example.homework4.data.remote.CountriesApi

class FakeCountriesApi : CountriesApi {

    var countries: List<CountryDto> = emptyList()
    var searchMap: Map<String, List<CountryDto>> = emptyMap()
    var byCode: Map<String, List<CountryDto>> = emptyMap()
    var failAllRequests: Boolean = false

    override suspend fun getCountries(): List<CountryDto> {
        if (failAllRequests) throw RuntimeException("network")
        return countries
    }

    override suspend fun searchCountries(name: String): List<CountryDto> {
        if (failAllRequests) throw RuntimeException("network")
        return searchMap[name] ?: emptyList()
    }

    override suspend fun getCountryByCode(code: String): List<CountryDto> {
        if (failAllRequests) throw RuntimeException("network")
        return byCode[code] ?: emptyList()
    }
}
