package com.uvg.proyectoasignaciones.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.uvg.proyectoasignaciones.model.Estudiante;
import com.uvg.proyectoasignaciones.model.Notificacion;
import com.uvg.proyectoasignaciones.repository.NotificacionRepository;
import com.uvg.proyectoasignaciones.service.correo.ServicioCorreo;

@ExtendWith(MockitoExtension.class)
class NotificacionControllerTests {

    @Mock
    private NotificacionRepository repository;

    @Mock
    private ServicioCorreo servicioCorreo;

    private NotificacionController controller;
    private Estudiante estudiante;

    @BeforeEach
    void prepararPrueba() {
        controller = new NotificacionController(repository, servicioCorreo);
        estudiante = new Estudiante(1, "Estudiante", "estudiante@uvg.edu.gt", "1234", "26594");
    }

    @Test
    void obtieneNotificacionesDelEstudianteActual() {
        Notificacion notificacion = new Notificacion("26594", "Cambio de horario", "2026-09-11T10:00", null);
        when(repository.findByEstudianteCarneOrderByIdNotificacionDesc("26594"))
                .thenReturn(List.of(notificacion));

        List<Notificacion> resultado = controller.obtenerNotificaciones(estudiante);

        assertEquals(1, resultado.size());
        assertEquals("Cambio de horario", resultado.get(0).getMensaje());
    }

    @Test
    void filtraNotificacionesNoLeidas() {
        Notificacion pendiente = new Notificacion("26594", "Nuevo cupo", "2026-09-11T10:00", null);
        when(repository.findByEstudianteCarneAndLeidaFalseOrderByIdNotificacionDesc("26594"))
                .thenReturn(List.of(pendiente));

        List<Notificacion> resultado = controller.obtenerNotificacionesNoLeidas(estudiante);

        assertEquals(List.of(pendiente), resultado);
        assertFalse(resultado.get(0).isLeida());
    }

    @Test
    void marcaComoLeidaSoloUnaNotificacionPropia() {
        Notificacion notificacion = new Notificacion("26594", "Nuevo cupo", "2026-09-11T10:00", null);
        when(repository.findById(7)).thenReturn(Optional.of(notificacion));
        when(repository.save(notificacion)).thenReturn(notificacion);

        Optional<Notificacion> resultado = controller.marcarComoLeida(7, estudiante);

        assertTrue(resultado.isPresent());
        assertTrue(resultado.get().isLeida());
        verify(repository).save(notificacion);
    }

    @Test
    void rechazaMensajesVacios() {
        assertThrows(
                IllegalArgumentException.class,
                () -> controller.crearNotificacion(estudiante, null, "   "));
    }

    @Test
    void guardaAlertaAntesDeSolicitarCorreo() {
        Notificacion guardada = new Notificacion(
                "26594", "Cambio de estado", "2026-11-04T18:00", null
        );

        when(repository.save(
                org.mockito.ArgumentMatchers.any(Notificacion.class)
        )).thenReturn(guardada);

        Notificacion resultado = controller.crearNotificacion(
                estudiante, null, "Cambio de estado"
        );

        var orden = inOrder(repository, servicioCorreo);

        orden.verify(repository).save(
                org.mockito.ArgumentMatchers.any(Notificacion.class)
        );

        orden.verify(servicioCorreo).enviarNotificacion(
                "estudiante@uvg.edu.gt",
                "Notificacion academica - Proyecto POO UVG",
                "Cambio de estado"
        );

        assertEquals(guardada, resultado);
    }

    @Test
    void conservaAlertaCuandoFallaCorreo() {
        Notificacion guardada = new Notificacion(
                "26594", "Cambio de estado", "2026-11-04T18:00", null
        );

        when(repository.save(
                org.mockito.ArgumentMatchers.any(Notificacion.class)
        )).thenReturn(guardada);

        org.mockito.Mockito.doThrow(
                new IllegalStateException("Servicio no disponible")
        ).when(servicioCorreo).enviarNotificacion(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString()
        );

        Notificacion resultado = controller.crearNotificacion(
                estudiante, null, "Cambio de estado"
        );

        assertEquals(guardada, resultado);
        verify(repository).save(
                org.mockito.ArgumentMatchers.any(Notificacion.class)
        );
    }
}
