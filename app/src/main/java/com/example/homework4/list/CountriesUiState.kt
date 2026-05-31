package com.example.homework4.list

import com.example.homework4.data.model.Country

sealed interface CountriesUiState {
    object Loading : CountriesUiState
    object Empty : CountriesUiState
    data class Error(val message: String) : CountriesUiState
    data class Success(val list: List<Country>) : CountriesUiState
}