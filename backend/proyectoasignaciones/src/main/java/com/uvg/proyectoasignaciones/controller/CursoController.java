package com.uvg.proyectoasignaciones.controller;

import java.util.ArrayList;
import java.util.List;

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

    public Seccion buscarSeccion(
            String codigoCurso,
            int numeroSeccion) {

        Curso curso = buscarCurso(codigoCurso);

        if (curso == null) {
            return null;
        }

        List<Seccion> secciones = curso.getSecciones();

        for (Seccion seccion : secciones) {

            if (seccion.getNumeroSeccion() == numeroSeccion) {
                return seccion;
            }
        }

        return null;
    }
}
