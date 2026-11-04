package com.grupo.musica.autoresclasicos.controller

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.file.Path

/**
 * @author Lucas Palermo Andreu
 * Configuración para servir imágenes subidas por los usuarios en tiempo real
 */
@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // Mapea la URL /uploads/** a una carpeta física llamada "uploads" en la raíz de tu proyecto
        val rutaUploads = Path.of("uploads").toAbsolutePath().toUri().toString()
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations(rutaUploads)
    }
}