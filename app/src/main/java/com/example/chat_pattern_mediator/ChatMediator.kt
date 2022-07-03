package com.example.chat_pattern_mediator

import java.lang.Exception

interface ChatMediator {
    fun send(message: String, from: User, to: User? = null, mode: Mode): Result<Boolean>
    fun registerUser(user: User): Result<Boolean>
    fun unregisterUser(user: User): Result<Boolean>

    class Base : ChatMediator {
        private val users = mutableListOf<User>()
        var registerListener: ((user: User) -> Unit)? = null
        var warningListener: ((message: String) -> Unit)? = null

        override fun send(message: String, from: User, to: User?, mode: Mode): Result<Boolean> {
            try {
                if (message.isEmpty())
                    return Result.Error(Errors.EmptyMessage())
                if (!from.isActive) {
                    val error = Errors.YourIsNotInTheChat()
                    warningListener?.invoke(error.message)
                    return Result.Error(error)
                }
                if (to?.isActive == false && mode != Mode.ANY) {
                    val error = Errors.UserIsNotInTheChat()
                    warningListener?.invoke(error.message)
                    return Result.Error(Errors.UserIsNotInTheChat())
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
            registerListener?.invoke(user)
            return Result.Success(true)
        }

        override fun unregisterUser(user: User): Result<Boolean> {
            if (!user.isActive) return Result.Error(Errors.YourIsNotInTheChat())
            user.isActive = false
            users.remove(user)
            registerListener?.invoke(user)
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
