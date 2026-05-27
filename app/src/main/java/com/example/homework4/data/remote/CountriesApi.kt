package com.example.homework4.data.remote

import com.example.homework4.data.model.CountryDto
import retrofit2.http.GET
import retrofit2.http.Path

interface CountriesApi {

    @GET("all?fields=cca2,name,capital,region,population,area,flags")
    suspend fun getCountries(): List<CountryDto>

    @GET("name/{name}?fields=cca2,name,capital,region,population,area,flags")
    suspend fun searchCountries(
        @Path("name") name: String
    ): List<CountryDto>

    @GET("alpha/{code}?fields=cca2,name,capital,region,population,area,flags")
    suspend fun getCountryByCode(
        @Path("code") code: String
    ): List<CountryDto>

}
