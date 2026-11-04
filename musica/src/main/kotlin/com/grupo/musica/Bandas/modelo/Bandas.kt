/**
 * @author Fabio Zamudio Grimaldo
 * Entidad: Bandas
 */

package com.grupo.musica.Bandas.modelo

data class Bandas(val id: Int,
    val nombre: String,
    val genero: String,
    val ano_fundacion: Int,
    var ventas_millones: Double? = null)