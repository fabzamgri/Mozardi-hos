/**
 * @author Carlos Prada Escortell
 * Entidad: Festival
 */
package com.grupo.musica.festival.repositorio

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.grupo.musica.festival.modelo.Festival
import org.springframework.stereotype.Repository
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

@Repository
class FestivalRepositorio {

    private val ruta: Path = buscarRutaCSV()

    private fun buscarRutaCSV(): Path {
        val relativa = "src/main/resources/data/festivales.csv"
        try {
            val clases = File(FestivalRepositorio::class.java.protectionDomain.codeSource.location.toURI())
            val proyecto = clases.parentFile.parentFile
            val ruta = proyecto.toPath().resolve(relativa)
            if (Files.exists(ruta.parent)) return ruta
        } catch (e: Exception) {
            println("No se pudo calcular la carpeta del proyecto, se usa la ruta relativa.")
        }
        return Path.of(relativa)
    }

    private fun leerCSV(): MutableList<Festival> {
        val festivales = mutableListOf<Festival>()

        if (!Files.exists(ruta)) {
            println("El fichero CSV no existe en la ruta: ${ruta.toAbsolutePath()}")
            return festivales
        }

        val archivo: File = ruta.toFile()
        val filas = csvReader { delimiter = ';' }.readAll(archivo)

        for (fila in filas) {
            val id = fila.getOrNull(0)?.trim()?.toIntOrNull()
            if (id != null && fila.size < 8) {
                println("El festival con ID $id tiene ${fila.size} columnas y se esperaban 8 separadas por ';'")
            }
            if (id != null && fila.size >= 8) {
                try {
                    festivales.add(
                        Festival(
                            id,
                            fila[1].trim(),
                            fila[2].trim(),
                            fila[3].trim().toInt(),
                            fila[4].trim().replace(',', '.').toDouble(),
                            fila[5].trim(),
                            fila[6].trim(),
                            fila[7].trim()
                        )
                    )
                } catch (e: Exception) {
                    println("Error al leer el festival con ID $id del CSV")
                }
            }
        }
        return festivales
    }

    private fun escribirCSV(festivales: List<Festival>) {
        Files.createDirectories(ruta.parent)

        val filas = mutableListOf<List<String>>()
        filas.add(listOf("id", "nombre", "ciudad", "asistentes", "precioAbono", "categoria", "descripcion", "foto"))
        for (f in festivales) {
            filas.add(
                listOf(
                    f.id.toString(),
                    f.nombre,
                    f.ciudad,
                    f.asistentes.toString(),
                    f.precioAbono.toString(),
                    f.categoria,
                    f.descripcion,
                    f.foto
                )
            )
        }

        csvWriter { delimiter = ';' }.writeAll(filas, ruta.toFile())
    }

    fun leerTodosFestivales(): MutableList<Festival> = leerCSV()

    fun buscarPorId(id: Int): Festival? = leerCSV().find { it.id == id }

    fun guardarFestival(festival: Festival) {
        val festivales = leerCSV()
        val indice = festivales.indexOfFirst { it.id == festival.id }

        if (indice != -1) {
            festivales[indice] = festival
        } else {
            val nuevoId = (festivales.maxOfOrNull { it.id } ?: 0) + 1
            festivales.add(festival.copy(id = nuevoId))
        }
        escribirCSV(festivales)
    }

    fun borrarPorId(id: Int): Boolean {
        val festivales = leerCSV()
        val borrado = festivales.removeAll { it.id == id }
        if (borrado) {
            escribirCSV(festivales)
        }
        return borrado
    }
}