package com.uvg.proyectoasignaciones.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "docentes")
public class Docente {
    @Id
    private int idDocente;
    private String nombre;
    private String correo;

    protected Docente() {
        // Requerido por JPA. El ID conserva el código institucional.
    }

    public Docente(int idDocente, String nombre, String correo) {
        this.idDocente = idDocente;
        this.nombre = nombre;
        this.correo = correo;
    }

    public int getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(int idDocente) {
        this.idDocente = idDocente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    @Override
    public String toString() {
        return "Docente{" +
                "idDocente=" + idDocente +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                '}';
    }
    
}
