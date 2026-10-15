package com.uvg.proyectoasignaciones.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.uvg.proyectoasignaciones.security.ControlAccesoRol.Permiso;

class ReglasAutorizacionTest {

    private static final String RUTA_ESTADO =
        "/api/administracion/cursos/CC2008/secciones/10/estado";

    @Test
    void cambiarEstadoRequierePermisoAdministrativo() {
        assertEquals(
            Permiso.GESTIONAR_ESTADO,
            ReglasAutorizacion.obtenerPermiso(
                "PATCH", RUTA_ESTADO
            )
        );
    }

    @Test
    void metodoHttpIncorrectoNoCoincide() {
        assertNull(
            ReglasAutorizacion.obtenerPermiso(
                "GET", RUTA_ESTADO
            )
        );
    }

    @Test
    void rutaDeCursosNoEsAdministrativa() {
        assertFalse(
            ReglasAutorizacion.esRutaAdministrativa(
                "/api/cursos"
            )
        );
    }

    @Test
    void rutaAdministrativaSeReconoce() {
        assertTrue(
            ReglasAutorizacion.esRutaAdministrativa(
                RUTA_ESTADO
            )
        );
    }

    @Test
    void rutaAdministrativaDesconocidaNoTieneRegla() {
        assertNull(
            ReglasAutorizacion.obtenerPermiso(
                "DELETE",
                "/api/administracion/usuarios/1"
            )
        );
    }

    @Test
    void numeroSeccionInvalidoNoCoincide() {
        assertNull(
            ReglasAutorizacion.obtenerPermiso(
                "PATCH",
                "/api/administracion/cursos/CC2008/secciones/abc/estado"
            )
        );
    }

    @Test
    void rutaIncompletaNoCoincide() {
        assertNull(
            ReglasAutorizacion.obtenerPermiso(
                "PATCH",
                "/api/administracion/cursos/CC2008"
            )
        );
    }

    @Test
    void valoresNulosNoProducenErrores() {
        assertNull(
            ReglasAutorizacion.obtenerPermiso(null, null)
        );

        assertFalse(
            ReglasAutorizacion.esRutaAdministrativa(null)
        );
    }

    // RF-12: pruebas adicionales de seguridad

    @Test
    void rutaAdministrativaParecidaNoDebeCoincidir() {
        assertFalse(
            ReglasAutorizacion.esRutaAdministrativa(
                "/api/administracion-falsa/cursos"
            )
        );
    }

    @Test
    void rutaConSeccionVaciaNoDebeCoincidir() {
        assertNull(
            ReglasAutorizacion.obtenerPermiso(
                "PATCH",
                "/api/administracion/cursos/CC2008/secciones//estado"
            )
        );
    }

    @Test
    void rutaConCursoVacioNoDebeCoincidir() {
        assertNull(
            ReglasAutorizacion.obtenerPermiso(
                "PATCH",
                "/api/administracion/cursos//secciones/10/estado"
            )
        );
    }

    @Test
    void rutaConOperacionAdicionalNoDebeCoincidir() {
        assertNull(
            ReglasAutorizacion.obtenerPermiso(
                "PATCH",
                "/api/administracion/cursos/CC2008/secciones/10/estado/extra"
            )
        );
    }

    @Test
    void metodoPatchEnMinusculasDebeCoincidir() {
        assertEquals(
            Permiso.GESTIONAR_ESTADO,
            ReglasAutorizacion.obtenerPermiso(
                "patch",
                "/api/administracion/cursos/CC2008/secciones/10/estado"
            )
        );
    }

}
