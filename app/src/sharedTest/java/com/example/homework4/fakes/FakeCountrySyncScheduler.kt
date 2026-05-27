package com.example.homework4.fakes

import com.example.homework4.sync.CountrySyncScheduler

class FakeCountrySyncScheduler : CountrySyncScheduler {
    val enabledValues = mutableListOf<Boolean>()

    override fun setBackgroundSyncEnabled(enabled: Boolean) {
        enabledValues.add(enabled)
    }
}

