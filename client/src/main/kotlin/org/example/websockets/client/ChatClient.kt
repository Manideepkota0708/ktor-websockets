package org.example.websockets.client

import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    HttpClient {
        install(WebSockets)
    }.use {
        runBlocking {
            it.webSocket(method = HttpMethod.Get, port = 8080, path = "/chat") {
                val sendMessagesJob = launch { sendMessages() }
                val receiveMessagesJob = launch { receiveMessages() }

                sendMessagesJob.join() // waiting for completion from client end by exit or by error
                receiveMessagesJob.cancelAndJoin()
            }
        }
    }
}

private suspend fun DefaultClientWebSocketSession.sendMessages() {
    while (true) {
        val message = readLine() ?: ""
        if (message.equals("exit", ignoreCase = true)) return
        try {
            send(message)
        } catch (e: Exception) {
            println("Error while sending: ${e.localizedMessage}")
            return
        }
    }
}

private suspend fun DefaultClientWebSocketSession.receiveMessages() {
    try {
        for (message in incoming) {
            message as? Frame.Text ?: continue
            println(message.readText())
        }
    } catch (e: Exception) {
        println("Error while receiving : ${e.localizedMessage}")
    }
}