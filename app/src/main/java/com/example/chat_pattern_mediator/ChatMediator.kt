package com.example.chat_pattern_mediator

interface ChatMediator {
    fun send(message: String, from: User, to: User? = null, mode: Mode): Boolean
    fun registerUser(user: User): Boolean
    fun unregisterUser(user: User): Boolean

    class Base : ChatMediator {
         val users = mutableListOf<User>()
        var registerListener: ((user: User) -> Unit)? = null
        var warningListener: ((message: String) -> Unit)? = null

        override fun send(message: String, from: User, to: User?, mode: Mode): Boolean {
            if (message.isEmpty())
                return false
            if (!from.isActive){
                warningListener?.invoke("Please join to chat")
                return false
            }
            when (mode) {
                Mode.PRIVATE -> {
                    to?.receive("${from.name} - ${to.name}: $message")
                }
                Mode.PUBLIC -> {
                    users.forEach { user -> user.receive("${from.name}: $message") }
                }
                Mode.ANY -> {
                    if (to != null) users.forEach { user -> user.receive("${from.name} - ${to.name}: $message")}
                    else users.forEach { user -> user.receive("${from.name}: $message") }

                }
            }
            return true
        }

        override fun registerUser(user: User): Boolean {
            if (user.isActive) return false
            user.isActive = true
            users.add(user)
            registerListener?.invoke(user)
            return true

        }

        override fun unregisterUser(user: User): Boolean {
            if (!user.isActive) return false
            user.isActive = false
            users.remove(user)
            registerListener?.invoke(user)
            return true
        }

    }
}
