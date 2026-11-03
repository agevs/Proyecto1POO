
package com.uvg.proyectoasignaciones.api;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.uvg.proyectoasignaciones.service.AutorizacionEstudianteService;

@RestController
@RequestMapping("/api/seguimientos")
public class SeguimientoRestController {

    private final SeguimientoRepository seguimientoRepository;
    private final EstudianteRepository estudianteRepository;
    private final CursoRepository cursoRepository;
    private final AutorizacionEstudianteService autorizacionEstudianteService;

    public SeguimientoRestController(
            SeguimientoRepository seguimientoRepository,
            EstudianteRepository estudianteRepository,
            CursoRepository cursoRepository,
            AutorizacionEstudianteService autorizacionEstudianteService) {

        this.seguimientoRepository = seguimientoRepository;
        this.estudianteRepository = estudianteRepository;
        this.cursoRepository = cursoRepository;
        this.autorizacionEstudianteService = autorizacionEstudianteService;
    }

    // RF-04: CREAR O REACTIVAR SEGUIMIENTO
    @PostMapping
    public ResponseEntity<?> crearSeguimiento(
            @RequestBody SolicitudSeguimiento solicitud) {

        if (solicitud == null) {
            return ResponseEntity.badRequest()
                    .body("Solicitud obligatoria");
        }

        String carne = String.valueOf(
                solicitud.getCarnetEstudiante()
        );

        // RF-01: Verificar identidad antes de modificar datos.
        if (!autorizacionEstudianteService.puedeAccederACarne(carne)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No autorizado para modificar seguimientos de este estudiante");
        }

        Optional<Estudiante> estudianteOptional =
                estudianteRepository.findByCarne(carne);

        if (estudianteOptional.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Estudiante no encontrado");
        }

        Optional<Curso> cursoOptional =
                cursoRepository.findById(
                        solicitud.getCodigoCurso()
                );

        if (cursoOptional.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Curso no encontrado");
        }

        Curso curso = cursoOptional.get();

        Seccion seccionEncontrada = curso
                .getSecciones()
                .stream()
                .filter(seccion ->
                        seccion.getNumeroSeccion()
                                == solicitud.getSeccion()
                )
                .findFirst()
                .orElse(null);

        if (seccionEncontrada == null) {
            return ResponseEntity.badRequest()
                    .body("Sección no encontrada");
        }

        Long idSeccion = seccionEncontrada.getIdSeccion();

        // Buscar seguimientos activos, tolerando duplicados.
        List<Seguimiento> seguimientosActivos =
                seguimientoRepository
                        .findByEstudianteCarneAndSeccionIdSeccionAndActivoTrue(
                                carne,
                                idSeccion
                        );

        if (!seguimientosActivos.isEmpty()) {

            Seguimiento seguimientoPrincipal =
                    seguimientosActivos.stream()
                            .max((a, b) ->
                                    Integer.compare(
                                            a.getIdSeguimiento(),
                                            b.getIdSeguimiento()
                                    )
                            )
                            .orElse(seguimientosActivos.get(0));

            // Conservar el más reciente y desactivar duplicados.
            for (Seguimiento seguimiento : seguimientosActivos) {

                if (seguimiento.getIdSeguimiento()
                        != seguimientoPrincipal.getIdSeguimiento()) {

                    seguimiento.desactivar();
                    seguimientoRepository.save(seguimiento);
                }
            }

            return ResponseEntity.ok(seguimientoPrincipal);
        }

        // Buscar seguimientos históricos.
        List<Seguimiento> seguimientosAnteriores =
                seguimientoRepository
                        .findByEstudianteCarneAndSeccionIdSeccionOrderByIdSeguimientoDesc(
                                carne,
                                idSeccion
                        );

        if (!seguimientosAnteriores.isEmpty()) {

            Seguimiento seguimientoExistente =
                    seguimientosAnteriores.get(0);

            // Mantener inactivos los registros históricos restantes.
            for (int i = 1;
                    i < seguimientosAnteriores.size();
                    i++) {

                Seguimiento seguimientoAnterior =
                        seguimientosAnteriores.get(i);

                if (seguimientoAnterior.isActivo()) {
                    seguimientoAnterior.desactivar();
                    seguimientoRepository.save(seguimientoAnterior);
                }
            }

            seguimientoExistente.activar();

            Seguimiento seguimientoReactivado =
                    seguimientoRepository.save(
                            seguimientoExistente
                    );

            return ResponseEntity.ok(seguimientoReactivado);
        }

        // Crear seguimiento nuevo.
        Seguimiento nuevoSeguimiento =
                new Seguimiento(
                        0,
                        estudianteOptional.get(),
                        seccionEncontrada
                );

        Seguimiento seguimientoGuardado =
                seguimientoRepository.save(
                        nuevoSeguimiento
                );

        return ResponseEntity.ok(seguimientoGuardado);
    }

