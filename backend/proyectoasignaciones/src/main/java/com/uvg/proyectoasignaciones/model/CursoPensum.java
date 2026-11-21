package com.uvg.proyectoasignaciones.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Curso del pensum oficial de Ingeniería en Ciencia de la Computación
 * y Tecnologías de la Información (UVG, Pensum 2025).
 *
 * Es independiente de {@link Curso}: el pensum es solo de consulta y
 * no afecta el catálogo de asignación ni sus secciones.
 */
@Entity
@Table(name = "curso_pensum")
public class CursoPensum {

    /** Curso común a todos los estudiantes. */
    public static final int RUTA_COMUN = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Puede ser null mientras el código oficial no esté confirmado. */
    private String codigo;

    private String nombre;
    private int creditos;

    /** Año de la carrera: 1 a 5. */
    private int anio;

    /** Semestre (ciclo) dentro del año: 1 o 2. */
    private int semestre;

    /** 0 = común, 1 = Ruta 1, 2 = Ruta 2 (rutas de matemática). */
    private int ruta;

    // Constructor vacío requerido por JPA/Hibernate
    public CursoPensum() {
    }

    public CursoPensum(
            String codigo,
            String nombre,
            int creditos,
            int anio,
            int semestre,
            int ruta) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.creditos = creditos;
        this.anio = anio;
        this.semestre = semestre;
        this.ruta = ruta;
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCreditos() {
        return creditos;
    }

    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public int getSemestre() {
        return semestre;
    }

    public void setSemestre(int semestre) {
        this.semestre = semestre;
    }

    public int getRuta() {
        return ruta;
    }

    public void setRuta(int ruta) {
        this.ruta = ruta;
    }

    @Override
    public String toString() {
        return "CursoPensum{" +
                "codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", creditos=" + creditos +
                ", anio=" + anio +
                ", semestre=" + semestre +
                ", ruta=" + ruta +
                '}';
    }
}
