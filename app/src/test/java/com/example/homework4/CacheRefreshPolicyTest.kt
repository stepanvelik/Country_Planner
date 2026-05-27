package com.example.homework4

import com.example.homework4.sync.CacheRefreshPolicy
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CacheRefreshPolicyTest {

    private val policy = CacheRefreshPolicy()

    @Test
    fun missingCache_requiresRefresh() {
        assertTrue(
            policy.shouldRefresh(
                oldestCacheMillis = null,
                nowMillis = 10_000L,
                ttlHours = 24
            )
        )
    }

    @Test
    fun freshCache_doesNotRefreshBeforeTtl() {
        assertFalse(
            policy.shouldRefresh(
                oldestCacheMillis = 0L,
                nowMillis = 30 * 60 * 1000L,
                ttlHours = 1
            )
        )
    }

    @Test
    fun staleCache_refreshesAfterTtl() {
        assertTrue(
            policy.shouldRefresh(
                oldestCacheMillis = 0L,
                nowMillis = 2 * 60 * 60 * 1000L,
                ttlHours = 1
            )
        )
    }
}

