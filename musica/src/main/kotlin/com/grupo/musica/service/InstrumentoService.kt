package com.grupo.musica.service

import com.grupo.musica.model.Instrumento
import com.grupo.musica.repository.InstrumentoRepository
import org.springframework.stereotype.Service

/**
 * @author Lucia Troncho Ortí
 * Entidad: Instrumentos
 */

@Service
class InstrumentoService(private val repository: InstrumentoRepository) {
    fun listar(): List<Instrumento> = repository.leerInstrumentos()
    fun buscar(id: Int): Instrumento? = repository.buscarPorId(id)
    fun guardar(instrumento: Instrumento) = repository.guardarInstrumento(instrumento)
    fun borrar(id: Int) = repository.borrarPorId(id)
}