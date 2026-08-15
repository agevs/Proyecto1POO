package com.uvg.proyectoasignaciones.controller;
import java.util.ArrayList;

import com.uvg.proyectoasignaciones.model.Curso;
import com.uvg.proyectoasignaciones.model.Seccion;

public class CursoController {
    private ArrayList<Curso> cursos;

    public CursoController(ArrayList<Curso> cursos) {
        this.cursos = cursos;
    }

    public ArrayList<Curso> obtenerCursos() {
        return cursos;
    }

    public Curso buscarCurso(String codigoCurso) {

        for (Curso curso : cursos) {
            if (curso.getCodigoCurso().equalsIgnoreCase(codigoCurso)) {
                return curso;
            }
        }

        return null;
    }

    public ArrayList<Seccion> obtenerSecciones(String codigoCurso) {

        Curso curso = buscarCurso(codigoCurso);

        if (curso != null) {
            return curso.getSecciones();
        }

        return new ArrayList<>();
    }

    public Seccion buscarSeccion(String codigoCurso, int numeroSeccion) {

        Curso curso = buscarCurso(codigoCurso);

        if (curso != null) {
            for (Seccion seccion : curso.getSecciones()) {
                if (seccion.getNumeroSeccion() == numeroSeccion) {
                    return seccion;
                }
            }
        }

        return null;
    }

    public String consultarEstadoSeccion(String codigoCurso, int numeroSeccion) {

        Seccion seccion = buscarSeccion(codigoCurso, numeroSeccion);

        if (seccion != null) {
            return seccion.getEstado();
        }

        return null;
    }

    public ArrayList<Curso> getCursos() {
    return cursos;
    }

    public void setCursos(ArrayList<Curso> cursos) {
        this.cursos = cursos;
    }

    @Override
    public String toString() {
        return "CursoController{" +
                "cantidadCursos=" + cursos.size() +
                '}';
    }

}
