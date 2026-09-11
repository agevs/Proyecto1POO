package com.uvg.proyectoasignaciones.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idNotificacion;

    private String estudianteCarne;
    private String mensaje;
    private String fecha;
    private boolean leida;

    @ManyToOne
    @JoinColumn(name = "seccion_id")
    private Seccion seccion;

    protected Notificacion() {
        // Constructor requerido por JPA/Hibernate.
    }

    public Notificacion(String estudianteCarne, String mensaje, String fecha, Seccion seccion) {
        this.estudianteCarne = estudianteCarne;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.seccion = seccion;
        this.leida = false;
    }

    public Notificacion(int idNotificacion, String mensaje, String fecha, Seccion seccion) {
        this(null, mensaje, fecha, seccion);
        this.idNotificacion = idNotificacion;
    }

    public void marcarComoLeida() {
        this.leida = true;
    }

    public Integer getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(Integer idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public String getEstudianteCarne() {
        return estudianteCarne;
    }

    public void setEstudianteCarne(String estudianteCarne) {
        this.estudianteCarne = estudianteCarne;
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
                ", estudianteCarne='" + estudianteCarne + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", fecha='" + fecha + '\'' +
                ", leida=" + leida +
                '}';
    }
}
