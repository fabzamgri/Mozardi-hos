package com.grupo.musica.autoresclasicos.service

import AutoresClasicos

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
}