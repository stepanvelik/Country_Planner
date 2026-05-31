package com.example.homework4.sync

import javax.inject.Inject

class CacheRefreshPolicy @Inject constructor() {

    fun shouldRefresh(
        oldestCacheMillis: Long?,
        nowMillis: Long,
        ttlHours: Int
    ): Boolean {
        if (oldestCacheMillis == null) return true
        val ttlMillis = ttlHours.coerceAtLeast(1) * 60L * 60L * 1000L
        return nowMillis - oldestCacheMillis >= ttlMillis
    }
}

