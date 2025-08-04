package com.example.deflatam_chatapp.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.deflatam_chatapp.R
import com.example.deflatam_chatapp.presentation.ui.chat.ChatActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Servicio de Firebase Messaging para manejar notificaciones push.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "MyFirebaseMsgService"

    /**
     * Se llama cuando se recibe un mensaje de FCM.
     * @param remoteMessage Objeto que contiene los datos del mensaje.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Comprobar si el mensaje contiene una carga de datos.
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            // Aquí puedes procesar los datos del mensaje.
            // Por ejemplo, si los datos contienen un ID de sala, puedes abrir esa sala.
            val roomId = remoteMessage.data["roomId"]
            val messageContent = remoteMessage.data["message"]
            val senderName = remoteMessage.data["senderName"]

            if (messageContent != null && senderName != null) {
                sendNotification(messageContent, senderName, roomId)
            }
        }

        // Comprobar si el mensaje contiene una carga de notificación.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            // Si el mensaje tiene un cuerpo de notificación, lo mostramos.
            sendNotification(it.body ?: "Nuevo mensaje", it.title ?: "Chat App", remoteMessage.data["roomId"])
        }
    }

    /**
     * Se llama cuando se genera un nuevo token de registro de FCM.
     * Esto sucede cuando la aplicación se instala por primera vez,
     * cuando se desinstala/reinstala, o cuando el token expira.
     * @param token El nuevo token de registro de FCM.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // Envía este token a tu servidor de aplicaciones.
        // Por ejemplo, podrías guardarlo en Firestore asociado al UID del usuario actual.
        // sendRegistrationToServer(token)
    }

    /**
     * Crea y muestra una notificación push.
     * @param messageBody El cuerpo del mensaje de la notificación.
     * @param messageTitle El título de la notificación.
     * @param roomId El ID de la sala de chat (opcional, para navegación).
     */
    private fun sendNotification(messageBody: String, messageTitle: String, roomId: String?) {
        // Intent para abrir la ChatActivity cuando se toca la notificación
        val intent = Intent(this, ChatActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Si hay un roomId, lo pasamos para que la ChatActivity sepa qué sala abrir
            roomId?.let { putExtra("roomId", it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0 /* Request code */,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE // FLAG_IMMUTABLE es requerido en Android S (API 31) y superior
        )

        val channelId = getString(R.string.default_notification_channel_id) // Define esto en strings.xml
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Usa tu ícono de notificación
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setAutoCancel(true) // La notificación se cierra al tocarla
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Para que aparezca como un "heads-up notification"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear el canal de notificación para Android 8.0 (Oreo) y superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = getString(R.string.default_notification_channel_name) // Define esto en strings.xml
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para notificaciones de chat"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID de notificación */, notificationBuilder.build())
    }
}