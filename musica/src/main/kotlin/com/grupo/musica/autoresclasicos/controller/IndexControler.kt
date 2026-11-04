package com.grupo.musica.autoresclasicos.controller
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

/**
 * @author Lucas Palermo Andreu
 * Entidad: Autores Clásicos
 */
@Controller
class IndexController {

    @GetMapping("/")
    fun index(): String {
        return "index"
    }
}