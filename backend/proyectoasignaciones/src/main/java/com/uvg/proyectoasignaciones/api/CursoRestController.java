package com.uvg.proyectoasignaciones.api;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uvg.proyectoasignaciones.controller.CursoController;
import com.uvg.proyectoasignaciones.model.Curso;
import com.uvg.proyectoasignaciones.model.Seccion;

@RestController
@RequestMapping("/api/cursos")
public class CursoRestController {

    private CursoController cursoController;

    public CursoRestController() {

        ArrayList<Curso> cursos = new ArrayList<>();

        Curso cursoPOO = new Curso(
                "CC2008",
                "Programación Orientada a Objetos 1",
                4
        );

        Seccion seccion10 = new Seccion(
                10,
                "Lu 10:00 AM / Mi 10:00 AM",
                25,
                "Docente en confirmación"
        );

        cursoPOO.agregarSeccion(seccion10);
        cursos.add(cursoPOO);

        this.cursoController = new CursoController(cursos);
    }

    @GetMapping
    public ArrayList<Curso> obtenerCursos() {
        return cursoController.obtenerCursos();
    }
}