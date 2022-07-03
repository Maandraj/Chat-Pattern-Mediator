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
import androidx.compose.runtime.snapshots.SnapshotMutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_pattern_mediator.ui.theme.ChatPatternMediatorTheme


class MainActivity() : ComponentActivity(), User {
    override var lastMessageReceive: String = ""
    private val mediator: ChatMediator.Base = ChatMediator.Base()
    private val usersList = mutableListOf<String>()
        //FIXME Быстрая реализация которая мягко говоря так себе))
        get() {
            field.clear()
            mediator.getUsers().forEach {
                field.add(it.name)
            }
            if (field[0] != "Все")
                field.add(0, "Все")
            return field
        }
    private var userName: MutableState<String>? = null

    private var messages: SnapshotStateList<String>? = null
    private var userOne: User.Base? = null

    override val name: String = "Android"
    override var isActive: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediator.registerListener = { registerUser ->
            if (registerUser.isActive) messages?.add("${registerUser.name} joined the chat!")
            else messages?.add("${registerUser.name} leave chat!")
        }
        mediator.warningListener = { msg ->
            messages?.add(msg)
        }
        setContent {
            MainContent()
        }

    }

    override fun receive(message: String, from: User?) {
        lastMessageReceive = message
        messages?.add(message)
    }

    override fun send(message: String, to: User?, mode: Mode): Result<Boolean> {
        return mediator.send(message, this, to, mode)
    }

    override fun join(): Result<Boolean> {
       return mediator.registerUser(this)
    }

    override fun leave(): Result<Boolean> {
       return mediator.unregisterUser(this)
    }


    @Composable
    fun Chat(messages: MutableList<String>) {
        messages.forEach {
            Message(it)
        }
    }

    @Composable
    fun MainContent() {

        messages = remember { mutableStateListOf() }

        remember {
            userOne = User.Base(chatMediator = mediator, "Oleg")
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
                CountrySelection()
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
                            messages?.clear()
                        },
                    ) {
                        Text(text = "Clear chat")
                    }
                }

                Button(
                    onClick = {

                        userOne?.send(
                            message = "Message(${messages?.size ?: 0})",
                            to = this@MainActivity)
                    },
                ) {
                    Text(text = "Get message")
                }
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center) {
                    Button(
                        onClick = {
                            mediator.registerUser(this@MainActivity)
                        },
                    ) {
                        Text(text = "Join chat")
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                    Button(
                        onClick = {
                            mediator.unregisterUser(this@MainActivity)
                        },
                    ) {
                        Text(text = "Leave chat")
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

                messages?.let {
                    Chat(it)
                }

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
    fun CountrySelection() {
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

