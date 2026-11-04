/**
 * @author Fabio Zamudio Grimaldo
 * Entidad: BandasRepositorio
 */

package com.grupo.musica.Bandas.repositorio

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.grupo.musica.Bandas.modelo.Bandas
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Repository
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

@Repository
class BandasRepositorio {

    // Obtenemos el archivo de forma segura usando Spring y lo convertimos a Path y File
    private fun obtenerArchivoYRuta(): Pair<File, Path> {
        val resource = ClassPathResource("data/bandas.csv")
        val archivo = resource.file
        val ruta: Path = archivo.toPath()
        return Pair(archivo, ruta)
    }

    fun leerTodasBandas(): MutableList<Bandas> {
        val listaBandas = mutableListOf<Bandas>()

        val (archivo, ruta) = obtenerArchivoYRuta()

        // Usamos java.nio.file.Files para verificar si existe
        if (!Files.exists(ruta)) return listaBandas

        // Usamos Kotlin-CSV para leer el archivo
        csvReader {
            delimiter = ';'
        }.open(archivo) {
            readAllAsSequence().drop(1).forEach { row ->
                if (row.isNotEmpty() && row.size >= 5) {
                    val banda = Bandas(
                        id = row[0].trim().toInt(),
                        nombre = row[1].trim(),
                        genero = row[2].trim(),
                        ano_fundacion = row[3].trim().toInt(),
                        ventas_millones = row[4].trim().toDoubleOrNull()
                    )
                    listaBandas.add(banda)
                }
            }
        }
        return listaBandas
    }

    private fun escribirCSV(lista: List<Bandas>) {
        val (archivo, ruta) = obtenerArchivoYRuta()

        val filas = mutableListOf<List<String>>()
        filas.add(listOf("id", "nombre", "genero", "ano_fundacion", "ventas_millones"))

        for (b in lista) {
            val ventasStr = b.ventas_millones?.toString() ?: ""
            filas.add(listOf(
                b.id.toString(),
                b.nombre,
                b.genero,
                b.ano_fundacion.toString(),
                ventasStr
            ))
        }

        // Usamos java.nio.file.Files por si hay que asegurar el directorio
        Files.createDirectories(ruta.parent)

        // Usamos Kotlin-CSV para escribir
        csvWriter {
            delimiter = ';'
        }.writeAll(filas, archivo)
    }

    fun buscarPorID(id: Int): Bandas? {
        return leerTodasBandas().find { it.id == id }
    }

    fun guardarBanda(banda: Bandas) {
        val bandas = leerTodasBandas()
        val index = bandas.indexOfFirst { it.id == banda.id }

        if (index != -1) {
            bandas[index] = banda
        } else {
            val nuevoId = (bandas.maxOfOrNull { it.id } ?: 0) + 1
            bandas.add(banda.copy(id = nuevoId))
        }
        escribirCSV(bandas)
    }

    fun borrarPorId(id: Int) {
        val bandas = leerTodasBandas()
        bandas.removeAll { it.id == id }
        escribirCSV(bandas)
    }
}