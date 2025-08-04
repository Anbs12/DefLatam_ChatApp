package com.example.deflatam_chatapp.presentation.ui.perfil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.deflatam_chatapp.R
import com.example.deflatam_chatapp.presentation.ui.login.LoginActivity
import com.example.deflatam_chatapp.presentation.viewmodel.PerfilViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


/**
 * Actividad para mostrar y gestionar el perfil del usuario.
 */
@AndroidEntryPoint
class PerfilActivity : AppCompatActivity() {

    private val perfilViewModel: PerfilViewModel by viewModels()
    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        usernameTextView = findViewById(R.id.usernameTextView)
        emailTextView = findViewById(R.id.emailTextView)
        logoutButton = findViewById(R.id.logoutButton)

        // Observa los datos del usuario actual.
        lifecycleScope.launch {
            perfilViewModel.currentUser.collect { user ->
                if (user != null) {
                    usernameTextView.text = user.username
                    emailTextView.text = user.email
                } else {
                    usernameTextView.text = "No autenticado"
                    emailTextView.text = "N/A"
                    // Si el usuario es nulo, redirigir al login
                    //Toast.makeText(this@PerfilActivity, "Sesión caducada. Por favor, inicia sesión de nuevo.", Toast.LENGTH_SHORT).show()
                    /*val intent = Intent(this@PerfilActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finishAffinity() // Cierra todas las actividades anteriores.*/
                }
            }
        }

        // Observa los eventos de UI.
        lifecycleScope.launch {
            perfilViewModel.eventFlow.collect { event ->
                when (event) {
                    is PerfilViewModel.UiEvent.ShowToast -> {
                        Toast.makeText(this@PerfilActivity, event.message, Toast.LENGTH_SHORT).show()
                    }
                    PerfilViewModel.UiEvent.LoggedOut -> {
                        Toast.makeText(this@PerfilActivity, "Sesión cerrada.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@PerfilActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finishAffinity() // Cierra todas las actividades anteriores.
                    }
                }
            }
        }

        logoutButton.setOnClickListener {
            perfilViewModel.logout()
        }
    }
}
