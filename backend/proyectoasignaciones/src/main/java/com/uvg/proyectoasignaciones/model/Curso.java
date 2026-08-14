package com.uvg.proyectoasignaciones.model;
import java.util.ArrayList;

public class Curso {
    private String codigoCurso;
    private String nombreCurso;
    private int creditos;
    private ArrayList<Seccion> secciones;

    public Curso(String codigoCurso, String nombreCurso, int creditos) {
        this.codigoCurso = codigoCurso;
        this.nombreCurso = nombreCurso;
        this.creditos = creditos;
        this.secciones = new ArrayList<>();
    }

    public void agregarSeccion(Seccion seccion) {
        secciones.add(seccion);
    }

    public void eliminarSeccion(Seccion seccion) {
        secciones.remove(seccion);
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

    public ArrayList<Seccion> getSecciones() {
        return secciones;
    }

    public void setSecciones(ArrayList<Seccion> secciones) {
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
