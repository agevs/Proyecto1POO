package com.uvg.proyectoasignaciones.controller;
import java.util.ArrayList;

import com.uvg.proyectoasignaciones.model.Curso;
import com.uvg.proyectoasignaciones.model.Docente;
import com.uvg.proyectoasignaciones.model.Estudiante;
import com.uvg.proyectoasignaciones.model.Seccion;
import com.uvg.proyectoasignaciones.model.Seguimiento;

public class AdministracionController {
    private ArrayList<Curso> cursos;
    private ArrayList<Docente> docentes;
    private NotificacionController notificacionController;
    private SeguimientoController seguimientoController;

    public AdministracionController(
            ArrayList<Curso> cursos,
            ArrayList<Docente> docentes,
            NotificacionController notificacionController,
            SeguimientoController seguimientoController) {

        this.cursos = cursos;
        this.docentes = docentes;
        this.notificacionController = notificacionController;
        this.seguimientoController = seguimientoController;
    }

    public boolean registrarCurso(Curso curso) {

        if (curso == null) {
            return false;
        }

        for (Curso cursoExistente : cursos) {
            if (cursoExistente.getCodigoCurso()
                    .equalsIgnoreCase(curso.getCodigoCurso())) {
                return false;
            }
        }

        cursos.add(curso);
        return true;
    }

    public boolean actualizarCurso(Curso curso) {

        if (curso == null) {
            return false;
        }

        for (int i = 0; i < cursos.size(); i++) {
            if (cursos.get(i).getCodigoCurso()
                    .equalsIgnoreCase(curso.getCodigoCurso())) {

                cursos.set(i, curso);
                return true;
            }
        }

        return false;
    }

    public boolean agregarSeccion(Curso curso, Seccion seccion) {

        if (curso == null || seccion == null) {
            return false;
        }

        curso.agregarSeccion(seccion);
        return true;
    }

    public boolean actualizarSeccion(Seccion seccion) {
        return seccion != null;
    }

    public boolean actualizarEstadoSeccion(
            Seccion seccion,
            String estado) {

        if (seccion == null || estado == null) {
            return false;
        }

        seccion.actualizarEstado(estado);

        notificarCambio(
                seccion,
                "El estado de la sección "
                        + seccion.getNumeroSeccion()
                        + " cambió a: "
                        + estado
        );

        return true;
    }

    public boolean registrarDocente(Docente docente) {

        if (docente == null) {
            return false;
        }

        for (Docente docenteExistente : docentes) {
            if (docenteExistente.getIdDocente()
                    == docente.getIdDocente()) {
                return false;
            }
        }

        docentes.add(docente);
        return true;
    }

    public boolean confirmarDocente(
            Seccion seccion,
            Docente docente) {

        if (seccion == null || docente == null) {
            return false;
        }

        seccion.asignarDocente(docente);
        seccion.actualizarEstado("Confirmado");

        notificarCambio(
                seccion,
                "Se confirmó al docente "
                        + docente.getNombre()
                        + " para la sección "
                        + seccion.getNumeroSeccion()
        );

        return true;
    }

    public void notificarCambio(
            Seccion seccion,
            String mensaje) {

        for (Seguimiento seguimiento :
                seguimientoController.getSeguimientos()) {

            if (seguimiento.getSeccion().equals(seccion)
                    && seguimiento.isActivo()) {

                Estudiante estudiante =
                        seguimiento.getEstudiante();

                notificacionController.crearNotificacion(
                        estudiante,
                        seccion,
                        mensaje
                );
            }
        }
    }

    public ArrayList<Curso> getCursos() {
        return cursos;
    }

    public void setCursos(ArrayList<Curso> cursos) {
        this.cursos = cursos;
    }

    public ArrayList<Docente> getDocentes() {
        return docentes;
    }

    public void setDocentes(ArrayList<Docente> docentes) {
        this.docentes = docentes;
    }

    public NotificacionController getNotificacionController() {
        return notificacionController;
    }

    public void setNotificacionController(
            NotificacionController notificacionController) {
        this.notificacionController = notificacionController;
    }

    public SeguimientoController getSeguimientoController() {
        return seguimientoController;
    }

    public void setSeguimientoController(
            SeguimientoController seguimientoController) {
        this.seguimientoController = seguimientoController;
    }

    @Override
    public String toString() {
        return "AdministracionController{" +
                "cantidadCursos=" + cursos.size() +
                ", cantidadDocentes=" + docentes.size() +
                '}';
    }
    
}
