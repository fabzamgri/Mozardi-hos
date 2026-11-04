package com.grupo.musica.instrumentos.service

import com.grupo.musica.instrumentos.model.Instrumento
import com.grupo.musica.instrumentos.repository.InstrumentoRepository
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