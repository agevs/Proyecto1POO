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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uvg.proyectoasignaciones.model.Curso;
import com.uvg.proyectoasignaciones.model.Seccion;
import com.uvg.proyectoasignaciones.repository.CursoRepository;
import com.uvg.proyectoasignaciones.repository.SeccionRepository;

/**
 * Controlador REST para gestionar cursos, secciones y filtros académicos.
 */
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

    /**
     * Crea una instancia de {@code CursoRestController} con sus repositorios.
     *
     * @param cursoRepository   repositorio de acceso a datos de cursos
     * @param seccionRepository repositorio de acceso a datos de secciones
     */
    public CursoRestController(CursoRepository cursoRepository, SeccionRepository seccionRepository) {
        this.seccionRepository = seccionRepository;
        this.cursoRepository = cursoRepository;
    }

    /**
     * Obtiene la lista completa de cursos registrados.
     *
     * @return lista de todos los cursos
     */
    @GetMapping
    public List<Curso> obtenerCursos() {
        return cursoRepository.findAll();
    }

    /**
     * Filtra los cursos según año, semestre y estado de sus secciones.
     *
     * @param anio     año académico opcional para filtrar
     * @param semestre semestre opcional para filtrar (ignora mayúsculas y minúsculas)
     * @param estado   estado opcional de sección para filtrar
     * @return respuesta HTTP con la lista de cursos que cumplen los filtros indicados
     */
    @GetMapping("/filtrar")
    public ResponseEntity<List<Curso>> filtrarCursos(
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) String semestre,
            @RequestParam(required = false) String estado) {

        List<Curso> todosLosCursos = cursoRepository.findAll();

        List<Curso> cursosFiltrados = todosLosCursos.stream().filter(curso -> {
            boolean cumpleAnio = (anio == null) || (curso.getAnio() == anio);
            boolean cumpleSemestre = (semestre == null) || (curso.getSemestre() != null && String.valueOf(curso.getSemestre()).equalsIgnoreCase(semestre));
            boolean cumpleEstado = (estado == null) || curso.getSecciones().stream()
                    .anyMatch(seccion -> seccion.getEstado() != null && seccion.getEstado().equalsIgnoreCase(estado));

            return cumpleAnio && cumpleSemestre && cumpleEstado;
        }).toList();

        System.out.println("[FILTROS] Aplicados -> Año: " + anio + ", Semestre: " + semestre + ", Estado: " + estado
                .concat(" | Cursos encontrados: " + cursosFiltrados.size()));

        return ResponseEntity.ok(cursosFiltrados);
    }

    /**
     * Crea y guarda un nuevo curso en el sistema.
     *
     * @param nuevoCurso datos del curso a registrar
     * @return respuesta HTTP {@code 201 Created} con el curso guardado
     */
    @PostMapping
    public ResponseEntity<Curso> crearCurso(@RequestBody Curso nuevoCurso) {
        Curso cursoGuardado = cursoRepository.save(nuevoCurso);
        return new ResponseEntity<>(cursoGuardado, HttpStatus.CREATED);
    }

    /**
     * Actualiza el nombre y los créditos de un curso existente.
     *
     * @param codigoCurso  código identificador del curso
     * @param cursoDetalles datos actualizados del curso
     * @return respuesta HTTP {@code 200 OK} con el curso actualizado, o {@code 404 Not Found} si no existe
     */
    @PutMapping("/{codigoCurso}")
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

    /**
     * Agrega una nueva sección a un curso existente.
     *
     * @param codigoCurso  código del curso al que se añadirá la sección
     * @param nuevaSeccion datos de la sección a registrar
     * @return respuesta HTTP {@code 201 Created} con la sección agregada, o {@code 404 Not Found} si el curso no existe
     */
    @PostMapping("/{codigoCurso}/secciones")
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

    /**
     * Actualiza los datos de una sección existente (número, horario y cupos disponibles).
     *
     * @param idSeccion        identificador de la sección a actualizar
     * @param seccionDetalles datos actualizados de la sección
     * @return respuesta HTTP {@code 200 OK} con la sección actualizada, o {@code 404 Not Found} si no existe
     */
    @PutMapping("/secciones/{idSeccion}")
    public ResponseEntity<Seccion> actualizarSeccion(
            @PathVariable Long idSeccion,
            @RequestBody Seccion seccionDetalles) {

        Optional<Seccion> seccionOpt =
                seccionRepository.findById(idSeccion);

        if (seccionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Seccion seccion = seccionOpt.get();

        seccion.setNumeroSeccion(
                seccionDetalles.getNumeroSeccion()
        );
        seccion.setHorario(
                seccionDetalles.getHorario()
        );
        seccion.setCuposDisponibles(
                seccionDetalles.getCuposDisponibles()
        );

        Seccion actualizada =
                seccionRepository.save(seccion);

        return ResponseEntity.ok(actualizada);
    }
}