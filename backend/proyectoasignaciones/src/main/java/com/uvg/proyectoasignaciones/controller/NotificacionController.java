package com.uvg.proyectoasignaciones.controller;
import java.util.ArrayList;

import com.uvg.proyectoasignaciones.model.Estudiante;
import com.uvg.proyectoasignaciones.model.Notificacion;
import com.uvg.proyectoasignaciones.model.Seccion;

public class NotificacionController {
    private ArrayList<Notificacion> notificaciones;

    public NotificacionController(ArrayList<Notificacion> notificaciones) {
        this.notificaciones = notificaciones;
    }

    public Notificacion crearNotificacion(
            Estudiante estudiante,
            Seccion seccion,
            String mensaje) {

        int nuevoId = notificaciones.size() + 1;
        String fecha = java.time.LocalDateTime.now().toString();

        Notificacion nuevaNotificacion =
                new Notificacion(nuevoId, mensaje, fecha, seccion);

        notificaciones.add(nuevaNotificacion);
        estudiante.agregarNotificacion(nuevaNotificacion);

        return nuevaNotificacion;
    }

    public ArrayList<Notificacion> obtenerNotificaciones(
            Estudiante estudiante) {

        return estudiante.getNotificaciones();
    }

    public ArrayList<Notificacion> obtenerNotificacionesNoLeidas(
            Estudiante estudiante) {

        ArrayList<Notificacion> resultado = new ArrayList<>();

        for (Notificacion notificacion : estudiante.getNotificaciones()) {

            if (!notificacion.isLeida()) {
                resultado.add(notificacion);
            }
        }

        return resultado;
    }

    public void marcarComoLeida(Notificacion notificacion) {

        if (notificacion != null) {
            notificacion.marcarComoLeida();
        }
    }

    public ArrayList<Notificacion> getNotificaciones() {
        return notificaciones;
    }

    public void setNotificaciones(ArrayList<Notificacion> notificaciones) {
        this.notificaciones = notificaciones;
    }

    @Override
    public String toString() {
        return "NotificacionController{" +
                "cantidadNotificaciones=" + notificaciones.size() +
                '}';
    }
}
