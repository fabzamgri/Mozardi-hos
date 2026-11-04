package com.grupo.musica.autoresclasicos.repositories
import AutoresClasicos
import java.nio.file.Files
import java.nio.file.Path
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import org.springframework.stereotype.Repository
import jakarta.annotation.PostConstruct
/**
 * @author Lucas Palermo Andreu
 * Entidad: Autores Clásicos
 */
@Repository
class AutoresClasicosRepositories {

    private val archivoPath: Path = Path.of("src", "main", "resources", "data", "autores_clasicos.csv")

    @PostConstruct
    fun inicializar() {
        if (archivoPath.parent != null && !Files.exists(archivoPath.parent)) {
            Files.createDirectories(archivoPath.parent)
        }
        if (!Files.exists(archivoPath) || Files.size(archivoPath) == 0L) {
            val autoresIniciales = listOf(
                AutoresClasicos(1, "Wolfgang Amadeus Mozart", "18", "27 de enero de 1756", "Destacan óperas como Las bodas de Fígaro, Don Giovanni y La flauta mágica, además de su Misa de Réquiem.", "https://upload.wikimedia.org/wikipedia/commons/1/1e/Wolfgang-amadeus-mozart_1.jpg"),
                AutoresClasicos(2, "Ludwig van Beethoven", "18-19", "16 de diciembre de 1770", "Destacan la Quinta Sinfonía y la Novena Sinfonía y la sonata Claro de luna.", "https://upload.wikimedia.org/wikipedia/commons/6/6f/Beethoven.jpg"),
                AutoresClasicos(3, "Johann Sebastian Bach", "17-18", "1685", "El clave bien temperado, los Conciertos de Brandeburgo y la Pasión según San Mateo.", "https://upload.wikimedia.org/wikipedia/commons/6/6a/Johann_Sebastian_Bach.jpg"),
                AutoresClasicos(4, "Frédéric Chopin", "19", "1 de marzo de 1810", "Nocturnos, los Preludios (op. 28), los Estudios (como el Revolucionario) y sus dos conciertos para piano.", "https://upload.wikimedia.org/wikipedia/commons/e/e8/Frederic_Chopin_photo.jpeg"),
                AutoresClasicos(5, "Antonio Vivaldi", "17-18", "4 de marzo de 1678", "Las cuatro estaciones, Gloria en re mayor (RV 589), L'estro armonico, Il cimento dell'armonia e dell'inventione, Stabat Mater (RV 621)", "https://upload.wikimedia.org/wikipedia/commons/a/a8/Antonio_Vivaldi.jpg")
            )
            guardarTodos(autoresIniciales)
        }
    }
    fun guardarTodos(autores: List<AutoresClasicos>) {
        val filas = autores.map { listOf(it.id, it.nombre, it.siglo, it.fechaNacimiento, it.obrasDestacadas, it.imagen) }
        val cabecera = listOf("id", "nombre", "siglo", "fecha_nacimiento", "obras_destacadas", "imagen")

        csvWriter {
            delimiter = ';'
        }.writeAll(listOf(cabecera) + filas, archivoPath.toFile())
    }
    fun leerTodos(): List<AutoresClasicos> {
        if (!Files.exists(archivoPath) || Files.size(archivoPath) == 0L) return emptyList()

        return try {
            csvReader {
                delimiter = ';'
            }.readAllWithHeader(archivoPath.toFile()).map { fila ->
                AutoresClasicos(
                    id = fila["id"]?.toIntOrNull() ?: 0, // Usamos toIntOrNull por si hay espacios
                    nombre = fila["nombre"] ?: "",
                    siglo = fila["siglo"] ?: "",
                    fechaNacimiento = fila["fecha_nacimiento"] ?: "",
                    obrasDestacadas = fila["obras_destacadas"] ?: "",
                    imagen = fila["imagen"] ?: ""
                )
            }
        } catch (e: Exception) {
            println("\n ERROR GRAVE LEYENDO EL CSV: ${e.message}\n")
            e.printStackTrace()
            emptyList() // Si hay error, devuelve la lista vacía pero no rompe la web
        }
    }
    fun resetearDatos() {
        val autoresIniciales = listOf(
            AutoresClasicos(1, "Wolfgang Amadeus Mozart", "18", "27 de enero de 1756", "Destacan óperas como Las bodas de Fígaro, Don Giovanni y La flauta mágica, además de su Misa de Réquiem.", "https://upload.wikimedia.org/wikipedia/commons/1/1e/Wolfgang-amadeus-mozart_1.jpg"),
            AutoresClasicos(2, "Ludwig van Beethoven", "18-19", "16 de diciembre de 1770", "Destacan la Quinta Sinfonía y la Novena Sinfonía y la sonata Claro de luna.", "https://upload.wikimedia.org/wikipedia/commons/6/6f/Beethoven.jpg"),
            AutoresClasicos(3, "Johann Sebastian Bach", "17-18", "1685", "El clave bien temperado, los Conciertos de Brandeburgo y la Pasión según San Mateo.", "https://upload.wikimedia.org/wikipedia/commons/6/6a/Johann_Sebastian_Bach.jpg"),
            AutoresClasicos(4, "Frédéric Chopin", "19", "1 de marzo de 1810", "Nocturnos, los Preludios (op. 28), los Estudios (como el Revolucionario) y sus dos conciertos para piano.", "https://upload.wikimedia.org/wikipedia/commons/e/e8/Frederic_Chopin_photo.jpeg"),
            AutoresClasicos(5, "Antonio Vivaldi", "17-18", "4 de marzo de 1678", "Las cuatro estaciones, Gloria en re mayor (RV 589), L'estro armonico, Il cimento dell'armonia e dell'inventione, Stabat Mater (RV 621)", "https://upload.wikimedia.org/wikipedia/commons/a/a8/Antonio_Vivaldi.jpg")
        )
        guardarTodos(autoresIniciales)
    }
}