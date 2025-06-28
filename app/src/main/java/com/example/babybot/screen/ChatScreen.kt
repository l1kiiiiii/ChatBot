package com.example.babybot.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.babybot.ChatViewModel
import com.example.babybot.Message

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel = viewModel()) {
    LazyColumn {
        itemsIndexed(viewModel.messages) { index, message ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${message.role}: ${message.content}")
                Button(onClick = { viewModel.deleteMessage(index) }) {
                    Text("Delete")
                }
            }
        }
    }
    var inputText by remember { mutableStateOf("") }

    // Colors should ideally come from Theme.kt
    val darkBackgroundColor = Color.Black
    val darkSurfaceColor = Color(0xFF1E1E1E)
    val darkOnSurfaceColor = Color.White
    val darkPrimaryColor = Color(0xFFBB86FC)

    Scaffold(
        containerColor = darkBackgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Baby Bot", color = darkOnSurfaceColor) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent // Changed to transparent
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(viewModel.messages) { message ->
                    MessageItem(
                        message = message,
                        surfaceColor = darkSurfaceColor,
                        onSurfaceColor = darkOnSurfaceColor,
                        userMessageColor =  darkPrimaryColor.copy(alpha = 0.6f)
                    )
                }
            }

            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = darkPrimaryColor
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...", color = Color.LightGray) },
                    enabled = !viewModel.isLoading,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = darkOnSurfaceColor,
                        unfocusedTextColor = darkOnSurfaceColor,
                        disabledTextColor = Color.Gray,
                        cursorColor = darkPrimaryColor,
                        focusedContainerColor = darkSurfaceColor,
                        unfocusedContainerColor = darkSurfaceColor,
                        disabledContainerColor = darkSurfaceColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(16.dp) // Added for rounded corners
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    },
                    enabled = !viewModel.isLoading,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = darkPrimaryColor,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send message"
                    )
                }
            }
        }
    }
}

// MessageItem remains unchanged
@Composable
fun MessageItem(
    message: Message,
    surfaceColor: Color,
    onSurfaceColor: Color,
    userMessageColor: Color
) {
    val bubbleColor = if (message.role == "user") userMessageColor else surfaceColor
    val textColor = if (message.role == "user") Color.Black else onSurfaceColor

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.role == "user") Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.wrapContentWidth(),
            colors = CardDefaults.cardColors(containerColor = bubbleColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
        }
    }
}
// A simplified fake ViewModel for preview purposes
class FakeChatViewModel : ChatViewModel() { // Inherit from your actual ChatViewModel

    // Override or provide fake data
    private val _previewMessages = mutableStateListOf(
        Message("user", "Hello Baby Bot! (Preview)"),
        Message("assistant", "Hello! I'm a preview. How can I help?"),
        Message("user", "Tell me a fun fact."),
        Message("assistant", "Honey never spoils! (Preview)")
    )
    override val messages: List<Message> get() = _previewMessages

    private val _previewIsLoading = mutableStateOf(false)
    override val isLoading: Boolean get() = _previewIsLoading.value

    override fun sendMessage(userMessage: String) {
        _previewMessages.add(Message("user", userMessage))
        _previewMessages.add(Message("assistant", "Preview reply to: $userMessage"))
    }

    override fun deleteMessage(index: Int) {
        if (index >= 0 && index < _previewMessages.size) {
            _previewMessages.removeAt(index)
        }
    }

    val previousPrompts: List<String> get() = listOf("Old prompt 1", "Old prompt 2")
}


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun ChatScreenPreview() {
    val fakeViewModel = remember { FakeChatViewModel() }
    ChatScreen(viewModel = fakeViewModel)
}