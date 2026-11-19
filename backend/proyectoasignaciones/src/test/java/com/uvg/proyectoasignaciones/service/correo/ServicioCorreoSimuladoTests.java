package com.uvg.proyectoasignaciones.service.correo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServicioCorreoSimuladoTests {

    private final ServicioCorreo servicio =
            new ServicioCorreoSimulado();

    @Test
    void aceptaDatosValidos() {
        assertDoesNotThrow(() ->
                servicio.enviarNotificacion(
                        "prueba@example.com",
                        "Cambio de estado",
                        "La seccion fue confirmada"
                )
        );
    }

    @Test
    void rechazaDestinatarioVacio() {
        assertThrows(IllegalArgumentException.class, () ->
                servicio.enviarNotificacion(
                        " ",
                        "Cambio de estado",
                        "Mensaje de prueba"
                )
        );
    }

    @Test
    void rechazaAsuntoVacio() {
        assertThrows(IllegalArgumentException.class, () ->
                servicio.enviarNotificacion(
                        "prueba@example.com",
                        "",
                        "Mensaje de prueba"
                )
        );
    }

    @Test
    void rechazaMensajeVacio() {
        assertThrows(IllegalArgumentException.class, () ->
                servicio.enviarNotificacion(
                        "prueba@example.com",
                        "Cambio de estado",
                        " "
                )
        );
    }
}
