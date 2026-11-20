package com.uvg.proyectoasignaciones.service.correo;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!correo-real & !correo-simulado")
public class ServicioCorreoDesactivado implements ServicioCorreo {

    @Override
    public void enviarNotificacion(
            String destinatario,
            String asunto,
            String mensaje) {

        // Envio desactivado por defecto.
        // Las notificaciones internas siguen funcionando.
    }
}
