package com.example.deflatam_chatapp.data.datasource.websocket

import android.util.Log
import com.example.deflatam_chatapp.domain.model.Message
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString


/**
 * Gestiona la conexión WebSocket para la comunicación en tiempo real.
 */
@Singleton
class WebSocketManager @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson // Usamos Gson para serializar/deserializar mensajes JSON.
) {
    private var webSocket: WebSocket? = null
    private val _incomingMessages = Channel<Message>(Channel.UNLIMITED)
    val incomingMessages: Flow<Message> = _incomingMessages.receiveAsFlow()

    private val TAG = "WebSocketManager"

    /**
     * Conecta a un servidor WebSocket.
     * @param url La URL del servidor WebSocket.
     * @param roomId El ID de la sala a la que conectarse (para filtrar mensajes).
     */
    fun connect(url: String, roomId: String) {
        val request = Request.Builder().url(url).build()
        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket connected: ${response.message}")
                // Podríamos enviar un mensaje de "unirse a sala" aquí si el backend lo requiere.
                // sendWebSocketMessage(WebSocketMessage(type = "JOIN_ROOM", payload = roomId))
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Receiving: $text")
                try {
                    // Asumimos que el servidor envía mensajes de chat como JSON.
                    val message = gson.fromJson(text, Message::class.java)
                    // Podríamos añadir una validación extra aquí si el mensaje es para la sala correcta.
                    if (message.roomId == roomId) {
                        _incomingMessages.trySend(message) // Enviar mensaje al flujo.
                    }
                } catch (e: JsonSyntaxException) {
                    Log.e(TAG, "Failed to parse WebSocket message: $text", e)
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d(TAG, "Receiving bytes: ${bytes.hex()}")
                // Manejar mensajes binarios si es necesario, por ejemplo, archivos pequeños.
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "Closing: $code / $reason")
                webSocket.close(1000, null)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "Error: ${t.message}", t)
                // Implementar lógica de reconexión si es necesario.
            }
        })
    }

    /**
     * Envía un mensaje a través del WebSocket.
     * @param message El objeto [Message] a enviar.
     * @return True si el mensaje fue enviado, false de lo contrario.
     */
    fun sendWebSocketMessage(message: Message): Boolean {
        return webSocket?.send(gson.toJson(message)) ?: false
    }

    /**
     * Desconecta el WebSocket.
     */
    fun disconnect() {
        webSocket?.close(1000, "Disconnected by client")
        webSocket = null
        _incomingMessages.close() // Cierra el canal cuando se desconecta.
        Log.d(TAG, "WebSocket disconnected")
    }
}
