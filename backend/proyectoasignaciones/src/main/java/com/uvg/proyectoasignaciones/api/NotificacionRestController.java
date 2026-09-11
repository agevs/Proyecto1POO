package com.uvg.proyectoasignaciones.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uvg.proyectoasignaciones.controller.NotificacionController;
import com.uvg.proyectoasignaciones.data.DatosSistema;
import com.uvg.proyectoasignaciones.model.Estudiante;
import com.uvg.proyectoasignaciones.model.Notificacion;

@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"})
@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionRestController {

    private final NotificacionController notificacionController;
    private final DatosSistema datosSistema;

    public NotificacionRestController(
            NotificacionController notificacionController,
            DatosSistema datosSistema) {
        this.notificacionController = notificacionController;
        this.datosSistema = datosSistema;
    }

    @GetMapping
    public List<Notificacion> obtenerNotificaciones(
            @RequestParam(defaultValue = "false") boolean soloNoLeidas) {
        Estudiante estudiante = datosSistema.getEstudianteActual();
        return soloNoLeidas
                ? notificacionController.obtenerNotificacionesNoLeidas(estudiante)
                : notificacionController.obtenerNotificaciones(estudiante);
    }

    @PatchMapping("/{idNotificacion}/leida")
    public ResponseEntity<Notificacion> marcarComoLeida(@PathVariable int idNotificacion) {
        return notificacionController
                .marcarComoLeida(idNotificacion, datosSistema.getEstudianteActual())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
