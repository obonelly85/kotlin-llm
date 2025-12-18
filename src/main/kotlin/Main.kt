package org.example

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    print("Enter your prompt: ")
    var userPrompt = readLine()?.trim()
    if (userPrompt.isNullOrEmpty()) {
        println("Prompt cannot be empty.")
        return
    }
    while (userPrompt != "stop") {
        callOpenAIAPI(userPrompt!!)
        userPrompt = readLine()?.trim()
    }
}

fun callOpenAIAPI(userPrompt: String) {
    val jsonBody = JSONObject()
        .put("model", "llama3.1")
        .put("prompt", userPrompt)
        .put("stream", false)

    val requestBody = jsonBody
        .toString()
        .toRequestBody("application/json".toMediaType())

    val request = Request.Builder()
        .url("http://localhost:11434/api/generate")
        .post(requestBody)
        .build()

    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS) // IMPORTANT
        .build()

    // Measure execution time
    val startTime = System.currentTimeMillis()

    client.newCall(request).execute().use { response ->
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        println("Request completed in $duration ms")
        if (!response.isSuccessful) {
            println("Request failed. Response code: ${response.code}")
            return
        }

        val responseBody = response.body?.string()
        responseBody?.let {
            val jsonResponse = JSONObject(responseBody)
            val generatedText = jsonResponse.optString("response", "No text found in response.")
            println("Generated Text: $generatedText")
        } ?: println("Response body is null.")
    }
}
