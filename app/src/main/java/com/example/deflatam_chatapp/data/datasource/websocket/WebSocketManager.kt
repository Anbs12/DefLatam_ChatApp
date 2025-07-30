package com.example.deflatam_chatapp.data.datasource.websocket

import com.example.deflatam_chatapp.domain.model.Message
import com.example.deflatam_chatapp.utils.Constants
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString


/**
 * Gestiona la conexión WebSocket para el chat en tiempo real.
 */
@Singleton
class WebSocketManager @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson
) {

    private var webSocket: WebSocket? = null
    private val _incomingMessages = MutableSharedFlow<Message>(extraBufferCapacity = 1)
    val incomingMessages: SharedFlow<Message> = _incomingMessages

    /**
     * Conecta al servidor WebSocket.
     */
    fun connect() {
        val request = Request.Builder().url(Constants.WEBSOCKET_URL).build()
        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // Conexión WebSocket abierta.
                println("WebSocket abierto: ${response.message}")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                // Mensaje de texto recibido.
                try {
                    val message = gson.fromJson(text, Message::class.java)
                    _incomingMessages.tryEmit(message)
                } catch (e: JsonSyntaxException) {
                    println("Error parsing WebSocket message: ${e.message}")
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                // Mensaje de bytes recibido (no usado actualmente para el chat).
                println("Mensaje binario recibido: ${bytes.hex()}")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                // WebSocket a punto de cerrarse.
                println("WebSocket cerrando: $code / $reason")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                // WebSocket cerrado.
                println("WebSocket cerrado: $code / $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                // Error en la conexión WebSocket.
                println("Fallo del WebSocket: ${t.message}")
                t.printStackTrace()
                // Implementar lógica de reconexión si es necesario.
            }
        })
    }

    /**
     * Envía un mensaje a través del WebSocket.
     * @param message El mensaje a enviar.
     */
    fun sendMessage(message: Message) {
        val jsonMessage = gson.toJson(message)
        webSocket?.send(jsonMessage)
    }

    /**
     * Desconecta del servidor WebSocket.
     */
    fun disconnect() {
        webSocket?.close(1000, "Disconnected by client")
        webSocket = null
    }
}

