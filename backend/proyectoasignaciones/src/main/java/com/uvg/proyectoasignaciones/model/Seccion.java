package com.uvg.proyectoasignaciones.model;

public class Seccion {
    private int numeroSeccion;
    private String horario;
    private int cuposDisponibles;
    private String estado;
    private Docente docente;

    public Seccion(int numeroSeccion, String horario, int cuposDisponibles, String estado){
        this.numeroSeccion = numeroSeccion;
        this.horario = horario;
        this.cuposDisponibles = cuposDisponibles;
        this.estado = estado;
        this.docente = null;
    }

    public void asignarDocente(Docente docente){
            this.docente = docente;
    }

    public void actualizarEstado(String estado) {
            this.estado = estado;
    }

    public int getNumeroSeccion() {
        return numeroSeccion;
    }

    public void setNumeroSeccion(int numeroSeccion) {
        this.numeroSeccion = numeroSeccion;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public int getCuposDisponibles() {
        return cuposDisponibles;
    }

    public void setCuposDisponibles(int cuposDisponibles) {
        this.cuposDisponibles = cuposDisponibles;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Docente getDocente() {
        return docente;
    }

    public void setDocente(Docente docente) {
        this.docente = docente;
    }

    @Override
    public String toString() {
        return "Seccion{" +
                "numeroSeccion=" + numeroSeccion +
                ", horario='" + horario + '\'' +
                ", cuposDisponibles=" + cuposDisponibles +
                ", estado='" + estado + '\'' +
                ", docente=" + (docente != null ? docente.getNombre() : "STAFF") +
                '}';
    }
 
}
