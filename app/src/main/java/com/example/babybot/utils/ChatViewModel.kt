package com.example.babybot.utils

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babybot.BuildConfig
import com.example.babybot.utils.RetrofitClient
import kotlinx.coroutines.launch

open class ChatViewModel : ViewModel() {
    private val _messages = mutableStateListOf<Message>()
    open val messages: List<Message> get() = _messages
    private val _isLoading = mutableStateOf(false)
    open val isLoading: Boolean get() = _isLoading.value

    open fun sendMessage(userMessage: String) {
        val message = Message(role = "user", content = userMessage)
        _messages.add(message)
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val apiKey = if (BuildConfig.DEBUG) {
                    BuildConfig.API_KEY
                } else {
                    BuildConfig.API_KEY
                }
                if (apiKey.isBlank()) {
                    _messages.add(Message(role = "system", content = "API key is missing"))
                    _isLoading.value = false
                    return@launch
                }
                val response = RetrofitClient.grokApiService.sendMessage(
                    apiKey = "Bearer $apiKey",
                    request = ChatRequest(messages = listOf(message))
                )
                _messages.add(response.choices.first().message)
            } catch (e: Exception) {
                _messages.add(Message(role = "system", content = "Error: ${e.message}"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    // New function to delete a specific message
    open fun deleteMessage(index: Int) {
        if (index in 0 until _messages.size) {
            _messages.removeAt(index)
        }
    }
}