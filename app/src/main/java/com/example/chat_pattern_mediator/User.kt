package com.example.chat_pattern_mediator

interface User {
    val name: String
    var isActive: Boolean
    var lastMessageReceive: String
    fun receive(message: String, from: User? = null)
    fun send(message: String, to: User? = null, mode: Mode = Mode.ANY): Result<Boolean>
    fun join(): Result<Boolean>
    fun leave(): Result<Boolean>


    class Base(private val chatMediator: ChatMediator, override val name: String) : User {
        override var isActive: Boolean = false
        override var lastMessageReceive: String = ""

        override fun receive(message: String, from: User?) {
            lastMessageReceive = message
            println(message)
        }

        override fun send(message: String, to: User?, mode: Mode): Result<Boolean> {
            return chatMediator.send(message, this, to, mode)
        }

        override fun join(): Result<Boolean> {
            return chatMediator.registerUser(this)
        }

        override fun leave(): Result<Boolean> {
            return chatMediator.unregisterUser(this)
        }
    }
}