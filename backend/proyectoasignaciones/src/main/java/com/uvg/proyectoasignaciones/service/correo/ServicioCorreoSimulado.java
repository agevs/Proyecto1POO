package com.uvg.proyectoasignaciones.service.correo;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("correo-simulado")
public class ServicioCorreoSimulado implements ServicioCorreo {

    @Override
    public void enviarNotificacion(
            String destinatario,
            String asunto,
            String mensaje) {

        if (destinatario == null || destinatario.isBlank()
                || asunto == null || asunto.isBlank()
                || mensaje == null || mensaje.isBlank()) {
            throw new IllegalArgumentException(
                    "Los datos del correo son obligatorios"
            );
        }

        System.out.println(
                "SIMULACION: solicitud de correo procesada"
        );
    }
}