    // RF-05: DESACTIVAR SEGUIMIENTO
    @PatchMapping("/desactivar")
    public ResponseEntity<?> desactivarSeguimiento(
            @RequestBody SolicitudSeguimiento solicitud) {

        if (solicitud == null) {
            return ResponseEntity.badRequest()
                    .body("Solicitud obligatoria");
        }

        String carne = String.valueOf(
                solicitud.getCarnetEstudiante()
        );

        // RF-01: Verificar identidad antes de modificar datos.
        if (!autorizacionEstudianteService.puedeAccederACarne(carne)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No autorizado para modificar seguimientos de este estudiante");
        }

        Optional<Estudiante> estudianteOptional =
                estudianteRepository.findByCarne(carne);

        if (estudianteOptional.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Estudiante no encontrado");
        }

        Optional<Curso> cursoOptional =
                cursoRepository.findById(
                        solicitud.getCodigoCurso()
                );

        if (cursoOptional.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Curso no encontrado");
        }

        Seccion seccionEncontrada =
                cursoOptional.get()
                        .getSecciones()
                        .stream()
                        .filter(seccion ->
                                seccion.getNumeroSeccion()
                                        == solicitud.getSeccion()
                        )
                        .findFirst()
                        .orElse(null);

        if (seccionEncontrada == null) {
            return ResponseEntity.badRequest()
                    .body("Sección no encontrada");
        }

        // Buscar todos los seguimientos activos.
        List<Seguimiento> seguimientosActivos =
                seguimientoRepository
                        .findByEstudianteCarneAndSeccionIdSeccionAndActivoTrue(
                                carne,
                                seccionEncontrada.getIdSeccion()
                        );

        if (seguimientosActivos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Seleccionar el seguimiento activo más reciente.
        Seguimiento seguimientoPrincipal =
                seguimientosActivos.stream()
                        .max((a, b) ->
                                Integer.compare(
                                        a.getIdSeguimiento(),
                                        b.getIdSeguimiento()
                                )
                        )
                        .orElse(seguimientosActivos.get(0));

        // Desactivar todos los registros activos duplicados.
        for (Seguimiento seguimiento : seguimientosActivos) {
            seguimiento.desactivar();
            seguimientoRepository.save(seguimiento);
        }

        return ResponseEntity.ok(seguimientoPrincipal);
    }

    // RF-06: CONSULTA GLOBAL DE SEGUIMIENTOS
    // Temporalmente restringida a administradores.
    @GetMapping
    public ResponseEntity<?> obtenerTodosLosSeguimientosActivos() {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Consulta global no disponible para estudiantes");
    }

    // RF-06: CONSULTAR SEGUIMIENTOS PROPIOS
    @GetMapping("/estudiante/{carne}")
    public ResponseEntity<?> obtenerSeguimientosPorEstudiante(
            @PathVariable String carne) {

        // RF-01: Impedir consultar seguimientos ajenos.
        if (!autorizacionEstudianteService.puedeAccederACarne(carne)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No autorizado para consultar seguimientos de este estudiante");
        }

        return ResponseEntity.ok(
                seguimientoRepository
                        .findByEstudianteCarneAndActivoTrue(carne)
        );
    }

    // DTO PARA RECIBIR JSON DEL FRONTEND
    public static class SolicitudSeguimiento {

        private int carnetEstudiante;
        private String codigoCurso;
        private int seccion;

        public SolicitudSeguimiento() {
        }

        public int getCarnetEstudiante() {
            return carnetEstudiante;
        }

        public void setCarnetEstudiante(int carnetEstudiante) {
            this.carnetEstudiante = carnetEstudiante;
        }

        public String getCodigoCurso() {
            return codigoCurso;
        }

        public void setCodigoCurso(String codigoCurso) {
            this.codigoCurso = codigoCurso;
        }

        public int getSeccion() {
            return seccion;
        }

        public void setSeccion(int seccion) {
            this.seccion = seccion;
        }
    }
}
