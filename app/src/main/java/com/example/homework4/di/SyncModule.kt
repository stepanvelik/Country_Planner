package com.example.homework4.di

import com.example.homework4.sync.CountrySyncScheduler
import com.example.homework4.sync.WorkManagerCountrySyncScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {

    @Binds
    abstract fun bindCountrySyncScheduler(
        impl: WorkManagerCountrySyncScheduler
    ): CountrySyncScheduler
}

