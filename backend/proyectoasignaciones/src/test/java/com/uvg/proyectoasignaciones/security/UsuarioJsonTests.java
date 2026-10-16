
package com.uvg.proyectoasignaciones.security;

import com.uvg.proyectoasignaciones.model.Estudiante;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioJsonTests {

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    @Test
    void noExponeContrasenaEnJson() throws Exception {

        Estudiante estudiante = new Estudiante();

        estudiante.setIdUsuario(1);
        estudiante.setNombre("Estudiante de prueba");
        estudiante.setCorreo("prueba@uvg.edu.gt");
        estudiante.setContrasena("$2a$10$HASH_DE_PRUEBA");

        String json =
                objectMapper.writeValueAsString(estudiante);

        assertFalse(
                json.contains("\"contrasena\""),
                "El JSON no debe incluir el campo contrasena"
        );

        assertFalse(
                json.contains("$2a$10$HASH_DE_PRUEBA"),
                "El JSON no debe exponer el hash BCrypt"
        );
    }
}

