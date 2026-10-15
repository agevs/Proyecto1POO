
package com.uvg.proyectoasignaciones.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.uvg.proyectoasignaciones.model.Administrador;
import com.uvg.proyectoasignaciones.model.Estudiante;

class ControlAccesoRolTest {

    private final Estudiante estudiante = new Estudiante(
        1, "Estudiante Prueba", "estudiante@uvg.edu.gt",
        "clavePrueba", "26594"
    );

    private final Administrador administrador = new Administrador(
        2, "Administrador Prueba", "admin@uvg.edu.gt",
        "clavePrueba", "Coordinacion"
    );

    @Test
    void identificarRolEstudiante() {
        assertEquals(
            ControlAccesoRol.Rol.ESTUDIANTE,
            ControlAccesoRol.obtenerRol(estudiante)
        );
    }

    @Test
    void identificarRolAdministrador() {
        assertEquals(
            ControlAccesoRol.Rol.ADMINISTRADOR,
            ControlAccesoRol.obtenerRol(administrador)
        );
    }

    @Test
    void estudiantePuedeConsultarCursos() {
        assertTrue(ControlAccesoRol.tienePermiso(
            estudiante,
            ControlAccesoRol.Permiso.CONSULTAR_CURSOS
        ));
    }

    @Test
    void estudianteNoPuedeGestionarEstado() {
        assertFalse(ControlAccesoRol.tienePermiso(
            estudiante,
            ControlAccesoRol.Permiso.GESTIONAR_ESTADO
        ));
    }

    @Test
    void estudianteNoPuedeRegistrarDocente() {
        assertFalse(ControlAccesoRol.tienePermiso(
            estudiante,
            ControlAccesoRol.Permiso.REGISTRAR_DOCENTE
        ));
    }

    @Test
    void administradorPuedeGestionarEstado() {
        assertTrue(ControlAccesoRol.tienePermiso(
            administrador,
            ControlAccesoRol.Permiso.GESTIONAR_ESTADO
        ));
    }

    @Test
    void usuarioSinAutenticarNoTienePermisos() {
        assertFalse(ControlAccesoRol.tienePermiso(
            null,
            ControlAccesoRol.Permiso.GESTIONAR_ESTADO
        ));
    }

    @Test
    void identificarUsuarioSinAutenticar() {
        assertEquals(
            ControlAccesoRol.Rol.SIN_AUTENTICAR,
            ControlAccesoRol.obtenerRol(null)
        );
    }

    
    // RF-12: pruebas adicionales de denegacion de acceso

    @Test
    void estudianteNoPuedeGestionarCursosYSecciones() {
        assertFalse(ControlAccesoRol.tienePermiso(
            estudiante,
            ControlAccesoRol.Permiso.GESTIONAR_CURSOS_SECCIONES
        ));
    }

    @Test
    void administradorPuedeRegistrarDocente() {
        assertTrue(ControlAccesoRol.tienePermiso(
            administrador,
            ControlAccesoRol.Permiso.REGISTRAR_DOCENTE
        ));
    }

    @Test
    void usuarioSinAutenticarNoPuedeGestionarCursos() {
        assertFalse(ControlAccesoRol.tienePermiso(
            null,
            ControlAccesoRol.Permiso.GESTIONAR_CURSOS_SECCIONES
        ));
    }

    @Test
    void permisoNuloDebeSerDenegado() {
        assertFalse(ControlAccesoRol.tienePermiso(
            administrador,
            null
        ));
    }

    @Test
    void estudiantePuedeConsultarNotificaciones() {
        assertTrue(ControlAccesoRol.tienePermiso(
            estudiante,
            ControlAccesoRol.Permiso.CONSULTAR_NOTIFICACIONES
        ));
    }
}
