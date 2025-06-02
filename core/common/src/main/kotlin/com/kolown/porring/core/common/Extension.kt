package com.kolown.porring.core.common

suspend fun <T> retry(times: Int, block: suspend () -> T): T {
    repeat(times - 1) {
        try { return block() } catch (e: Exception) { /* ignore */ }
    }
    return block()
}