package com.uvg.proyectoasignaciones.service.correo;

public interface ServicioCorreo {

    void enviarNotificacion(
            String destinatario,
            String asunto,
            String mensaje
    );
}
