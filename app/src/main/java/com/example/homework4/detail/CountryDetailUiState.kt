package com.example.homework4.detail

import com.example.homework4.data.model.Country

sealed interface CountryDetailUiState {
    object Loading : CountryDetailUiState
    object Empty : CountryDetailUiState
    data class Error(val message: String) : CountryDetailUiState
    data class Success(val country: Country) : CountryDetailUiState
}
