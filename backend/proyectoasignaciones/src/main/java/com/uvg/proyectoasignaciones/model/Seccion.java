package com.uvg.proyectoasignaciones.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity
public class Seccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSeccion;

    private int numeroSeccion;
    private String horario;
    private int cuposDisponibles;
    private String estado;

    @ManyToOne
    @JsonIgnore
    private Curso curso;
    
    @Transient
    private Docente docente;

    // Constructor vacío requerido por JPA/Hibernate
    public Seccion() {
    }

    public Seccion(
            int numeroSeccion,
            String horario,
            int cuposDisponibles,
            String estado) {

        this.numeroSeccion = numeroSeccion;
        this.horario = horario;
        this.cuposDisponibles = cuposDisponibles;
        this.estado = estado;
        this.docente = null;
    }

    public void asignarDocente(Docente docente) {
        this.docente = docente;
    }

    public void actualizarEstado(String estado) {
        this.estado = estado;
    }

    public Long getIdSeccion() {
        return idSeccion;
    }

    public void setIdSeccion(Long idSeccion) {
        this.idSeccion = idSeccion;
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

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    @Override
    public String toString() {
        return "Seccion{" +
                "idSeccion=" + idSeccion +
                ", numeroSeccion=" + numeroSeccion +
                ", horario='" + horario + '\'' +
                ", cuposDisponibles=" + cuposDisponibles +
                ", estado='" + estado + '\'' +
                ", docente=" +
                (docente != null ? docente.getNombre() : "STAFF") +
                '}';
    }
}
