package com.uvg.proyectoasignaciones.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.uvg.proyectoasignaciones.model.Estudiante;
import com.uvg.proyectoasignaciones.model.Notificacion;
import com.uvg.proyectoasignaciones.model.Seccion;
import com.uvg.proyectoasignaciones.repository.NotificacionRepository;

@Service
public class NotificacionController {

    private final NotificacionRepository notificacionRepository;

    public NotificacionController(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public Notificacion crearNotificacion(Estudiante estudiante, Seccion seccion, String mensaje) {
        if (estudiante == null || mensaje == null || mensaje.isBlank()) {
            throw new IllegalArgumentException("El estudiante y el mensaje son obligatorios");
        }

        Notificacion notificacion = new Notificacion(
                estudiante.getCarne(), mensaje.trim(), LocalDateTime.now().toString(), seccion);
        return notificacionRepository.save(notificacion);
    }

    public List<Notificacion> obtenerNotificaciones(Estudiante estudiante) {
        if (estudiante == null) {
            return List.of();
        }
        return notificacionRepository
                .findByEstudianteCarneOrderByIdNotificacionDesc(estudiante.getCarne());
    }

    public List<Notificacion> obtenerNotificacionesNoLeidas(Estudiante estudiante) {
        if (estudiante == null) {
            return List.of();
        }
        return notificacionRepository
                .findByEstudianteCarneAndLeidaFalseOrderByIdNotificacionDesc(estudiante.getCarne());
    }

    public Optional<Notificacion> marcarComoLeida(int idNotificacion, Estudiante estudiante) {
        if (estudiante == null) {
            return Optional.empty();
        }

        return notificacionRepository.findById(idNotificacion)
                .filter(notificacion -> estudiante.getCarne().equals(notificacion.getEstudianteCarne()))
                .map(notificacion -> {
                    notificacion.marcarComoLeida();
                    return notificacionRepository.save(notificacion);
                });
    }
}
