package com.grupo.musica.instrumentos.controller

import com.grupo.musica.instrumentos.model.Instrumento
import com.grupo.musica.instrumentos.service.InstrumentoService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

/**
 * @author Lucia Troncho Ortí
 * Entidad: Instrumentos
 */

@Controller
class InstrumentoController(private val instrumentoService: InstrumentoService) {

    // 1. Listar instrumentos
    @GetMapping("/instrumentos")
    fun listarInstrumentos(model: Model): String {

        model.addAttribute(
            "instrumentos",
            instrumentoService.listar()
        )

        return "listaInstrumentos"
    }

    // 2. Ver detalle de un instrumento
    // La expresión regular \\d+ evita que "nuevo" se confunda con un id
    @GetMapping("/instrumentos/{id_instrumento:\\d+}")
    fun verInstrumento(
        @PathVariable("id_instrumento") id: Int,
        model: Model
    ): String {

        val instrumento = instrumentoService.buscar(id)
            ?: return "errorInstrumentos"

        model.addAttribute("instrumento", instrumento)

        return "detallesInstrumentos"
    }

    // 3. Crear un instrumento nuevo
    @GetMapping("/instrumentos/nuevo")
    fun nuevoInstrumento(model: Model): String {

        val instrumentoVacio = Instrumento(0, "", "", 0, 0.0, "")

        model.addAttribute("instrumento", instrumentoVacio)
        model.addAttribute("titulo", "Nuevo instrumento")

        return "formularioInstrumentos"
    }

    // 4. Editar un instrumento
    @GetMapping("/instrumentos/editar/{id_instrumento:\\d+}")
    fun editarInstrumento(
        @PathVariable("id_instrumento") id: Int,
        model: Model
    ): String {

        val instrumento = instrumentoService.buscar(id)
            ?: return "errorInstrumentos"

        model.addAttribute("instrumento", instrumento)
        model.addAttribute("titulo", "Editar instrumento")

        return "formularioInstrumentos"
    }

    // 5. Guardar instrumento
    @PostMapping("/instrumentos/guardar")
    fun guardarInstrumento(
        @ModelAttribute instrumento: Instrumento
    ): String {

        instrumentoService.guardar(instrumento)

        return "redirect:/instrumentos"
    }

    // 6. Borrar instrumento
    @PostMapping("/instrumentos/eliminar/{id_instrumento:\\d+}")
    fun borrarInstrumento(
        @PathVariable("id_instrumento") id: Int
    ): String {

        instrumentoService.borrar(id)

        return "redirect:/instrumentos"
    }
}
