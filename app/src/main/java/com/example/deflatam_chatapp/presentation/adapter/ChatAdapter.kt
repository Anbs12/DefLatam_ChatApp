package com.example.deflatam_chatapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.deflatam_chatapp.R
import com.example.deflatam_chatapp.domain.model.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/**
 * Adaptador para mostrar la lista de mensajes en un RecyclerView.
 */
class ChatAdapter(
    private val currentUserId: String,
    private val onItemClick: (Message) -> Unit
) : ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_SENT = 1 // Mensaje enviado por el usuario actual.
        private const val VIEW_TYPE_RECEIVED = 2 // Mensaje recibido de otro usuario.
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.senderId == currentUserId) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_sent, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_received, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is SentMessageViewHolder -> holder.bind(message)
            is ReceivedMessageViewHolder -> holder.bind(message)
        }
    }

    /**
     * ViewHolder para mensajes enviados por el usuario actual.
     */
    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageContentTextView: TextView = itemView.findViewById(R.id.messageContentTextView)
        private val timestampTextView: TextView = itemView.findViewById(R.id.timestampTextView)
        private val messageStatusTextView: TextView = itemView.findViewById(R.id.messageStatusTextView)
        private val fileNameTextView: TextView = itemView.findViewById(R.id.fileNameTextView)
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        init {
            itemView.setOnClickListener {
                onItemClick(getItem(adapterPosition))
            }
        }

        /**
         * Enlaza los datos de un mensaje enviado con las vistas.
         * @param message El mensaje a enlazar.
         */
        fun bind(message: Message) {
            val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            timestampTextView.text = dateFormat.format(Date(message.timestamp))
            messageStatusTextView.text = when (message.status) {
                Message.MessageStatus.SENT -> "Enviado"
                Message.MessageStatus.DELIVERED -> "Entregado"
                Message.MessageStatus.READ -> "Leído"
            }

            // Manejo de diferentes tipos de mensajes
            when (message.type) {
                Message.MessageType.TEXT -> {
                    messageContentTextView.text = message.content
                    messageContentTextView.visibility = View.VISIBLE
                    fileNameTextView.visibility = View.GONE
                    imageView.visibility = View.GONE
                }
                Message.MessageType.IMAGE -> {
                    messageContentTextView.visibility = View.GONE
                    fileNameTextView.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                    Glide.with(itemView.context)
                        .load(message.fileUrl)
                        .placeholder(R.drawable.ic_notification) // Placeholder mientras carga
                        .error(R.drawable.ic_notification) // Imagen de error
                        .into(imageView)
                }
                Message.MessageType.FILE -> {
                    messageContentTextView.text = message.content // Mostrar "File: fileName"
                    fileNameTextView.text = message.fileName // Mostrar el nombre del archivo
                    messageContentTextView.visibility = View.VISIBLE
                    fileNameTextView.visibility = View.VISIBLE
                    imageView.visibility = View.GONE
                }
            }
        }
    }

    /**
     * ViewHolder para mensajes recibidos de otros usuarios.
     */
    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val senderNameTextView: TextView = itemView.findViewById(R.id.senderNameTextView)
        private val messageContentTextView: TextView = itemView.findViewById(R.id.messageContentTextView)
        private val timestampTextView: TextView = itemView.findViewById(R.id.timestampTextView)
        private val fileNameTextView: TextView = itemView.findViewById(R.id.fileNameTextView)
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        init {
            itemView.setOnClickListener {
                onItemClick(getItem(adapterPosition))
            }
        }

        /**
         * Enlaza los datos de un mensaje recibido con las vistas.
         * @param message El mensaje a enlazar.
         */
        fun bind(message: Message) {
            senderNameTextView.text = message.senderId // Idealmente, buscarías el username por ID aquí.
            val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            timestampTextView.text = dateFormat.format(Date(message.timestamp))

            // Manejo de diferentes tipos de mensajes
            when (message.type) {
                Message.MessageType.TEXT -> {
                    messageContentTextView.text = message.content
                    messageContentTextView.visibility = View.VISIBLE
                    fileNameTextView.visibility = View.GONE
                    imageView.visibility = View.GONE
                }
                Message.MessageType.IMAGE -> {
                    messageContentTextView.visibility = View.GONE
                    fileNameTextView.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                    Glide.with(itemView.context)
                        .load(message.fileUrl)
                        .placeholder(R.drawable.ic_notification)
                        .error(R.drawable.ic_notification)
                        .into(imageView)
                }
                Message.MessageType.FILE -> {
                    messageContentTextView.text = message.content // Mostrar "File: fileName"
                    fileNameTextView.text = message.fileName // Mostrar el nombre del archivo
                    messageContentTextView.visibility = View.VISIBLE
                    fileNameTextView.visibility = View.VISIBLE
                    imageView.visibility = View.GONE
                }
            }
        }
    }

    /**
     * Callback para calcular las diferencias entre listas de mensajes.
     */
    class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
}
