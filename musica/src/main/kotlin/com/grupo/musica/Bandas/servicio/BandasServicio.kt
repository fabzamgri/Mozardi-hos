/**
 * @author Fabio Zamudio Grimaldo
 * Entidad: BandasServicio
 */

package com.grupo.musica.Bandas.servicio

import com.grupo.musica.Bandas.modelo.Bandas
import com.grupo.musica.Bandas.repositorio.BandasRepositorio
import org.springframework.stereotype.Service

@Service
class BandasServicio(private val repositorio: BandasRepositorio) {
    fun listar(): List<Bandas> = repositorio.leerTodasBandas()

    fun buscar(id: Int): Bandas? = repositorio.buscarPorID(id)

    fun guardar(bandas: Bandas) = repositorio.guardarBanda(bandas)

    fun borrar(id: Int) {
        repositorio.borrarPorId(id)
    }
}