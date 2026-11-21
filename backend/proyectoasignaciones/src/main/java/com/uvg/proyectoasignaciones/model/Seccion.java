package com.uvg.proyectoasignaciones.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

/**
 * Entidad JPA que representa una sección asignada a un {@link Curso}.
 * Almacena el número de sección, horario, cupos disponibles, estado actual,
 * el curso al que pertenece y el docente asignado.
 */
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

    @ManyToOne
    @JoinColumn(name = "docente_id")
    private Docente docente;

    /**
     * Constructor vacío requerido por JPA/Hibernate.
     */
    public Seccion() {
    }

    /**
     * Crea una nueva sección con sus datos principales e inicializa el docente como {@code null}.
     *
     * @param numeroSeccion    número identificador de la sección
     * @param horario          horario asignado a la sección
     * @param cuposDisponibles cantidad de cupos disponibles
     * @param estado           estado inicial de la sección
     */
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

    /**
     * Asigna un docente a esta sección.
     *
     * @param docente docente a asignar a la sección
     */
    public void asignarDocente(Docente docente) {
        this.docente = docente;
    }

    /**
     * Actualiza el estado de la sección.
     *
     * @param estado nuevo estado de la sección
     */
    public void actualizarEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Obtiene el identificador único de la sección.
     *
     * @return ID de la sección
     */
    public Long getIdSeccion() {
        return idSeccion;
    }

    /**
     * Establece el identificador único de la sección.
     *
     * @param idSeccion nuevo ID de la sección
     */
    public void setIdSeccion(Long idSeccion) {
        this.idSeccion = idSeccion;
    }

    /**
     * Obtiene el número de la sección.
     *
     * @return número de sección
     */
    public int getNumeroSeccion() {
        return numeroSeccion;
    }

    /**
     * Establece el número de la sección.
     *
     * @param numeroSeccion nuevo número de sección
     */
    public void setNumeroSeccion(int numeroSeccion) {
        this.numeroSeccion = numeroSeccion;
    }

    /**
     * Obtiene el horario de la sección.
     *
     * @return horario de la sección
     */
    public String getHorario() {
        return horario;
    }

    /**
     * Establece el horario de la sección.
     *
     * @param horario nuevo horario de la sección
     */
    public void setHorario(String horario) {
        this.horario = horario;
    }

    /**
     * Obtiene la cantidad de cupos disponibles en la sección.
     *
     * @return cupos disponibles
     */
    public int getCuposDisponibles() {
        return cuposDisponibles;
    }

    /**
     * Establece la cantidad de cupos disponibles en la sección.
     *
     * @param cuposDisponibles nueva cantidad de cupos disponibles
     */
    public void setCuposDisponibles(int cuposDisponibles) {
        this.cuposDisponibles = cuposDisponibles;
    }

    /**
     * Obtiene el estado actual de la sección.
     *
     * @return estado de la sección
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado de la sección.
     *
     * @param estado nuevo estado de la sección
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Obtiene el docente asignado a la sección.
     *
     * @return docente asignado, o {@code null} si no tiene docente
     */
    public Docente getDocente() {
        return docente;
    }

    /**
     * Establece el docente asignado a la sección.
     *
     * @param docente docente a asignar
     */
    public void setDocente(Docente docente) {
        this.docente = docente;
    }

    /**
     * Obtiene el curso al que pertenece esta sección.
     *
     * @return curso asociado
     */
    public Curso getCurso() {
        return curso;
    }

    /**
     * Establece el curso al que pertenece esta sección.
     *
     * @param curso curso padre a asociar
     */
    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    /**
     * Devuelve una representación en texto de la sección con su ID, número,
     * horario, cupos disponibles, estado y docente (o {@code STAFF} si no hay docente).
     *
     * @return cadena descriptiva de la sección
     */
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
