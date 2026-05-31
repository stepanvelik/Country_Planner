package com.example.homework4.sync

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerCountrySyncScheduler @Inject constructor(
    private val workManager: WorkManager
) : CountrySyncScheduler {

    override fun setBackgroundSyncEnabled(enabled: Boolean) {
        if (enabled) {
            val request = PeriodicWorkRequestBuilder<CountrySyncWorker>(
                repeatInterval = 15,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            workManager.enqueueUniquePeriodicWork(
                CountrySyncWorker.UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        } else {
            workManager.cancelUniqueWork(CountrySyncWorker.UNIQUE_WORK_NAME)
        }
    }
}

