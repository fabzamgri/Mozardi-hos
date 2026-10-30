package com.grupo.musica.repository

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.grupo.musica.model.Instrumento
import org.springframework.stereotype.Repository
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.collections.mutableListOf

/**
 * @author Lucia Troncho Ortí
 * Entidad: Instrumentos
 */

@Repository
class InstrumentoRepository {

    val ruta = Path.of("datos", "Instrumentos.csv")

    private val archivo: File = ruta.toFile()

    fun leerInstrumentos(): MutableList<Instrumento> {

        val instrumentos = mutableListOf<Instrumento>()

        if (!Files.exists(ruta)) {
            return instrumentos
        }

        // Leer el CSV utilizando Kotlin-CSV
        val registros = csvReader {
            delimiter = ';'
        }.readAll(archivo)

        // Convertir las filas del CSV en objetos Instrumento
        instrumentos.addAll(
            registros.mapNotNull { columnas ->

                if (columnas.size >= 6) {

                    try {
                        val id_instrumento = columnas[0].toInt()
                        val nombre = columnas[1]
                        val familia = columnas[2]
                        val invencion = columnas[3].toInt()
                        val peso = columnas[4].toDouble()
                        val imagen = columnas[5]

                        Instrumento(
                            id_instrumento,
                            nombre,
                            familia,
                            invencion,
                            peso,
                            imagen
                        )

                    } catch (e: Exception) {

                        println("Fila inválida ignorada: $columnas -> ${e.message}")
                        null
                    }

                } else {

                    println("Fila incompleta ignorada: $columnas")
                    null
                }
            }
        )
        return instrumentos
    }
    // Buscar instrumento por ID
    fun buscarPorId(id: Int): Instrumento? {

        return leerInstrumentos().find {
            it.id_instrumento == id
        }
    }
    // Guardar o modificar instrumento
    fun guardarInstrumento(instrumento: Instrumento) {
        val instrumentos = leerInstrumentos()
        val index = instrumentos.indexOfFirst {
            it.id_instrumento == instrumento.id_instrumento
        }
        if (index != -1) {
            instrumentos[index] = instrumento
        } else {
            val nuevoId = (instrumentos.maxOfOrNull {
                it.id_instrumento
            } ?: 0) + 1
            instrumentos.add(
                instrumento.copy(id_instrumento = nuevoId)
            )
        }
        escribirInstrumentos(instrumentos)
    }
    // Eliminar instrumento por ID
    fun borrarPorId(id: Int) {
        val instrumentos = leerInstrumentos()
        instrumentos.removeAll {
            it.id_instrumento == id
        }
        escribirInstrumentos(instrumentos)
    }
    // Escribir todos los instrumentos en el CSV
    private fun escribirInstrumentos(instrumentos: List<Instrumento>) {
        val filas = mutableListOf<List<String>>()
        // Cabecera
        filas.add(
            listOf(
                "id_instrumento",
                "nombre",
                "familia",
                "invencion",
                "peso",
                "imagen",
            )
        )
        // Registros
        instrumentos.forEach { instrumento ->
            filas.add(
                listOf(
                    instrumento.id_instrumento.toString(),
                    instrumento.nombre,
                    instrumento.familia,
                    instrumento.invencion.toString(),
                    instrumento.peso.toString(),
                    instrumento.imagen
                )
            )
        }
        // Guardar en el CSV
        csvWriter {
            delimiter = ';'
        }.writeAll(filas, archivo)
    }
}