package com.uvg.proyectoasignaciones.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.uvg.proyectoasignaciones.model.Administrador;
import com.uvg.proyectoasignaciones.model.Estudiante;

class DecisionAutorizacionTest {

    private static final String RUTA_ESTADO =
        "/api/administracion/cursos/CC2008/secciones/10/estado";

    private final Estudiante estudiante = new Estudiante(
        1, "Estudiante Prueba", "estudiante@uvg.edu.gt",
        "clavePrueba", "26594"
    );

    private final Administrador administrador = new Administrador(
        2, "Administrador Prueba", "admin@uvg.edu.gt",
        "clavePrueba", "Coordinacion"
    );

    @Test
    void administradorPuedeCambiarEstado() {
        assertTrue(
            DecisionAutorizacion.puedeEjecutar(
                administrador, "PATCH", RUTA_ESTADO
            )
        );
    }

    @Test
    void estudianteNoPuedeCambiarEstado() {
        assertFalse(
            DecisionAutorizacion.puedeEjecutar(
                estudiante, "PATCH", RUTA_ESTADO
            )
        );
    }

    @Test
    void usuarioSinAutenticarEsDenegado() {
        assertFalse(
            DecisionAutorizacion.puedeEjecutar(
                null, "PATCH", RUTA_ESTADO
            )
        );
    }

    @Test
    void rutaAdministrativaDesconocidaEsDenegada() {
        assertFalse(
            DecisionAutorizacion.puedeEjecutar(
                administrador,
                "DELETE",
                "/api/administracion/usuarios/1"
            )
        );
    }

    @Test
    void metodoIncorrectoEsDenegado() {
        assertFalse(
            DecisionAutorizacion.puedeEjecutar(
                administrador, "GET", RUTA_ESTADO
            )
        );
    }

    @Test
    void rutaNoAdministrativaEsDenegada() {
        assertFalse(
            DecisionAutorizacion.puedeEjecutar(
                administrador, "PATCH", "/api/cursos"
            )
        );
    }

    @Test
    void valoresNulosSonDenegados() {
        assertFalse(
            DecisionAutorizacion.puedeEjecutar(
                administrador, null, RUTA_ESTADO
            )
        );

        assertFalse(
            DecisionAutorizacion.puedeEjecutar(
                administrador, "PATCH", null
            )
        );
    }
}