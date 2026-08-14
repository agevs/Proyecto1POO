package com.uvg.proyectoasignaciones.model;

public class Notificacion {
    private int idNotificacion;
    private String mensaje;
    private String fecha;
    private boolean leida;
    private Seccion seccion;

    public Notificacion(int idNotificacion, String mensaje, String fecha, Seccion seccion) {
        this.idNotificacion = idNotificacion;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.seccion = seccion;
        this.leida = false;
    }

    public void marcarComoLeida() {
        this.leida = true;
    }

    public int getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(int idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    public Seccion getSeccion() {
        return seccion;
    }

    public void setSeccion(Seccion seccion) {
        this.seccion = seccion;
    }

    @Override
    public String toString() {
        return "Notificacion{" +
                "idNotificacion=" + idNotificacion +
                ", mensaje='" + mensaje + '\'' +
                ", fecha='" + fecha + '\'' +
                ", leida=" + leida +
                ", seccion=" + seccion.getNumeroSeccion() +
                '}';
    }
}
