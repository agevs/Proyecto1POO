package com.uvg.proyectoasignaciones.api;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uvg.proyectoasignaciones.model.Curso;
import com.uvg.proyectoasignaciones.model.Estudiante;
import com.uvg.proyectoasignaciones.model.Seccion;
import com.uvg.proyectoasignaciones.model.Seguimiento;
import com.uvg.proyectoasignaciones.repository.CursoRepository;
import com.uvg.proyectoasignaciones.repository.EstudianteRepository;
import com.uvg.proyectoasignaciones.repository.SeguimientoRepository;

@CrossOrigin(origins = {
        "http://127.0.0.1:5500",
        "http://localhost:5500"
})
@RestController
@RequestMapping("/api/seguimientos")
public class SeguimientoRestController {

    private final SeguimientoRepository seguimientoRepository;
    private final EstudianteRepository estudianteRepository;
    private final CursoRepository cursoRepository;

    public SeguimientoRestController(
            SeguimientoRepository seguimientoRepository,
            EstudianteRepository estudianteRepository,
            CursoRepository cursoRepository) {

        this.seguimientoRepository = seguimientoRepository;
        this.estudianteRepository = estudianteRepository;
        this.cursoRepository = cursoRepository;
    }

    // RF-04: CREAR / ACTIVAR SEGUIMIENTO
    @PostMapping
    public ResponseEntity<?> crearSeguimiento(
            @RequestBody SolicitudSeguimiento solicitud) {

        String carne = String.valueOf(
                solicitud.getCarnetEstudiante()
        );

        // Buscar estudiante
        Optional<Estudiante> estudianteOptional =
                estudianteRepository.findByCarne(carne);

        if (estudianteOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Estudiante no encontrado");
        }

        // Buscar curso
        Optional<Curso> cursoOptional =
                cursoRepository.findById(
                        solicitud.getCodigoCurso()
                );

        if (cursoOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Curso no encontrado");
        }

        Curso curso = cursoOptional.get();

        // Buscar sección dentro del curso
        Seccion seccionEncontrada = null;

        for (Seccion seccion : curso.getSecciones()) {

            if (seccion.getNumeroSeccion()
                    == solicitud.getSeccion()) {

                seccionEncontrada = seccion;
                break;
            }
        }

        if (seccionEncontrada == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Sección no encontrada");
        }

        // EVITAR SEGUIMIENTOS DUPLICADOS
        Optional<Seguimiento> seguimientoExistente =
                seguimientoRepository
                        .findByEstudianteCarneAndSeccionIdSeccionAndActivoTrue(
                                carne,
                                seccionEncontrada.getIdSeccion()
                        );

        if (seguimientoExistente.isPresent()) {

            return ResponseEntity.ok(
                    seguimientoExistente.get()
            );
        }

        // Crear nuevo seguimiento
        Seguimiento seguimiento =
                new Seguimiento(
                        0,
                        estudianteOptional.get(),
                        seccionEncontrada
                );

        // Guardar en SQLite
        Seguimiento seguimientoGuardado =
                seguimientoRepository.save(
                        seguimiento
                );

        return ResponseEntity.ok(
                seguimientoGuardado
        );
    }

    @PatchMapping("/desactivar")
    public ResponseEntity<?> desactivarSeguimiento(
            @RequestBody SolicitudSeguimiento solicitud) {

        String carne = String.valueOf(
                solicitud.getCarnetEstudiante()
        );

        Optional<Estudiante> estudianteOptional =
                estudianteRepository.findByCarne(carne);

        if (estudianteOptional.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Estudiante no encontrado");
        }

        Optional<Curso> cursoOptional =
                cursoRepository.findById(solicitud.getCodigoCurso());

        if (cursoOptional.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Curso no encontrado");
        }

        Seccion seccionEncontrada = cursoOptional.get()
                .getSecciones()
                .stream()
                .filter(seccion -> seccion.getNumeroSeccion()
                        == solicitud.getSeccion())
                .findFirst()
                .orElse(null);

        if (seccionEncontrada == null) {
            return ResponseEntity.badRequest()
                    .body("Sección no encontrada");
        }

        Optional<Seguimiento> seguimientoOptional =
                seguimientoRepository
                        .findByEstudianteCarneAndSeccionIdSeccionAndActivoTrue(
                                carne,
                                seccionEncontrada.getIdSeccion()
                        );

        if (seguimientoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Seguimiento seguimiento = seguimientoOptional.get();
        seguimiento.desactivar();

        return ResponseEntity.ok(
                seguimientoRepository.save(seguimiento)
        );
    }

    // RF-06: OBTENER TODOS LOS SEGUIMIENTOS ACTIVOS
    @GetMapping
    public List<Seguimiento>
            obtenerTodosLosSeguimientosActivos() {

        return seguimientoRepository
                .findByActivoTrue();
    }

    // RF-06: OBTENER SEGUIMIENTOS ACTIVOS DE UN ESTUDIANTE
    @GetMapping("/estudiante/{carne}")
    public List<Seguimiento>
            obtenerSeguimientosPorEstudiante(
                    @PathVariable String carne) {

        return seguimientoRepository
                .findByEstudianteCarneAndActivoTrue(
                        carne
                );
    }

    // DTO PARA RECIBIR EL JSON DEL FRONTEND
    public static class SolicitudSeguimiento {

        private int carnetEstudiante;
        private String codigoCurso;
        private int seccion;

        public SolicitudSeguimiento() {
        }

        public int getCarnetEstudiante() {
            return carnetEstudiante;
        }

        public void setCarnetEstudiante(
                int carnetEstudiante) {

            this.carnetEstudiante =
                    carnetEstudiante;
        }

        public String getCodigoCurso() {
            return codigoCurso;
        }

        public void setCodigoCurso(
                String codigoCurso) {

            this.codigoCurso =
                    codigoCurso;
        }

        public int getSeccion() {
            return seccion;
        }

        public void setSeccion(
                int seccion) {

            this.seccion = seccion;
        }
    }
}