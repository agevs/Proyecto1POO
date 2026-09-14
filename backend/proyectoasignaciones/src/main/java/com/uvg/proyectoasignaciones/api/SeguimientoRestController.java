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

    // RF-04: CREAR O REACTIVAR SEGUIMIENTO
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
            return ResponseEntity
                    .badRequest()
                    .body("Sección no encontrada");
        }

        Long idSeccion =
                seccionEncontrada.getIdSeccion();

        /*
         * Buscar todos los seguimientos activos.
         *
         * Se usa List para que una base antigua con
         * duplicados no provoque NonUniqueResultException.
         */
        List<Seguimiento> seguimientosActivos =
                seguimientoRepository
                        .findByEstudianteCarneAndSeccionIdSeccionAndActivoTrue(
                                carne,
                                idSeccion
                        );

        /*
         * Si existe uno activo, no crear otro.
         *
         * Si por datos históricos existen varios activos,
         * conservamos el más reciente y desactivamos
         * los demás.
         */
        if (!seguimientosActivos.isEmpty()) {

            Seguimiento seguimientoPrincipal =
                    seguimientosActivos
                            .stream()
                            .max((a, b) ->
                                    Integer.compare(
                                            a.getIdSeguimiento(),
                                            b.getIdSeguimiento()
                                    )
                            )
                            .orElse(seguimientosActivos.get(0));

            for (Seguimiento seguimiento :
                    seguimientosActivos) {

                if (seguimiento.getIdSeguimiento()
                        != seguimientoPrincipal.getIdSeguimiento()) {

                    seguimiento.desactivar();

                    seguimientoRepository.save(
                            seguimiento
                    );
                }
            }

            return ResponseEntity.ok(
                    seguimientoPrincipal
            );
        }

        /*
         * No existe ningún seguimiento activo.
         *
         * Buscar seguimientos históricos ordenados
         * desde el más reciente hasta el más antiguo.
         */
        List<Seguimiento> seguimientosAnteriores =
                seguimientoRepository
                        .findByEstudianteCarneAndSeccionIdSeccionOrderByIdSeguimientoDesc(
                                carne,
                                idSeccion
                        );

        /*
         * Si anteriormente ya se siguió esta sección,
         * reactivar el registro más reciente.
         */
        if (!seguimientosAnteriores.isEmpty()) {

            Seguimiento seguimientoExistente =
                    seguimientosAnteriores.get(0);

            /*
             * Asegurar que cualquier otro registro histórico
             * permanezca inactivo.
             */
            for (int i = 1;
                    i < seguimientosAnteriores.size();
                    i++) {

                Seguimiento seguimientoAnterior =
                        seguimientosAnteriores.get(i);

                if (seguimientoAnterior.isActivo()) {
                    seguimientoAnterior.desactivar();

                    seguimientoRepository.save(
                            seguimientoAnterior
                    );
                }
            }

            seguimientoExistente.activar();

            Seguimiento seguimientoReactivado =
                    seguimientoRepository.save(
                            seguimientoExistente
                    );

            return ResponseEntity.ok(
                    seguimientoReactivado
            );
        }

        /*
         * Si nunca existió un seguimiento para esta
         * estudiante y sección, crear uno nuevo.
         */
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

        return ResponseEntity.ok(
                seguimientoGuardado
        );
    }

    // RF-05: DESACTIVAR SEGUIMIENTO
    @PatchMapping("/desactivar")
    public ResponseEntity<?> desactivarSeguimiento(
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

        // Buscar sección dentro del curso
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
            return ResponseEntity
                    .badRequest()
                    .body("Sección no encontrada");
        }

        /*
         * Buscar todos los seguimientos activos.
         *
         * Normalmente existirá uno solo, pero List
         * permite tolerar datos antiguos duplicados.
         */
        List<Seguimiento> seguimientosActivos =
                seguimientoRepository
                        .findByEstudianteCarneAndSeccionIdSeccionAndActivoTrue(
                                carne,
                                seccionEncontrada.getIdSeccion()
                        );

        if (seguimientosActivos.isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        /*
         * Tomamos como principal el seguimiento
         * activo más reciente.
         */
        Seguimiento seguimientoPrincipal =
                seguimientosActivos
                        .stream()
                        .max((a, b) ->
                                Integer.compare(
                                        a.getIdSeguimiento(),
                                        b.getIdSeguimiento()
                                )
                        )
                        .orElse(seguimientosActivos.get(0));

        /*
         * Desactivar todos los seguimientos activos
         * encontrados.
         *
         * Esto también limpia lógicamente una base
         * antigua que tenga duplicados activos.
         */
        for (Seguimiento seguimiento :
                seguimientosActivos) {

            seguimiento.desactivar();

            seguimientoRepository.save(
                    seguimiento
            );
        }

        return ResponseEntity.ok(
                seguimientoPrincipal
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

            this.seccion =
                    seccion;
        }
    }
}