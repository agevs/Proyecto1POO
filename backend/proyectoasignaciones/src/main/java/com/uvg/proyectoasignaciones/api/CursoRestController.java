package com.uvg.proyectoasignaciones.api;

import java.util.List;
import java.util.Optional;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uvg.proyectoasignaciones.model.Curso;
import com.uvg.proyectoasignaciones.model.Seccion;
import com.uvg.proyectoasignaciones.repository.CursoRepository;
import com.uvg.proyectoasignaciones.repository.SeccionRepository;

@CrossOrigin(origins = {
    "http://127.0.0.1:5500",
    "http://localhost:5500",
    "null"
})
@RestController
@RequestMapping("/api/cursos")
public class CursoRestController {

    private final CursoRepository cursoRepository;
    private final SeccionRepository seccionRepository;

    public CursoRestController(CursoRepository cursoRepository, SeccionRepository seccionRepository) {
        this.seccionRepository = seccionRepository;
        this.cursoRepository = cursoRepository;
    }

    @GetMapping
    public List<Curso> obtenerCursos() {
        return cursoRepository.findAll();
    }


    @PostMapping
    public ResponseEntity<Curso> crearCurso(@RequestBody Curso nuevoCurso) {
        Curso cursoGuardado = cursoRepository.save(nuevoCurso);
        return new ResponseEntity<>(cursoGuardado, HttpStatus.CREATED);
    }

    @PutMapping ("/{codigoCurso}")
    public ResponseEntity<Curso> actualizarCurso(@PathVariable String codigoCurso, @RequestBody Curso cursoDetalles) {

        Optional<Curso> cursoOpt = cursoRepository.findById(codigoCurso);

        if (cursoOpt.isPresent()) {
            Curso curso = cursoOpt.get();
            curso.setNombreCurso(cursoDetalles.getNombreCurso());
            curso.setCreditos(cursoDetalles.getCreditos());

            Curso cursoActualizado = cursoRepository.save(curso);
            return ResponseEntity.ok(cursoActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping ("/{codigoCurso}/secciones")
    public ResponseEntity<Seccion> agregarSeccion(@PathVariable String codigoCurso, @RequestBody Seccion nuevaSeccion) {
        Optional<Curso> cursoOpt = cursoRepository.findById(codigoCurso);
        if (cursoOpt.isPresent()) {
            Curso curso = cursoOpt.get();
            curso.agregarSeccion(nuevaSeccion);

            cursoRepository.save(curso);
            return new ResponseEntity<>(nuevaSeccion, HttpStatus.CREATED);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping ("/secciones/{idSeccion}")
    public ResponseEntity<Seccion> actualizarSeccion(@PathVariable Long idSeccion, @RequestBody Seccion seccionDetalles) {
        Optional<Seccion> seccionOpt = seccionRepository.findById(idSeccion);
        if (seccionOpt.isPresent()) {
            Seccion seccion = seccionOpt.get();
            
            Seccion seccionActualizada = seccionRepository.save(seccion);
            return ResponseEntity.ok(seccionActualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    
    }


}