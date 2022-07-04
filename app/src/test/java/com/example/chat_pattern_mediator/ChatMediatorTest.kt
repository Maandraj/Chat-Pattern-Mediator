package com.example.chat_pattern_mediator

import com.example.chat_pattern_mediator.interfaces.ChatMediator
import com.example.chat_pattern_mediator.interfaces.User
import com.example.chat_pattern_mediator.resource.Errors
import com.example.chat_pattern_mediator.resource.Mode
import com.example.chat_pattern_mediator.resource.Result
import com.example.chat_pattern_mediator.resource.Successes
import org.junit.Assert.assertEquals
import org.junit.Test

internal class ChatMediatorTest {
    private val chatMediator = ChatMediator.Base()
    private val userFrom = TestUser(chatMediator, "Oleg")
    private val userTo = TestUser(chatMediator, "Masha")

    @Test
    fun notJoiningChatAndSendingMessage() {
        val expected = userFrom.send(message = "Hello", mode = Mode.ANY)
        val actual = Errors.YourIsNotInTheChat().message
        assertEquals(expected.message, actual)
    }

    @Test
    fun joiningChatAndSendingMessage() {
        userFrom.join()
        val expected = userFrom.send(message = "Hello", mode = Mode.ANY)
        val actual = true
        assertEquals(expected.data, actual)
    }

    @Test
    fun joiningChatAndSendPrivateNotSelectMessage() {
        userFrom.join()
        val expected = userFrom.send(message = "Hello", mode = Mode.PRIVATE)
        val actual = Errors.NoUserSelected().message
        assertEquals(expected.message, actual)
    }

    @Test
    fun joiningChatAndSendPrivateSelectMessage() {
        userFrom.join()
        userTo.join()
        val expected = "${userFrom.name} - ${userTo.name}: Hello"
        userFrom.send(message = "Hello", to = userTo, mode = Mode.PRIVATE)
        val actual = userTo.getLastMessage()
        assertEquals(expected, actual)
    }

    @Test
    fun joiningChatAndSendPublicMessage() {
        userFrom.join()
        val expected = userFrom.send(message = "Hello", mode = Mode.PUBLIC)
        val actual = true
        assertEquals(expected.data, actual)
    }

    @Test
    fun joiningChatAndSendMessagePrivateTheUserWhoIsNot() {
        userFrom.join()
        val expected = userFrom.send(message = "Hello", to = userTo, mode = Mode.PRIVATE)
        val actual = Errors.UserIsNotInTheChat().message
        assertEquals(expected.message, actual)
    }

    @Test
    fun joiningChatAndSendMessageAnyTheUserWhoIsNot() {
        userFrom.join()
        val expected = userFrom.send(message = "Hello", to = userTo, mode = Mode.ANY)
        val actual = true
        assertEquals(expected.data, actual)
    }

    @Test
    fun joiningChatAndLeaveDoneGetNewMessage() {
        userFrom.join()
        userTo.join()
        userFrom.leave()
        userTo.send(message = "Hello", to = userFrom, mode = Mode.PRIVATE)
        val expected = Successes.LeaveChatSuccess(userFrom.name).message
        val actual = userFrom.getLastMessage()
        assertEquals(expected, actual)
    }

    @Test
    fun getPublicMessagesChatZeroUsers() {
        userFrom.join()
        userTo.send(message = "Hello", mode = Mode.PUBLIC)
        val expected = userFrom.getLastMessage()
        val actual = Successes.JoiningChatSuccess(userFrom.name).message
        assertEquals(expected, actual)
    }

}

class TestUser(
    private val chatMediator: ChatMediator, override val name: String,
) : User {
    override var isActive: Boolean = false
    override val messages: MutableList<String> = mutableListOf()

    init {
        println("---------------------------$name------------------------------")
    }

    override fun receive(message: String, from: User?) {
        messages.add(message)
        println("Test(${name}) receive message : $message")
    }

    override fun send(message: String, to: User?, mode: Mode): Result<Boolean> {
        val result = chatMediator.send(message, this, to, mode)
        println("Test(${name}) send message(${to?.name ?: ""}) result: ${result.message ?: result.data}")
        return result
    }

    override fun join(): Result<Boolean> {
        val result = chatMediator.registerUser(this)
        println("Test(${name}) register ${result.message ?: result.data}")
        return result
    }

    override fun leave(): Result<Boolean> {
        val result = chatMediator.unregisterUser(this)
        println("Test(${name}) unregister ${result.message ?: result.data}")
        return result
    }
}
