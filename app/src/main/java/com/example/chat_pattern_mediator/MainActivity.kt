package com.example.chat_pattern_mediator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_pattern_mediator.interfaces.ChatMediator
import com.example.chat_pattern_mediator.interfaces.User
import com.example.chat_pattern_mediator.resource.Mode
import com.example.chat_pattern_mediator.resource.Result
import com.example.chat_pattern_mediator.ui.theme.ChatPatternMediatorTheme


class MainActivity() : ComponentActivity(), User {
    private val mediator: ChatMediator.Base = ChatMediator.Base()
    private val usersList = mutableListOf<String>()
        //FIXME Быстрая реализация которая мягко говоря так себе))
        get() {
            field.clear()
            mediator.getUsers().forEach {
                field.add(it.name)
            }
            if (field.find { it == "Всё" } != null)
                field.add(0, "Все")
            return field
        }
    private var userName: MutableState<String>? = null
    private var messagesState: SnapshotStateList<String>? = null

    private var userTo: User.Base? = null

    override val messages: MutableList<String> = mutableListOf()
    override val name: String = "Android"
    override var isActive: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContent()
        }

    }

    override fun receive(message: String, from: User?) {
        messagesState?.add(message)
    }

    override fun send(message: String, to: User?, mode: Mode): Result<Boolean> {
        val result = mediator.send(message, this, to, mode)
        result.message?.let { messagesState?.add(it) }
        return result
    }

    override fun join(): Result<Boolean> {
        val result = mediator.registerUser(this)
        if (result.message !== null) {
            messagesState?.add(result.message)
        }
        return result
    }

    override fun leave(): Result<Boolean> {
        val result = mediator.unregisterUser(this)
        if (result.message != null)
            messagesState?.add(result.message)
        return result
    }


    @Composable
    fun Chat(messages: MutableList<String>) {
        messages.forEach {
            Message(it)
        }
    }

    @Composable
    fun MainContent() {
        remember {
            messagesState = messages.toMutableStateList()
            userTo = User.Base(chatMediator = mediator, "Oleg")
        }

        val (textMessage, setTextMessage) = remember { mutableStateOf("") }
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                TextField(value = textMessage,
                    onValueChange = { setTextMessage(it) })
                UsersSelection()
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center) {
                    Button(onClick = {
                        var userTo: User? = null
                        if (userName?.value != "Все") {
                            userTo = mediator.getUsers().find { it.name == userName?.value }
                        }
                        send(message = textMessage, to = userTo)
                    }) {
                        Text(text = "Send")
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                    Button(
                        onClick = {
                            messagesState?.clear()
                        },
                    ) {
                        Text(text = "Clear chat")
                    }
                }

                Button(
                    onClick = {
                        userTo?.send(
                            message = "Message(${messagesState?.size ?: 0})",
                            to = this@MainActivity)
                    },
                ) {
                    Text(text = "Get message")
                }
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center) {
                    Button(
                        onClick = {
                            join()
                        },
                    ) {
                        Text(text = "Join chat")
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                    Button(
                        onClick = {
                            leave()
                        },
                    ) {
                        Text(text = "Leave chat")
                    }
                    Spacer(modifier = Modifier.width(15.dp))

                    Button(
                        onClick = {
                            userTo?.join()
                        },
                    ) {
                        Text(text = "Join another")
                    }
                }

            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 15.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                messagesState?.let { Chat(it) }
            }

        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        ChatPatternMediatorTheme {
            Chat(mutableListOf("One", "Two"))
        }
    }

    @Composable
    fun Message(msg: String) {
        Box() {
            Text(text = msg)
        }
    }

    @Composable
    fun UsersSelection() {
        userName = remember {
            mutableStateOf("Все")
        }
        var expanded by remember { mutableStateOf(false) }

        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Row(
                Modifier
                    .padding(24.dp)
                    .clickable {
                        expanded = !expanded
                    }
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                userName?.value?.let {
                    Text(text = it,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(end = 8.dp))
                }
                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")

                DropdownMenu(expanded = expanded, onDismissRequest = {
                    expanded = false
                }) {
                    usersList.forEach { user ->
                        if (user != this@MainActivity.name) {
                            DropdownMenuItem(onClick = {
                                expanded = false
                                userName?.value = user
                            }) {
                                Text(text = user)
                            }
                        }

                    }
                }
            }
        }

    }
}

