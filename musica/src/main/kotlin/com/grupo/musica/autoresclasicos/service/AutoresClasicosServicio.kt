package com.grupo.musica.autoresclasicos.service

import com.grupo.musica.autoresclasicos.model.AutoresClasicos
import com.grupo.musica.autoresclasicos.repositories.AutoresClasicosRepositories

import org.springframework.stereotype.Service

/*
Lucas Palermo Andreu
*/
@Service
class AutoresClasicosService(private val repository: AutoresClasicosRepositories) {

    fun obtenerTodos(): List<AutoresClasicos> = repository.leerTodos()

    fun obtenerPorId(id: Int): AutoresClasicos? {
        return repository.leerTodos().find { it.id == id }
    }

    fun guardar(autor: AutoresClasicos) {
        val autores = repository.leerTodos().toMutableList()
        val indice = autores.indexOfFirst { it.id == autor.id }

        if (indice != -1) {
            autores[indice] = autor
        } else {
            val nuevoId = if (autores.isEmpty()) 1 else autores.maxOf { it.id } + 1
            autor.id = nuevoId
            autores.add(autor)
        }
        repository.guardarTodos(autores)
    }

    fun eliminar(id: Int) {
        val autoresActualizados = repository.leerTodos().filter { it.id != id }
        repository.guardarTodos(autoresActualizados)
    }
    fun resetear() {
        repository.resetearDatos()
    }

    // Siglos distintos que hay en el CSV, para el desplegable del buscador
    fun siglos(): List<String> = obtenerTodos().map { it.siglo }.filter { it.isNotBlank() }.distinct().sorted()

    // Buscador: filtra por texto (nombre u obras) y por siglo a la vez
    fun filtrar(texto: String?, siglo: String?): List<AutoresClasicos> {
        var resultado = obtenerTodos()

        if (!texto.isNullOrBlank()) {
            val buscado = texto.trim()
            resultado = resultado.filter {
                it.nombre.contains(buscado, ignoreCase = true) ||
                        it.obrasDestacadas.contains(buscado, ignoreCase = true)
            }
        }

        if (!siglo.isNullOrBlank()) {
            resultado = resultado.filter { it.siglo == siglo }
        }

        // Ordenados por ID: los nuevos siempre salen debajo de los ya creados
        return resultado.sortedBy { it.id }
    }

    // Devuelve un mensaje de error si algún dato no es válido, o null si todo está bien
    fun validar(autor: AutoresClasicos): String? {
        if (autor.nombre.isBlank()) return "El nombre no puede estar vacío."
        if (autor.siglo.isBlank()) return "El siglo no puede estar vacío."
        if (autor.fechaNacimiento.isBlank()) return "La fecha de nacimiento no puede estar vacía."
        if (autor.obrasDestacadas.isBlank()) return "Las obras destacadas no pueden estar vacías."
        return null
    }
}
