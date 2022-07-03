package com.example.chat_pattern_mediator

interface User {
    val name: String
    var isActive: Boolean
    val messages : MutableList<String>
    fun receive(message: String, from: User? = null)
    fun send(message: String, to: User? = null, mode: Mode = Mode.ANY): Result<Boolean>
    fun join(): Result<Boolean>
    fun leave(): Result<Boolean>

    fun getLastMessage() : String{
        return messages.last()
    }

    class Base(private val chatMediator: ChatMediator, override val name: String) : User {
        override var isActive: Boolean = false
        override val messages: MutableList<String> = mutableListOf()

        override fun receive(message: String, from: User?) {
            messages.add(message)
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