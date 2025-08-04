package com.example.deflatam_chatapp.presentation.ui.salas


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deflatam_chatapp.R
import com.example.deflatam_chatapp.presentation.adapter.SalasAdapter
import com.example.deflatam_chatapp.presentation.ui.chat.ChatActivity
import com.example.deflatam_chatapp.presentation.ui.perfil.PerfilActivity
import com.example.deflatam_chatapp.presentation.viewmodel.SalasViewModel
import com.example.deflatam_chatapp.utils.Constants
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Actividad que muestra la lista de salas de chat y permite crear nuevas.
 */
@AndroidEntryPoint
class SalasActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    private val salasViewModel: SalasViewModel by viewModels()
    private lateinit var salasAdapter: SalasAdapter
    private lateinit var createRoomNameEditText: EditText
    private lateinit var createRoomButton: Button
    private lateinit var profileButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salas)

        createRoomNameEditText = findViewById(R.id.createRoomNameEditText)
        createRoomButton = findViewById(R.id.createRoomButton)
        profileButton = findViewById(R.id.profileButton)
        val recyclerView: RecyclerView = findViewById(R.id.salasRecyclerView)

        salasAdapter = SalasAdapter { chatRoom ->
            // Maneja el clic en una sala de chat.
            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra(Constants.EXTRA_ROOM_ID, chatRoom.id)
                putExtra(Constants.EXTRA_ROOM_NAME, chatRoom.name)
            }
            startActivity(intent)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SalasActivity)
            adapter = salasAdapter
        }

        // Observa la lista de salas de chat.
        lifecycleScope.launch {
            salasViewModel.chatRooms.collect { chatRooms ->
                salasAdapter.submitList(chatRooms)
            }
        }

        // Observa los eventos del ViewModel (errores, éxito).
        lifecycleScope.launch {
            salasViewModel.eventFlow.collect { event ->
                when (event) {
                    is SalasViewModel.UiEvent.ShowToast -> {
                        Toast.makeText(this@SalasActivity, event.message, Toast.LENGTH_SHORT).show()
                    }
                    SalasViewModel.UiEvent.RoomCreated -> {
                        createRoomNameEditText.text.clear()
                        Toast.makeText(this@SalasActivity, "Sala creada con éxito!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        createRoomButton.setOnClickListener {
            val userName = "Usuario"
            val roomName = createRoomNameEditText.text.toString().trim()
            if (roomName.isNotEmpty()) {
                salasViewModel.createChatRoom(roomName)
            } else {
                Toast.makeText(this, "El nombre de la sala no puede estar vacío.", Toast.LENGTH_SHORT).show()
            }
        }

        profileButton.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Registra la pantalla cuando la actividad se vuelve visible
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "Salas de Chat")
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, "SalasActivity")
        })
    }
}

