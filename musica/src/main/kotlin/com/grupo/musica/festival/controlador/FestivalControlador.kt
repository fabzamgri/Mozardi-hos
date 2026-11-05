/**
 * @author Carlos Prada Escortell
 * Entidad: Festival
 */
package com.grupo.musica.festival.controlador

import com.grupo.musica.festival.modelo.Festival
import com.grupo.musica.festival.servicio.FestivalServicio
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class FestivalControlador(private val festivalServicio: FestivalServicio) {

    @GetMapping("/")
    fun inicio(): String {
        return "index"
    }

    @GetMapping("/festivales")
    fun listarFestivales(
        @RequestParam("texto", required = false) texto: String?,
        @RequestParam("categoria", required = false) categoria: String?,
        model: Model
    ): String {
        model.addAttribute("festivales", festivalServicio.filtrar(texto, categoria))
        model.addAttribute("categorias", festivalServicio.categorias)
        // Se devuelven para que el buscador mantenga lo escrito
        model.addAttribute("texto", texto ?: "")
        model.addAttribute("categoriaElegida", categoria ?: "")
        return "festivales"
    }

    @GetMapping("/festivales/nuevo")
    fun nuevoFestival(model: Model): String {
        model.addAttribute("festival", Festival())
        prepararFormulario(model, "Nuevo Festival")
        return "formFestival"
    }


    @GetMapping("/festivales/{id}")
    fun verFestival(@PathVariable("id") id: String, model: Model): String {
        val festival = id.toIntOrNull()?.let { festivalServicio.buscar(it) }
            ?: return mostrarError(id, model)
        model.addAttribute("festival", festival)
        return "detalleFestival"
    }

    @GetMapping("/festivales/editar/{id}")
    fun editarFestival(@PathVariable("id") id: String, model: Model): String {
        val festival = id.toIntOrNull()?.let { festivalServicio.buscar(it) }
            ?: return mostrarError(id, model)
        model.addAttribute("festival", festival)
        prepararFormulario(model, "Editar Festival")
        return "formFestival"
    }

    @PostMapping("/festivales/guardar")
    fun guardarFestival(
        @ModelAttribute("festival") festival: Festival,
        resultado: BindingResult,
        model: Model,
        redirect: RedirectAttributes
    ): String {
        val esNuevo = festival.id == 0
        val titulo = if (esNuevo) "Nuevo Festival" else "Editar Festival"

        if (resultado.hasErrors()) {
            prepararFormulario(model, titulo)
            model.addAttribute("error", "Revisa los datos: asistentes y precio deben ser números.")
            return "formFestival"
        }

        val error = festivalServicio.validar(festival)
        if (error != null) {
            prepararFormulario(model, titulo)
            model.addAttribute("error", error)
            return "formFestival"
        }

        if (!esNuevo && festivalServicio.buscar(festival.id) == null) {
            return mostrarError(festival.id.toString(), model)
        }

        festivalServicio.guardar(festival)
        if (esNuevo) {
            redirect.addFlashAttribute("mensaje", "Festival creado correctamente.")
        } else {
            redirect.addFlashAttribute("mensaje", "Festival modificado correctamente.")
        }
        return "redirect:/festivales"
    }

    @GetMapping("/festivales/borrar/{id}")
    fun borrarFestival(@PathVariable("id") id: String, model: Model, redirect: RedirectAttributes): String {
        val idNumero = id.toIntOrNull() ?: return mostrarError(id, model)
        if (!festivalServicio.borrar(idNumero)) {
            return mostrarError(id, model)
        }
        redirect.addFlashAttribute("mensaje", "Festival borrado correctamente.")
        return "redirect:/festivales"
    }

    private fun prepararFormulario(model: Model, titulo: String) {
        model.addAttribute("titulo", titulo)
        model.addAttribute("categorias", festivalServicio.categorias)
    }

    private fun mostrarError(id: String, model: Model): String {
        model.addAttribute("idBuscado", id)
        return "errorFestival"
    }
}