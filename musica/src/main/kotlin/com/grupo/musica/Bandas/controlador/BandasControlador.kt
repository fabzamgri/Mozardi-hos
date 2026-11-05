/**
 * @author Fabio Zamudio Grimaldo
 * Entidad: BandasControlador
 */

package com.grupo.musica.Bandas.controlador

import com.grupo.musica.Bandas.modelo.Bandas
import com.grupo.musica.Bandas.servicio.BandasServicio
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller // ¡Importante! Faltaba esta anotación para que Spring reconozca el controlador
class BandasControlador(private val bandasServicio: BandasServicio) {

    @GetMapping("/bandas")
    fun listarBandas(model: Model): String {
        model.addAttribute("bandas", bandasServicio.listar())
        return "bandas"
    }

    @GetMapping("/bandas/{id}")
    fun verBanda(@PathVariable id: Int, model: Model): String {
        val banda = bandasServicio.buscar(id) ?: return "ErrorBanda"
        model.addAttribute("banda", banda)
        return "detalleBanda"
    }

    @GetMapping("/bandas/nueva")
    fun nuevoBanda(model: Model): String {
        val bandaVacia = Bandas(0, "", "", 0, null)
        model.addAttribute("banda", bandaVacia)
        model.addAttribute("titulo", "Nueva Banda")
        return "formBanda"
    }

    @GetMapping("/bandas/editar/{id}") // Cambiado {bandaval} por {id} para que coincida con el parámetro
    fun editarBanda(@PathVariable id: Int, model: Model): String {
        val banda = bandasServicio.buscar(id) ?: return "redirect:/bandas"
        model.addAttribute("banda", banda)
        model.addAttribute("titulo", "Editar Banda")
        return "formBanda" // Corregido a "formBanda" (sin la 's' final para que coincida con el template)
    }

    @PostMapping("/bandas/guardar")
    fun guardarBanda(@ModelAttribute banda: Bandas): String { // Cambiado @PathVariable por @ModelAttribute
        bandasServicio.guardar(banda)
        return "redirect:/bandas"
    }

    @GetMapping("/bandas/borrar/{id}")
    fun borrarBanda(@PathVariable id: Int): String {
        bandasServicio.borrar(id)
        return "redirect:/bandas"
    }
}