package org.example.websockets.server

import io.ktor.http.cio.websocket.*
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * user id holds the users currently in the chat session
 */
internal var userId = AtomicInteger(0)

internal val connectionsSet = Collections.synchronizedSet(linkedSetOf<Connection>())

class Connection(
    internal val session: DefaultWebSocketSession,
    internal val name: String = "User ${userId.getAndIncrement()}"
)