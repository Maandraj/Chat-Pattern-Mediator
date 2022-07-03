package com.example.chat_pattern_mediator

sealed class Result<T>(
    val data: T? = null,
    val message: String? = null,
) {
    class Success<T>(data: T) : Result<T>(data = data)

    class Error<T>(error: Errors) : Result<T>(message = error.message)

}

sealed class Errors(var message: String) {
    class BaseError(error: String) : Errors(error)
    class YourIsNotInTheChat(error: String = "Your are is not in the chat") : Errors(error)
    class NoUserSelected(error: String = "No user selected") : Errors(error)
    class UserIsNotInTheChat(error: String = "The user is not in the chat") : Errors(error)
    class EmptyMessage(error: String = "Empty message send") : Errors(error)
    class UserIsAlready(error: String = "The user is already in the chat") : Errors(error)

}