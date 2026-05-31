package com.example.homework4.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.homework4.data.repository.CountriesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CountrySyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: CountriesRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result =
        try {
            repository.refreshCountries()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }

    companion object {
        const val UNIQUE_WORK_NAME = "country_cache_sync"
    }
}

