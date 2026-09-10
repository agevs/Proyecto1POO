package com.uvg.proyectoasignaciones.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "seguimientos")

public class Seguimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idSeguimiento;

    @ManyToOne
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudiante;

    @ManyToOne
    @JoinColumn(name = "seccion_id")
    private Seccion seccion;
    private boolean activo;

    public Seguimiento() {
    }
    
    public Seguimiento(int idSeguimiento, Estudiante estudiante, Seccion seccion) {
        this.idSeguimiento = idSeguimiento;
        this.estudiante = estudiante;
        this.seccion = seccion;
        this.activo = true;
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }

    public int getIdSeguimiento() {
        return idSeguimiento;
    }

    public void setIdSeguimiento(int idSeguimiento) {
        this.idSeguimiento = idSeguimiento;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public Seccion getSeccion() {
        return seccion;
    }

    public void setSeccion(Seccion seccion) {
        this.seccion = seccion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Seguimiento{" +
                "idSeguimiento=" + idSeguimiento +
                ", estudiante=" + estudiante.getCarne() +
                ", seccion=" + seccion.getNumeroSeccion() +
                ", activo=" + activo +
                '}';
    }
}
