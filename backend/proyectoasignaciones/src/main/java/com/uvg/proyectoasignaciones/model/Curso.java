package com.uvg.proyectoasignaciones.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * Entidad JPA que representa un curso académico dentro del catálogo del sistema.
 * Contiene su código identificador, nombre, créditos, ubicación temporal (año y semestre)
 * y la lista de secciones asociadas.
 */
@Entity
public class Curso {

    @Id
    private String codigoCurso;

    private String nombreCurso;
    private int creditos;
    private Integer anio;
    private Integer semestre;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL)
    private List<Seccion> secciones;

    /**
     * Constructor vacío requerido por JPA/Hibernate.
     * Inicializa la lista de secciones vacía.
     */
    public Curso() {
        this.secciones = new ArrayList<>();
    }

    /**
     * Crea un nuevo curso con su código, nombre y cantidad de créditos.
     *
     * @param codigoCurso código único identificador del curso
     * @param nombreCurso nombre oficial del curso
     * @param creditos    cantidad de créditos académicos del curso
     */
    public Curso(String codigoCurso, String nombreCurso, int creditos) {
        this.codigoCurso = codigoCurso;
        this.nombreCurso = nombreCurso;
        this.creditos = creditos;
        this.secciones = new ArrayList<>();
    }

    /**
     * Asocia una sección al curso y actualiza la referencia bidireccional.
     *
     * @param seccion sección a agregar al curso
     */
    public void agregarSeccion(Seccion seccion) {
        secciones.add(seccion);
        seccion.setCurso(this);
    }

    /**
     * Desvincula una sección del curso y limpia su referencia al curso padre.
     *
     * @param seccion sección a eliminar del curso
     */
    public void eliminarSeccion(Seccion seccion) {
        secciones.remove(seccion);
        seccion.setCurso(null);
    }

    /**
     * Obtiene el código identificador del curso.
     *
     * @return código del curso
     */
    public String getCodigoCurso() {
        return codigoCurso;
    }

    /**
     * Establece el código identificador del curso.
     *
     * @param codigoCurso nuevo código del curso
     */
    public void setCodigoCurso(String codigoCurso) {
        this.codigoCurso = codigoCurso;
    }

    /**
     * Obtiene el nombre del curso.
     *
     * @return nombre del curso
     */
    public String getNombreCurso() {
        return nombreCurso;
    }

    /**
     * Establece el nombre del curso.
     *
     * @param nombreCurso nuevo nombre del curso
     */
    public void setNombreCurso(String nombreCurso) {
        this.nombreCurso = nombreCurso;
    }

    /**
     * Obtiene la cantidad de créditos del curso.
     *
     * @return número de créditos
     */
    public int getCreditos() {
        return creditos;
    }

    /**
     * Establece la cantidad de créditos del curso.
     *
     * @param creditos nueva cantidad de créditos
     */
    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }

    /**
     * Obtiene la lista de secciones asociadas al curso.
     *
     * @return lista de secciones
     */
    public List<Seccion> getSecciones() {
        return secciones;
    }

    /**
     * Reemplaza la lista de secciones asociadas al curso.
     *
     * @param secciones nueva lista de secciones
     */
    public void setSecciones(List<Seccion> secciones) {
        this.secciones = secciones;
    }

    /**
     * Obtiene el año académico en el que se imparte el curso.
     *
     * @return año académico, o {@code null} si no está asignado
     */
    public Integer getAnio() {
        return anio;
    }

    /**
     * Establece el año académico del curso.
     *
     * @param anio año académico a asignar
     */
    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    /**
     * Obtiene el semestre en el que se imparte el curso.
     *
     * @return número de semestre, o {@code null} si no está asignado
     */
    public Integer getSemestre() {
        return semestre;
    }

    /**
     * Establece el semestre en el que se imparte el curso.
     *
     * @param semestre número de semestre a asignar
     */
    public void setSemestre(Integer semestre) {
        this.semestre = semestre;
    }

    /**
     * Devuelve una representación en texto del curso con su código, nombre,
     * créditos y cantidad de secciones asociadas.
     *
     * @return cadena descriptiva del curso
     */
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
