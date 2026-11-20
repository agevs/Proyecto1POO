package com.uvg.proyectoasignaciones.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.uvg.proyectoasignaciones.controller.NotificacionController;
import com.uvg.proyectoasignaciones.model.Curso;
import com.uvg.proyectoasignaciones.model.Seccion;
import com.uvg.proyectoasignaciones.model.Estudiante;
import com.uvg.proyectoasignaciones.model.Seguimiento;
import com.uvg.proyectoasignaciones.repository.CursoRepository;
import com.uvg.proyectoasignaciones.repository.SeguimientoRepository;

/**
 * Pruebas unitarias para verificar el comportamiento de
 * {@link AdministracionRestController} al actualizar estados de secciones,
 * validar entradas y notificar a estudiantes con seguimiento activo.
 */
@ExtendWith(MockitoExtension.class)
class AdministracionRestControllerTests {

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private SeguimientoRepository seguimientoRepository;

    @Mock
    private NotificacionController notificacionController;

    private AdministracionRestController controller;
    private Curso curso;
    private Seccion seccion;

    /**
     * Inicializa el controlador con dependencias simuladas y crea un curso con
     * una sección base en estado pendiente antes de cada prueba.
     */
    @BeforeEach
    void preparar() {
        controller = new AdministracionRestController(
                cursoRepository,
                seguimientoRepository,
                notificacionController
        );

        curso = new Curso("CC2005", "Programacion", 4);
        seccion = new Seccion(10, "Lu 08:00", 25, "Pendiente");
        seccion.setIdSeccion(1L);
        curso.agregarSeccion(seccion);
    }

    /**
     * Construye una solicitud de actualización de estado con el valor indicado.
     *
     * @param estado nuevo estado a asignar en la solicitud
     * @return instancia de {@link AdministracionRestController.SolicitudEstado}
     */
    private AdministracionRestController.SolicitudEstado solicitud(
            String estado) {
        AdministracionRestController.SolicitudEstado solicitud =
                new AdministracionRestController.SolicitudEstado();
        solicitud.setEstado(estado);
        return solicitud;
    }

    /**
     * Verifica que se rechace una solicitud con estado vacío devolviendo {@code 400 Bad Request}
     * sin consultar ni modificar los repositorios o notificaciones.
     */
    @Test
    void rechazaEstadoVacio() {
        ResponseEntity<?> respuesta =
                controller.actualizarEstadoSeccion(
                        "CC2005", 10, solicitud(""));

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        verifyNoInteractions(cursoRepository, seguimientoRepository,
                notificacionController);
    }

    /**
     * Verifica que se rechace un estado no permitido devolviendo {@code 400 Bad Request}
     * y manteniendo el estado original de la sección sin guardar cambios.
     */
    @Test
    void rechazaEstadoNoPermitido() {
        when(cursoRepository.findById("CC2005"))
                .thenReturn(Optional.of(curso));

        ResponseEntity<?> respuesta =
                controller.actualizarEstadoSeccion(
                        "CC2005", 10, solicitud("Invalido"));

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals("Pendiente", seccion.getEstado());
        verify(cursoRepository, never()).save(any());
        verifyNoInteractions(seguimientoRepository, notificacionController);
    }

    /**
     * Verifica que se devuelva {@code 404 Not Found} cuando el curso indicado no existe.
     */
    @Test
    void rechazaCursoInexistente() {
        when(cursoRepository.findById("NOEXISTE"))
                .thenReturn(Optional.empty());

        ResponseEntity<?> respuesta =
                controller.actualizarEstadoSeccion(
                        "NOEXISTE", 10, solicitud("Confirmado"));

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
    }

    /**
     * Verifica que se devuelva {@code 404 Not Found} cuando la sección no existe dentro
     * del curso consultado y no se guarde el curso.
     */
    @Test
    void rechazaSeccionInexistente() {
        when(cursoRepository.findById("CC2005"))
                .thenReturn(Optional.of(curso));

        ResponseEntity<?> respuesta =
                controller.actualizarEstadoSeccion(
                        "CC2005", 99, solicitud("Confirmado"));

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        verify(cursoRepository, never()).save(any());
    }

    /**
     * Verifica que si el estado enviado es igual al estado actual no se guarde
     * ni se envíen notificaciones.
     */
    @Test
    void noNotificaSiEstadoNoCambia() {
        when(cursoRepository.findById("CC2005"))
                .thenReturn(Optional.of(curso));

        ResponseEntity<?> respuesta =
                controller.actualizarEstadoSeccion(
                        "CC2005", 10, solicitud("Pendiente"));

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        verify(cursoRepository, never()).save(any());
        verifyNoInteractions(seguimientoRepository, notificacionController);
    }

    /**
     * Verifica que al cambiar a un estado válido se actualice la sección, se guarde el curso
     * y se notifique a los estudiantes con seguimiento activo.
     */
    @Test
    void actualizaEstadoYNotificaSeguidores() {
        Estudiante estudiante = new Estudiante(
                1, "Estudiante", "estudiante@uvg.edu.gt",
                "hash", "26594"
        );

        Seguimiento seguimiento =
                new Seguimiento(1, estudiante, seccion);

        when(cursoRepository.findById("CC2005"))
                .thenReturn(Optional.of(curso));

        when(seguimientoRepository
                .findBySeccionIdSeccionAndActivoTrue(1L))
                .thenReturn(List.of(seguimiento));

        ResponseEntity<?> respuesta =
                controller.actualizarEstadoSeccion(
                        "CC2005", 10, solicitud("Confirmado"));

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("Confirmado", seccion.getEstado());

        verify(cursoRepository).save(curso);
        verify(notificacionController).crearNotificacion(
                eq(estudiante),
                eq(seccion),
                contains("Confirmado")
        );
    }
}
