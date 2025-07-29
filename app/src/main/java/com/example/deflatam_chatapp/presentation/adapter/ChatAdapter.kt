package com.example.deflatam_chatapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


/**
 * Adaptador para mostrar los mensajes de chat en un RecyclerView.
 */
class ChatAdapter(private val messages: List<String>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    /**
     * Define la vista de cada elemento del mensaje.
     */
    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(android.R.id.text1) // Usando un layout simple de Android para empezar
        val senderText: TextView = view.findViewById(android.R.id.text2)
    }

    /**
     * Crea nuevas vistas (invocado por el layout manager).
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false) // Puedes cambiar esto por un layout personalizado más adelante
        return ChatViewHolder(view)
    }

    /**
     * Reemplaza el contenido de una vista (invocado por el layout manager).
     */
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        // En un caso real, messages sería una lista de objetos Message con remitente y contenido.
        val messageParts = messages[position].split(": ", limit = 2)
        if (messageParts.size == 2) {
            holder.senderText.text = messageParts[0]
            holder.messageText.text = messageParts[1]
        } else {
            holder.senderText.text = "Desconocido"
            holder.messageText.text = messages[position]
        }
    }

    /**
     * Devuelve el tamaño de tu conjunto de datos (invocado por el layout manager).
     */
    override fun getItemCount(): Int = messages.size
}
