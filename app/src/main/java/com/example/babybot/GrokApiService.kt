package com.example.babybot

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GrokApiService {
    @POST("chat/completions") // Adjust based on xAI API docs
    suspend fun sendMessage(
        @Header("Authorization") apiKey: String,
        @Body request: ChatRequest
    ): ChatResponse
}

data class ChatRequest(
    val messages: List<Message>,
    val model: String = "grok-3" // Adjust model name
)

data class Message(
    val role: String,
    val content: String
)

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)