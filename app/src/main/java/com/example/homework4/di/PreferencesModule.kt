package com.example.homework4.di

import com.example.homework4.data.preferences.DefaultUserPreferencesRepository
import com.example.homework4.data.preferences.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesModule {

    @Binds
    abstract fun bindUserPreferencesRepository(
        impl: DefaultUserPreferencesRepository
    ): UserPreferencesRepository
}
