package com.uvg.proyectoasignaciones.api;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uvg.proyectoasignaciones.model.Curso;
import com.uvg.proyectoasignaciones.repository.CursoRepository;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/cursos")
public class CursoRestController {

    private final CursoRepository cursoRepository;

    public CursoRestController(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    @GetMapping
    public List<Curso> obtenerCursos() {
        return cursoRepository.findAll();
    }
}