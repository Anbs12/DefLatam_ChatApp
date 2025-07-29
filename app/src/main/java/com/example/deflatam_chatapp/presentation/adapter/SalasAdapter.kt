package com.example.deflatam_chatapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


/**
 * Adaptador para mostrar la lista de salas de chat en un RecyclerView.
 */
class SalasAdapter(private val salas: List<String>) : RecyclerView.Adapter<SalasAdapter.SalasViewHolder>() {

    /**
     * Define la vista de cada elemento de la sala.
     */
    class SalasViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val salaName: TextView = view.findViewById(android.R.id.text1) // Usando un layout simple de Android para empezar
    }

    /**
     * Crea nuevas vistas (invocado por el layout manager).
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalasViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false) // Puedes cambiar esto por un layout personalizado más adelante
        return SalasViewHolder(view)
    }

    /**
     * Reemplaza el contenido de una vista (invocado por el layout manager).
     */
    override fun onBindViewHolder(holder: SalasViewHolder, position: Int) {
        holder.salaName.text = salas[position]
    }

    /**
     * Devuelve el tamaño de tu conjunto de datos (invocado por el layout manager).
     */
    override fun getItemCount(): Int = salas.size
}
