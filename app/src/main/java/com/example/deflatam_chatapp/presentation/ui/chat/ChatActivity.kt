package com.example.deflatam_chatapp.presentation.ui.chat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deflatam_chatapp.R
import com.example.deflatam_chatapp.domain.model.Message
import com.example.deflatam_chatapp.presentation.adapter.ChatAdapter
import com.example.deflatam_chatapp.presentation.viewmodel.ChatViewModel
import com.example.deflatam_chatapp.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


/**
 * Actividad principal para la interfaz de chat en tiempo real.
 */
@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageEditText: EditText
    private lateinit var sendMessageButton: Button
    private lateinit var attachFileButton: ImageButton
    private lateinit var roomNameTextView: TextView

    private var roomId: String? = null
    private var roomName: String? = null

    // Contrato para seleccionar un archivo
    private val pickFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            uri?.let {
                val fileName = getFileName(it)
                chatViewModel.sendFile(it, fileName)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        roomId = intent.getStringExtra(Constants.EXTRA_ROOM_ID)
        roomName = intent.getStringExtra(Constants.EXTRA_ROOM_NAME)

        if (roomId == null) {
            Toast.makeText(this, "Error: No se proporcionó ID de sala.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        roomNameTextView = findViewById(R.id.roomNameTextView)
        messageEditText = findViewById(R.id.messageEditText)
        sendMessageButton = findViewById(R.id.sendMessageButton)
        attachFileButton = findViewById(R.id.attachFileButton)
        val recyclerView: RecyclerView = findViewById(R.id.chatRecyclerView)

        roomNameTextView.text = roomName ?: "Cargando..."

        chatAdapter = ChatAdapter(chatViewModel.currentUserId) { message ->
            // Manejar clic en un mensaje si es necesario (ej. para ver imagen a pantalla completa)
            // O marcar como leído si no es del usuario actual
            if (message.senderId != chatViewModel.currentUserId && message.status != Message.MessageStatus.READ) {
                chatViewModel.updateMessageStatus(message.id, Message.MessageStatus.READ)
            }
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true // Los nuevos mensajes aparecen abajo.
            }
            adapter = chatAdapter
        }

        // Observa los mensajes del ViewModel.
        lifecycleScope.launch {
            chatViewModel.messages.collect { messages ->
                chatAdapter.submitList(messages) {
                    // Desplazarse al último mensaje cuando la lista se actualice
                    recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                }
            }
        }

        // Observa los eventos del ViewModel.
        lifecycleScope.launch {
            chatViewModel.eventFlow.collect { event ->
                when (event) {
                    is ChatViewModel.UiEvent.ShowToast -> {
                        Toast.makeText(this@ChatActivity, event.message, Toast.LENGTH_SHORT).show()
                    }
                    ChatViewModel.UiEvent.MessageSent -> {
                        messageEditText.text.clear()
                        recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                    }
                }
            }
        }

        // Inicia la carga de mensajes para la sala actual.
        roomId?.let { chatViewModel.startObservingMessages(it) }

        sendMessageButton.setOnClickListener {
            val messageContent = messageEditText.text.toString().trim()
            if (messageContent.isNotEmpty()) {
                chatViewModel.sendMessage(messageContent, Message.MessageType.TEXT)
            }
        }

        attachFileButton.setOnClickListener {
            openFilePicker()
        }
    }

    /**
     * Abre el selector de archivos para que el usuario elija una imagen o documento.
     */
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*" // Permite seleccionar cualquier tipo de archivo.
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "application/pdf", "text/plain")) // Opcional: filtrar tipos
        }
        pickFileLauncher.launch(intent)
    }

    /**
     * Obtiene el nombre del archivo a partir de su URI.
     * @param uri La URI del archivo.
     * @return El nombre del archivo o "unknown_file".
     */
    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result ?: "unknown_file"
    }
}
