package com.example.chat_pattern_mediator.resource

sealed class Successes(var message: String) {
    class BaseSuccess(successMessage: String) : Successes(successMessage)
    class JoiningChatSuccess(name: String) : Successes("$name joined the chat")
    class LeaveChatSuccess(name: String) : Successes("$name left the chat")
}
