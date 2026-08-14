package com.uvg.proyectoasignaciones.model;
import java.util.ArrayList;

public class Estudiante extends Usuario {
    private String carne;
    private ArrayList<Seguimiento> seguimientos;
    private ArrayList<Notificacion> notificaciones;

    public Estudiante(int idUsuario, String nombre, String correo, String contrasena, String carne) {
    
        super(idUsuario, nombre, correo, contrasena);

        this.carne = carne;
        this.seguimientos = new ArrayList<>();
        this.notificaciones = new ArrayList<>();
    }

    public void agregarSeguimiento(Seguimiento seguimiento) {
        seguimientos.add(seguimiento);
    }

    public void eliminarSeguimiento(Seguimiento seguimiento) {
        seguimientos.remove(seguimiento);
    }

    public void agregarNotificacion(Notificacion notificacion) {
        notificaciones.add(notificacion);
    }

    public String getCarne() {
    return carne;
    }

    public void setCarne(String carne) {
        this.carne = carne;
    }

    public ArrayList<Seguimiento> getSeguimientos() {
        return seguimientos;
    }

    public void setSeguimientos(ArrayList<Seguimiento> seguimientos) {
        this.seguimientos = seguimientos;
    }

    public ArrayList<Notificacion> getNotificaciones() {
        return notificaciones;
    }

    public void setNotificaciones(ArrayList<Notificacion> notificaciones) {
        this.notificaciones = notificaciones;
    }

    @Override
    public String toString() {
        return "Estudiante{" +
                "idUsuario=" + getIdUsuario() +
                ", nombre='" + getNombre() + '\'' +
                ", correo='" + getCorreo() + '\'' +
                ", carne='" + carne + '\'' +
                '}';
    }

}
