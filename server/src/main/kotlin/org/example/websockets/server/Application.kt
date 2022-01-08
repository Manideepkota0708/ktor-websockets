package org.example.websockets.server

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(){
    embeddedServer(Netty, port = 8080){
        routing {
            get("/"){
                call.respondText { "Hello webSocketServer" }
            }
        }
    }.start(wait = true)
}