package com.kolown.porring.core.data.utils

import kotlinx.coroutines.delay

internal suspend fun <T> retryWithLimit(
    maxAttempts: Int = 3,
    delayMillis: Long = 1000,
    block: suspend () -> Result<T>
): Result<T> {
    var result = block()

    if (result.isSuccess) return result

    repeat(maxAttempts - 1) {
        delay(delayMillis)
        result = block()
        if (result.isSuccess) return result
    }

    return result
}