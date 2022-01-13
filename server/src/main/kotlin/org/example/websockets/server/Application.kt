package org.example.websockets.server

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(WebSockets)
        routing {
            get("/") {
                call.respondText { "Hello webSocketServer" }
            }

            webSocket("/echoChat") {
                send("You are connected!")
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    send("You said: $receivedText")
                }
            }

            webSocket("/chat") {
                val connection = Connection(this)
                println("Adding user : ${connection.name}")
                connectionsSet += connection
                send("You are connected! and total userCount = ${connectionsSet.count()}")
                try {
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        launch {
                            val receivedText = frame.readText()
                            val textWithUserName = "[${connection.name}]: $receivedText"
//                            delay(10000)
                            println("$textWithUserName -> ${Thread.currentThread()}")
                            connectionsSet.forEach {
                                launch {
                                    println("sending to others $textWithUserName, ${Thread.currentThread()}")
//                                    delay(10000)
                                    it.session.send(textWithUserName)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    println("localizedMessage: ${e.localizedMessage}")
                    println("message: ${e.message}")

                } finally {
                    println("Removing user: ${connection.name}")
                    connectionsSet -= connection
                }
            }

        }
    }.start(wait = true)
}