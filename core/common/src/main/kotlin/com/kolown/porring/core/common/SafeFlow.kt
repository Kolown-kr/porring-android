package com.kolown.porring.core.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

inline fun <T> safeFlow(
    crossinline onError: (Throwable) -> T = { throw it },
    crossinline block: suspend FlowCollector<T>.() -> Unit,
): Flow<T> =
    flow {
        try {
            block()
        } catch (e: Exception) {
            emit(onError(e))
        }
    }