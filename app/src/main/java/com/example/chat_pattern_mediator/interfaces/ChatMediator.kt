package com.example.chat_pattern_mediator.interfaces

import com.example.chat_pattern_mediator.resource.Mode
import com.example.chat_pattern_mediator.resource.Errors
import com.example.chat_pattern_mediator.resource.Result
import com.example.chat_pattern_mediator.resource.Successes
import java.lang.Exception

interface ChatMediator {
    fun send(message: String, from: User, to: User? = null, mode: Mode): Result<Boolean>
    fun registerUser(user: User): Result<Boolean>
    fun unregisterUser(user: User): Result<Boolean>

    class Base : ChatMediator {
        private val users = mutableListOf<User>()
        override fun send(message: String, from: User, to: User?, mode: Mode): Result<Boolean> {
            try {
                if (!from.isActive) {
                    val error = Errors.YourIsNotInTheChat()
                    return Result.Error(error)
                }
                if (message.isEmpty())
                    return Result.Error(Errors.EmptyMessage())
                if (to?.isActive == false && mode != Mode.ANY) {
                    val error = Errors.UserIsNotInTheChat()
                    return Result.Error(error)
                }
                when (mode) {
                    Mode.PRIVATE -> {
                        if (to == null) return Result.Error(Errors.NoUserSelected())
                        to.receive("${from.name} - ${to.name}: $message")
                    }
                    Mode.PUBLIC -> {
                        users.forEach { user -> user.receive("${from.name}: $message") }
                    }
                    Mode.ANY -> {
                        if (to != null && to.isActive) users.forEach { user -> user.receive("${from.name} - ${to.name}: $message") }
                        else users.forEach { user -> user.receive("${from.name}: $message") }
                    }
                }
                return Result.Success(true)
            } catch (ex: Exception) {
                return Result.Error(Errors.BaseError(ex.message ?: "Exception error"))
            }
        }

        override fun registerUser(user: User): Result<Boolean> {
            if (user.isActive) return Result.Error(Errors.UserIsAlready())
            user.isActive = true
            users.add(user)
            users.forEach { it.receive(Successes.JoiningChatSuccess(user.name).message) }
            return Result.Success(true)
        }

        override fun unregisterUser(user: User): Result<Boolean> {
            if (!user.isActive) return Result.Error(Errors.YourIsNotInTheChat())
            user.isActive = false
            users.forEach { it.receive(Successes.LeaveChatSuccess(user.name).message) }
            users.remove(user)
            return Result.Success(true)
        }


        fun removeUser(user: User) {
            user.isActive = false
            users.remove(user)
        }

        fun removeAllUsers() {
            users.forEach { user ->
                user.isActive = false
            }
            users.clear()
        }

        fun getUsers() = users
    }
}
