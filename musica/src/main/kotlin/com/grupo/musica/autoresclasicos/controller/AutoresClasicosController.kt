package com.grupo.musica.autoresclasicos.controller
import AutoresClasicos
import com.grupo.musica.autoresclasicos.service.AutoresClasicosService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

/**
 * @author Lucas Palermo Andreu
 * Entidad: Autores Clásicos
 */
@Controller
@RequestMapping("/autores")
class AutoresClasicosController(private val service: AutoresClasicosService) {

    @GetMapping
    fun listar(model: Model): String {
        model.addAttribute("autores", service.obtenerTodos())
        return "autores/listado"
    }

    @GetMapping("/detalle/{id}")
    fun verDetalle(@PathVariable id: Int, model: Model): String {
        val autor = service.obtenerPorId(id)
        return if (autor != null) {
            model.addAttribute("autor", autor)
            "autores/detalle"
        } else {
            "autores/error"
        }
    }

    @GetMapping("/formulario")
    fun mostrarFormularioNuevo(model: Model): String {
        model.addAttribute("autor", AutoresClasicos())
        return "autores/formulario"
    }

    @GetMapping("/formulario/{id}")
    fun mostrarFormularioEdicion(@PathVariable id: Int, model: Model): String {
        val autor = service.obtenerPorId(id)
        return if (autor != null) {
            model.addAttribute("autor", autor)
            "autores/formulario"
        } else {
            "autores/error"
        }
    }

    // --- ESTE ES EL MÉTODO MODIFICADO ---
    @PostMapping("/guardar")
    fun guardarAutor(
        @ModelAttribute autor: AutoresClasicos,
        @RequestParam("archivoImagen", required = false) archivo: MultipartFile?
    ): String {

        // 1. Si estamos editando y el usuario NO sube foto nueva, mantenemos la foto que ya tenía.
        if (autor.id != 0) {
            val autorExistente = service.obtenerPorId(autor.id)
            if (autorExistente != null && (archivo == null || archivo.isEmpty)) {
                autor.imagen = autorExistente.imagen
            }
        }

        // 2. Si el usuario sube un archivo nuevo...
        if (archivo != null && !archivo.isEmpty) {
            // Cogemos el nombre original del archivo (o generamos uno si no tiene)
            val nombreArchivo = archivo.originalFilename ?: "imagen_${System.currentTimeMillis()}.jpg"

            // Creamos la carpeta física 'uploads' en la raíz del proyecto si no existe
            val rutaDirectorio = Path.of("uploads")
            if (!Files.exists(rutaDirectorio)) {
                Files.createDirectories(rutaDirectorio)
            }

            // Copiamos el archivo del formulario a nuestra carpeta
            val rutaDestino = rutaDirectorio.resolve(nombreArchivo)
            Files.copy(archivo.inputStream, rutaDestino, StandardCopyOption.REPLACE_EXISTING)

            // Guardamos la ruta web en nuestro objeto Autor para el CSV
            autor.imagen = "/uploads/" + nombreArchivo
        }

        service.guardar(autor)
        return "redirect:/autores"
    }

    @GetMapping("/eliminar/{id}")
    fun eliminarAutor(@PathVariable id: Int): String {
        service.eliminar(id)
        return "redirect:/autores"
    }
    @GetMapping("/reset")
    fun resetear(): String {
        service.resetear()
        return "redirect:/autores"
    }
}