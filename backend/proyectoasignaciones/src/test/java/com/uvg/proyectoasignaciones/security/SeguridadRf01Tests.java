
package com.uvg.proyectoasignaciones.security;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SeguridadRf01Tests {

    @Autowired
    private MockMvc mockMvc;

    // PRUEBA 1:
    // Un usuario sin sesion no puede consultar seguimientos.
    @Test
    void usuarioSinSesionNoPuedeConsultarSeguimientos()
            throws Exception {

        mockMvc.perform(
                get("/api/seguimientos/estudiante/26594")
        ).andExpect(
                status().isUnauthorized()
        );
    }

    // PRUEBA 2:
    // Un estudiante autenticado puede acceder a la ruta.
    @Test
    void estudianteAutenticadoPuedeConsultarSeguimientos()
            throws Exception {

        mockMvc.perform(
                get("/api/seguimientos/estudiante/26594")
                    .with(user("estudiante@uvg.edu.gt")
                        .roles("ESTUDIANTE"))
        ).andExpect(
                status().isOk()
        );
    }

    // PRUEBA 3:
    // Un estudiante no puede modificar estados administrativos.
    @Test
    void estudianteNoPuedeModificarEstadoAdministrativo()
            throws Exception {

        mockMvc.perform(
                patch(
                    "/api/administracion/cursos/CC2008/secciones/10/estado"
                )
                .with(user("estudiante@uvg.edu.gt")
                    .roles("ESTUDIANTE"))
                .contentType("application/json")
                .content("{\"estado\":\"Disponible\"}")
        ).andExpect(
                status().isForbidden()
        );
    }

    
        @Test
        void estudianteNoPuedeConsultarSeguimientosAjenos()
                throws Exception {

        mockMvc.perform(
                get("/api/seguimientos/estudiante/99999")
                        .with(user("estudiante@uvg.edu.gt")
                        .roles("ESTUDIANTE"))
        ).andExpect(
                status().isForbidden()
        );
        }

}

