/**
 * @author Lucas Palermo Andreu
 * Entidad: Autores Clásicos
 */
package com.grupo.musica.autoresclasicos.controller

import com.grupo.musica.autoresclasicos.model.AutoresClasicos
import com.grupo.musica.autoresclasicos.service.AutoresClasicosService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class AutoresClasicosController(private val autoresService: AutoresClasicosService) {

    // 1. LISTAR (con buscador y filtro por siglo)
    @GetMapping("/autoresclasicos")
    fun listarAutores(
        @RequestParam("texto", required = false) texto: String?,
        @RequestParam("siglo", required = false) siglo: String?,
        model: Model
    ): String {
        model.addAttribute("autores", autoresService.filtrar(texto, siglo))
        model.addAttribute("siglos", autoresService.siglos())
        // Se devuelven para que el buscador mantenga lo escrito
        model.addAttribute("texto", texto ?: "")
        model.addAttribute("sigloElegido", siglo ?: "")
        return "autoresClasicos"
    }

    // 2. FORMULARIO NUEVO AUTOR (objeto vacío con ID 0)
    @GetMapping("/autoresclasicos/nuevo")
    fun nuevoAutor(model: Model): String {
        model.addAttribute("autor", AutoresClasicos())
        model.addAttribute("titulo", "Nuevo autor")
        return "formAutor"
    }

    // 3. DETALLE
    // El id llega como String para que si alguien escribe /autoresclasicos/abc no pete el programa
    @GetMapping("/autoresclasicos/{id}")
    fun verAutor(@PathVariable("id") id: String, model: Model): String {
        val autor = id.toIntOrNull()?.let { autoresService.obtenerPorId(it) }
            ?: return mostrarError(id, model)
        model.addAttribute("autor", autor)
        return "detalleAutor"
    }

    // 4. FORMULARIO EDITAR AUTOR
    @GetMapping("/autoresclasicos/editar/{id}")
    fun editarAutor(@PathVariable("id") id: String, model: Model): String {
        val autor = id.toIntOrNull()?.let { autoresService.obtenerPorId(it) }
            ?: return mostrarError(id, model)
        model.addAttribute("autor", autor)
        model.addAttribute("titulo", "Editar autor")
        return "formAutor"
    }

    // 5. GUARDAR (sirve para crear y para editar)
    @PostMapping("/autoresclasicos/guardar")
    fun guardarAutor(
        @ModelAttribute("autor") autor: AutoresClasicos,
        resultado: BindingResult,
        model: Model,
        redirect: RedirectAttributes
    ): String {
        val esNuevo = autor.id == 0
        val titulo = if (esNuevo) "Nuevo autor" else "Editar autor"

        // Errores de tipo
        if (resultado.hasErrors()) {
            model.addAttribute("titulo", titulo)
            model.addAttribute("error", "Revisa los datos del formulario.")
            return "formAutor"
        }

        // Errores de contenido (campos vacíos)
        val error = autoresService.validar(autor)
        if (error != null) {
            model.addAttribute("titulo", titulo)
            model.addAttribute("error", error)
            return "formAutor"
        }

        // Si es una edición, comprobamos que el autor siga existiendo
        if (!esNuevo && autoresService.obtenerPorId(autor.id) == null) {
            return mostrarError(autor.id.toString(), model)
        }

        autoresService.guardar(autor)
        if (esNuevo) {
            redirect.addFlashAttribute("mensaje", "Autor creado correctamente.")
        } else {
            redirect.addFlashAttribute("mensaje", "Autor modificado correctamente.")
        }
        return "redirect:/autoresclasicos"
    }

    // 6. BORRAR (la confirmación se hace con confirm() en la plantilla)
    @GetMapping("/autoresclasicos/borrar/{id}")
    fun borrarAutor(@PathVariable("id") id: String, model: Model, redirect: RedirectAttributes): String {
        val idNumero = id.toIntOrNull() ?: return mostrarError(id, model)
        if (autoresService.obtenerPorId(idNumero) == null) {
            return mostrarError(id, model)
        }
        autoresService.eliminar(idNumero)
        redirect.addFlashAttribute("mensaje", "Autor borrado correctamente.")
        return "redirect:/autoresclasicos"
    }

    // 7. RESTAURAR los autores iniciales
    @GetMapping("/autoresclasicos/resetear")
    fun resetearAutores(redirect: RedirectAttributes): String {
        autoresService.resetear()
        redirect.addFlashAttribute("mensaje", "Se han restaurado los autores iniciales.")
        return "redirect:/autoresclasicos"
    }

    // Página de error de registro no encontrado
    private fun mostrarError(id: String, model: Model): String {
        model.addAttribute("idBuscado", id)
        return "errorAutor"
    }
}
