/**
 * @author Carlos Prada Escortell
 * Entidad: Festival
 */
package com.grupo.musica.festival.modelo

// Todos los atributos tienen valor por defecto para que Spring pueda
// crear un Festival vacío al rellenar el formulario (th:object / th:field)
data class Festival(
    var id: Int = 0,
    var nombre: String = "",
    var ciudad: String = "",
    var asistentes: Int = 0,
    var precioAbono: Double = 0.0,
    var categoria: String = "",
    var descripcion: String = "",
    var foto: String = ""          // Nombre del archivo en static/img/festivales/
)