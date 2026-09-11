package com.uvg.proyectoasignaciones.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Curso {

    @Id
    private String codigoCurso;

    private String nombreCurso;
    private int creditos;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL)
    private List<Seccion> secciones;

    // Constructor vacío requerido por JPA/Hibernate
    public Curso() {
        this.secciones = new ArrayList<>();
    }

    public Curso(String codigoCurso, String nombreCurso, int creditos) {
        this.codigoCurso = codigoCurso;
        this.nombreCurso = nombreCurso;
        this.creditos = creditos;
        this.secciones = new ArrayList<>();
    }

    public void agregarSeccion(Seccion seccion) {
        secciones.add(seccion);
        seccion.setCurso(this);
    }

    public void eliminarSeccion(Seccion seccion) {
        secciones.remove(seccion);
        seccion.setCurso(null);
    }

    public String getCodigoCurso() {
        return codigoCurso;
    }

    public void setCodigoCurso(String codigoCurso) {
        this.codigoCurso = codigoCurso;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }

    public void setNombreCurso(String nombreCurso) {
        this.nombreCurso = nombreCurso;
    }

    public int getCreditos() {
        return creditos;
    }

    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }

    public List<Seccion> getSecciones() {
        return secciones;
    }

    public void setSecciones(List<Seccion> secciones) {
        this.secciones = secciones;
    }

    @Override
    public String toString() {
        return "Curso{" +
                "codigoCurso='" + codigoCurso + '\'' +
                ", nombreCurso='" + nombreCurso + '\'' +
                ", creditos=" + creditos +
                ", cantidadSecciones=" + secciones.size() +
                '}';
    }
}
