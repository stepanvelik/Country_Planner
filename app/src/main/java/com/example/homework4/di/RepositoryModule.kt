package com.example.homework4.di

import com.example.homework4.data.repository.CountriesRepository
import com.example.homework4.data.repository.DefaultCountriesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindCountriesRepository(impl: DefaultCountriesRepository): CountriesRepository
}
