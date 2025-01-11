package com.meditrust.findadoctor.core.data.source.remote


import com.meditrust.findadoctor.core.data.model.ChatGPTRequest
import com.meditrust.findadoctor.core.data.model.ChatGPTResponse
import com.meditrust.findadoctor.core.util.ChatGptUtils
import retrofit2.Call

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatGPTApi {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer ${ChatGptUtils.KEY}" // Replace with your actual API key
    )
    @POST("v1/chat/completions")
    fun getChatResponse(@Body request: ChatGPTRequest): Call<ChatGPTResponse>
}
