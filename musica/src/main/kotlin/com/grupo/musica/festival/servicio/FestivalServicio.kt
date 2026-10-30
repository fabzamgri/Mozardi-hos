/**
 * @author Carlos Prada Escortell
 * Entidad: Festival
 */
package com.grupo.musica.festival.servicio

import com.grupo.musica.festival.modelo.Festival
import com.grupo.musica.festival.repositorio.FestivalRepositorio
import org.springframework.stereotype.Service

@Service
class FestivalServicio(private val repositorio: FestivalRepositorio) {

    // Valores del desplegable de categoría
    val categorias = listOf("Pop/Indie", "Rock", "Electrónica", "Urbano", "Mixto")

    fun listar(): List<Festival> = repositorio.leerTodosFestivales()

    fun buscar(id: Int): Festival? = repositorio.buscarPorId(id)

    fun guardar(festival: Festival) = repositorio.guardarFestival(festival)

    fun borrar(id: Int): Boolean = repositorio.borrarPorId(id)

    // Buscador: filtra por texto (nombre o ciudad) y por categoría a la vez
    fun filtrar(texto: String?, categoria: String?): List<Festival> {
        var resultado = listar()

        if (!texto.isNullOrBlank()) {
            val buscado = texto.trim()
            resultado = resultado.filter {
                it.nombre.contains(buscado, ignoreCase = true) || it.ciudad.contains(buscado, ignoreCase = true)
            }
        }

        if (!categoria.isNullOrBlank()) {
            resultado = resultado.filter { it.categoria == categoria }
        }

        // Ordenados por ID: los nuevos siempre salen debajo de los ya creados
        return resultado.sortedBy { it.id }
    }

    // Devuelve un mensaje de error si algún dato no es válido, o null si todo está bien
    fun validar(festival: Festival): String? {
        if (festival.nombre.isBlank()) return "El nombre no puede estar vacío."
        if (festival.ciudad.isBlank()) return "La ciudad no puede estar vacía."
        if (festival.asistentes < 0) return "Los asistentes no pueden ser negativos."
        if (festival.precioAbono < 0) return "El precio del abono no puede ser negativo."
        if (festival.categoria !in categorias) return "Elige una categoría de la lista."
        if (festival.descripcion.isBlank()) return "La descripción no puede estar vacía."
        if (festival.foto.contains("/") || festival.foto.contains("\\")) {
            return "En foto escribe solo el nombre del archivo (por ejemplo: arenal.jpg)."
        }
        // El ; rompería el CSV
        if (festival.nombre.contains(";") || festival.ciudad.contains(";") ||
            festival.descripcion.contains(";") || festival.foto.contains(";")
        ) {
            return "Ningún campo puede contener el carácter ';'."
        }
        return null
    }
}
