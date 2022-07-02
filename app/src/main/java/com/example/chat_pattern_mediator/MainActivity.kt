package com.example.chat_pattern_mediator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chat_pattern_mediator.ui.theme.ChatPatternMediatorTheme


class MainActivity : ComponentActivity(), User {
    private val mediator: ChatMediator.Base = ChatMediator.Base()
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
        mediator.warningListener = { msg->
            messages?.add(msg)
        }
        setContent {
            MainContent()
        }

    }

    override fun receive(message: String, from: User?) {
        messages?.add(message)
    }

    override fun send(message: String, to: User?, mode: Mode): Boolean {
        return mediator.send(message, this, to, mode)
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
                Button(
                    onClick = {
                        send(message = textMessage)
                    }) {
                    Text(text = "Send")
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
}

