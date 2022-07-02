package com.example.chat_pattern_mediator

interface User {
    val name: String
    var isActive: Boolean
    fun receive(message: String, from: User? = null)
    fun send(message: String, to: User? = null, mode: Mode = Mode.ANY): Boolean


  class Base(private val chatMediator: ChatMediator, override val name: String) : User {
        init {
            chatMediator.registerUser(this)
        }

      override var isActive: Boolean = false
      override fun receive(message: String, from: User?) {
            println(message)
        }

        override fun send(message: String, to: User?, mode: Mode): Boolean {
            return chatMediator.send(message, this, to, mode)
        }

        fun unregister(): Boolean {
            return chatMediator.unregisterUser(this)
        }
    }
}