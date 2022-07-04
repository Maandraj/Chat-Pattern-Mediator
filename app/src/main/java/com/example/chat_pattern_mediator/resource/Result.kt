package com.example.chat_pattern_mediator.resource

sealed class Result<T>(
    val data: T? = null,
    val message: String? = null,
) {
    class Success<T>(data: T) : Result<T>(data = data)

    class Error<T>(error: Errors) : Result<T>(message = error.message)
}


