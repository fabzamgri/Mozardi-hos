package com.grupo.musica.instrumentos.repository

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.grupo.musica.instrumentos.model.Instrumento
import org.springframework.core.io.ClassPathResource
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

    private val ruta: Path = Path.of("src", "main", "resources", "data", "Instrumentos.csv")

    private val archivo: File = ruta.toFile()

    // Nombre de la primera columna de la cabecera (se usa para ignorarla al leer)
    private val cabeceraId = "id_instrumento"

    @Synchronized
    fun leerInstrumentos(): MutableList<Instrumento> {

        val instrumentos = mutableListOf<Instrumento>()

        val lector = csvReader {
            delimiter = ';'
        }

        // Leer el CSV utilizando Kotlin-CSV
        val registros: List<List<String>> = if (Files.exists(ruta)) {
            lector.readAll(archivo)
        } else {
            // Si no existe el fichero en disco, se usa la copia del classpath (resources/data)
            val recurso = ClassPathResource("data/Instrumentos.csv")
            if (recurso.exists()) {
                println("Usando CSV del classpath. No se encontró: ${ruta.toAbsolutePath()}")
                recurso.inputStream.use { lector.readAll(it) }
            } else {
                println("No se encontró el CSV ni en ${ruta.toAbsolutePath()} ni en el classpath")
                return instrumentos
            }
        }

        // Convertir las filas del CSV en objetos Instrumento
        instrumentos.addAll(
            registros
                .filterNot { columnas ->
                    columnas.isEmpty() ||
                            columnas.all { it.isBlank() } ||
                            columnas[0].trim().equals(cabeceraId, ignoreCase = true)
                }
                .mapNotNull { columnas ->

                    if (columnas.size >= 6) {

                        try {
                            val id_instrumento = columnas[0].trim().toInt()
                            val nombre = columnas[1].trim()
                            val familia = columnas[2].trim()
                            val invencion = columnas[3].trim().toInt()
                            // Admite tanto punto como coma decimal
                            val peso = columnas[4].trim().replace(',', '.').toDouble()
                            val imagen = columnas[5].trim()

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
    @Synchronized
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
    @Synchronized
    fun borrarPorId(id: Int) {

        val instrumentos = leerInstrumentos()

        instrumentos.removeAll {
            it.id_instrumento == id
        }

        escribirInstrumentos(instrumentos)
    }

    // Escribir todos los instrumentos en el CSV
    private fun escribirInstrumentos(instrumentos: List<Instrumento>) {

        // Crear la carpeta de datos si no existe
        ruta.parent?.let { Files.createDirectories(it) }

        val filas = mutableListOf<List<String>>()

        filas.add(
            listOf(
                cabeceraId,
                "nombre",
                "familia",
                "invencion",
                "peso",
                "imagen"
            )
        )

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
