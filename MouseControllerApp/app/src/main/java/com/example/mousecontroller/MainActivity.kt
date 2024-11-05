package com.example.mousecontroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    private lateinit var client: OkHttpClient
    private var webSocket: WebSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        client = OkHttpClient()

        val request = Request.Builder().url("ws://<your-ip>>:6789").build()
        webSocket = client.newWebSocket(request, MouseWebSocketListener())

        setContent {
            MaterialTheme {
                MouseController(webSocket)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocket?.close(1000, "App closed")
        client.dispatcher.executorService.shutdown()
    }
}

@Composable
fun MouseController(webSocket: WebSocket?) {
    var deltaX by remember { mutableFloatStateOf(0f) }
    var deltaY by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            if (deltaX != 0f || deltaY != 0f) {
                val data = JSONObject().apply {
                    put("type", "move")
                    put("x", deltaX)
                    put("y", deltaY)
                }
                webSocket?.send(data.toString())
                deltaX = 0f
                deltaY = 0f
            }
            delay(50)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(300.dp, 200.dp)
                .background(Color.LightGray)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        deltaX += dragAmount.x
                        deltaY += dragAmount.y
                        change.consume()
                    }
                }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                val data = JSONObject().apply {
                    put("type", "click")
                    put("button", "left")
                }
                webSocket?.send(data.toString())
            }) {
                Text("Left Click")
            }

            Button(onClick = {
                val data = JSONObject().apply {
                    put("type", "click")
                    put("button", "right")
                }
                webSocket?.send(data.toString())
            }) {
                Text("Right Click")
            }
        }
    }
}

private class MouseWebSocketListener : WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
        println("Connected to server")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        println("Receiving message: $text")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        println("Closing: $code / $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
        println("Error: ${t.message}")
    }
}


