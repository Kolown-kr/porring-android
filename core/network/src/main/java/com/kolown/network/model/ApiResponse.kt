package com.kolown.network.model

internal sealed interface ApiResponse<out T> {

    data class Success<T>(
        val data: T,
    ) : ApiResponse<T>

    sealed interface Failure<T>: ApiResponse<T> {
        open class Error(val code: Int, val payload: Any? = null) : Failure<Nothing> {

            override fun equals(other: Any?): Boolean = other is Error &&
                    payload == other.payload && code == other.code

            override fun hashCode(): Int {
                var result = payload.hashCode()
                result = 31 * result + code
                return result
            }

            override fun toString(): String = payload.toString()
        }

        open class Exception(val throwable: Throwable) : Failure<Nothing> {
            private val message: String? = throwable.message

            override fun equals(other: Any?): Boolean = other is Exception &&
                    throwable == other.throwable

            override fun hashCode(): Int = throwable.hashCode()

            override fun toString(): String = message.orEmpty()
        }
    }
}

internal typealias CompletableResponse = ApiResponse<Unit>