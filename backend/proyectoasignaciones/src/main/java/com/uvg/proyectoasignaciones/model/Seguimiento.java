package com.uvg.proyectoasignaciones.model;

public class Seguimiento {
    private int idSeguimiento;
    private Estudiante estudiante;
    private Seccion seccion;
    private boolean activo;

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
