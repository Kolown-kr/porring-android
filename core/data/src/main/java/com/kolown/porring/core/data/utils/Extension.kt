package com.kolown.porring.core.data.utils

import kotlinx.coroutines.delay

internal suspend fun <T> retryWithLimit(
    maxAttempts: Int = 3,
    delayMillis: Long = 1000,
    block: suspend () -> T
): Result<T> {
    repeat(maxAttempts - 1) { attempt ->
        try {
            return Result.success(block())
        } catch (e: Exception) {
            if (attempt < maxAttempts - 1) {
                delay(delayMillis)
            }
        }
    }
    return runCatching { block() }
}