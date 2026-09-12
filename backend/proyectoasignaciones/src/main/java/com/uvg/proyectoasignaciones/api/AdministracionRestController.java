package com.uvg.proyectoasignaciones.api;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uvg.proyectoasignaciones.controller.NotificacionController;
import com.uvg.proyectoasignaciones.model.Curso;
import com.uvg.proyectoasignaciones.model.Seccion;
import com.uvg.proyectoasignaciones.model.Seguimiento;
import com.uvg.proyectoasignaciones.repository.CursoRepository;
import com.uvg.proyectoasignaciones.repository.SeguimientoRepository;

@CrossOrigin(origins = {
        "http://127.0.0.1:5500",
        "http://localhost:5500"
})
@RestController
@RequestMapping("/api/administracion")
public class AdministracionRestController {

    private final CursoRepository cursoRepository;
    private final SeguimientoRepository seguimientoRepository;
    private final NotificacionController notificacionController;

    public AdministracionRestController(
            CursoRepository cursoRepository,
            SeguimientoRepository seguimientoRepository,
            NotificacionController notificacionController) {

        this.cursoRepository = cursoRepository;
        this.seguimientoRepository = seguimientoRepository;
        this.notificacionController = notificacionController;
    }

    // RF-07: CAMBIAR ESTADO DE UNA SECCION Y NOTIFICAR
    @PatchMapping(
            "/cursos/{codigoCurso}/secciones/{numeroSeccion}/estado"
    )
    public ResponseEntity<?> actualizarEstadoSeccion(
            @PathVariable String codigoCurso,
            @PathVariable int numeroSeccion,
            @RequestBody SolicitudEstado solicitud) {

        // VALIDAR NUEVO ESTADO
        if (
                solicitud.getEstado() == null ||
                solicitud.getEstado().isBlank()
        ) {

            return ResponseEntity
                    .badRequest()
                    .body("El estado es obligatorio");
        }

        // BUSCAR CURSO
        Optional<Curso> cursoOptional =
                cursoRepository.findById(codigoCurso);

        if (cursoOptional.isEmpty()) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        Curso curso = cursoOptional.get();

        // BUSCAR SECCION
        Seccion seccionEncontrada = null;

        for (Seccion seccion : curso.getSecciones()) {

            if (
                    seccion.getNumeroSeccion()
                            == numeroSeccion
            ) {

                seccionEncontrada = seccion;
                break;
            }
        }

        if (seccionEncontrada == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        // ACTUALIZAR ESTADO
        String nuevoEstado =
                solicitud.getEstado().trim();

        seccionEncontrada.actualizarEstado(
                nuevoEstado
        );

        // GUARDAR CAMBIO EN SQLITE
        cursoRepository.save(curso);

        // BUSCAR QUIENES SIGUEN ESTA SECCION
        List<Seguimiento> seguimientos =
                seguimientoRepository
                        .findBySeccionIdSeccionAndActivoTrue(
                                seccionEncontrada.getIdSeccion()
                        );

        // GENERAR NOTIFICACION PARA CADA ESTUDIANTE
        for (Seguimiento seguimiento : seguimientos) {

            notificacionController.crearNotificacion(
                    seguimiento.getEstudiante(),
                    seccionEncontrada,
                    "El estado de la sección "
                            + numeroSeccion
                            + " del curso "
                            + codigoCurso
                            + " cambió a: "
                            + nuevoEstado
            );
        }

        return ResponseEntity.ok(
                seccionEncontrada
        );
    }

    // DTO PARA RECIBIR EL NUEVO ESTADO
    public static class SolicitudEstado {

        private String estado;

        public SolicitudEstado() {
        }

        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }
    }
}
