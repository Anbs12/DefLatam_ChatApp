package com.example.deflatam_chatapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.deflatam_chatapp.R
import com.example.deflatam_chatapp.domain.model.ChatRoom


/**
 * Adaptador para mostrar la lista de salas de chat en un RecyclerView.
 */
class SalasAdapter(private val onItemClick: (ChatRoom) -> Unit) :
    ListAdapter<ChatRoom, SalasAdapter.ChatRoomViewHolder>(ChatRoomDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chatroom, parent, false)
        return ChatRoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        val chatRoom = getItem(position)
        holder.bind(chatRoom)
    }

    /**
     * ViewHolder para cada elemento de sala de chat.
     */
    inner class ChatRoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val roomNameTextView: TextView = itemView.findViewById(R.id.roomNameTextView)
        private val roomDescriptionTextView: TextView = itemView.findViewById(R.id.roomDescriptionTextView)

        init {
            itemView.setOnClickListener {
                onItemClick(getItem(adapterPosition))
            }
        }

        /**
         * Enlaza los datos de una sala de chat con las vistas del ViewHolder.
         * @param chatRoom La sala de chat a enlazar.
         */
        fun bind(chatRoom: ChatRoom) {
            roomNameTextView.text = chatRoom.name
            roomDescriptionTextView.text = chatRoom.description ?: "Sin descripción"
        }
    }

    /**
     * Callback para calcular las diferencias entre listas de salas de chat.
     */
    class ChatRoomDiffCallback : DiffUtil.ItemCallback<ChatRoom>() {
        override fun areItemsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
            return oldItem == newItem
        }
    }
}
