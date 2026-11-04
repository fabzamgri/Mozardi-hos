package com.grupo.musica.model

/**
 * @author Lucia Troncho Ortí
 * Entidad: Instrumentos
 */

data class Instrumento(val id_instrumento: Int,
                       val nombre: String,
                       val familia: String,
                       val invencion: Int,
                       val peso: Double,
                       val imagen: String
)