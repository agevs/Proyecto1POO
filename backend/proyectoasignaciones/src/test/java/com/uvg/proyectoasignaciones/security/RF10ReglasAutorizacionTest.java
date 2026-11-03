package com.uvg.proyectoasignaciones.security;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.uvg.proyectoasignaciones.security.ControlAccesoRol.Permiso;
class RF10ReglasAutorizacionTest {
    @Test void reconoceRegistroYListado() {
        assertEquals(Permiso.REGISTRAR_DOCENTE, ReglasAutorizacion.obtenerPermiso("POST", "/api/administracion/docentes"));
        assertEquals(Permiso.REGISTRAR_DOCENTE, ReglasAutorizacion.obtenerPermiso("GET", "/api/administracion/docentes"));
        assertNull(ReglasAutorizacion.obtenerPermiso("DELETE", "/api/administracion/docentes"));
    }
    @Test void reconoceConfirmacionYRechazaRutaInvalida() {
        assertEquals(Permiso.REGISTRAR_DOCENTE, ReglasAutorizacion.obtenerPermiso("PATCH", "/api/administracion/cursos/CC2008/secciones/10/docente"));
        assertNull(ReglasAutorizacion.obtenerPermiso("GET", "/api/administracion/cursos/CC2008/secciones/10/docente"));
        assertNull(ReglasAutorizacion.obtenerPermiso("PATCH", "/api/administracion/cursos/CC2008/secciones/abc/docente"));
    }
}
