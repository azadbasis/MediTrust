package com.meditrust.findadoctor.core.data.model

data class ChatGPTRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Message>
)

data class Message(
    val role: String,
    val content: String
)

data class ChatGPTResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

