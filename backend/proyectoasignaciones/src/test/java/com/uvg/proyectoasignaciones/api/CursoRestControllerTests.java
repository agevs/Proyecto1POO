package com.uvg.proyectoasignaciones.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.uvg.proyectoasignaciones.model.Seccion;
import com.uvg.proyectoasignaciones.repository.CursoRepository;
import com.uvg.proyectoasignaciones.repository.SeccionRepository;

@ExtendWith(MockitoExtension.class)
class CursoRestControllerTests {

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private SeccionRepository seccionRepository;

    @Test
    void actualizarSeccionConservaEstadoActual() {

        CursoRestController controller =
                new CursoRestController(
                        cursoRepository,
                        seccionRepository
                );

        Seccion existente = new Seccion(
                10,
                "Lu 10:00",
                25,
                "Pendiente"
        );

        existente.setIdSeccion(1L);

        Seccion nuevosDatos = new Seccion(
                10,
                "Lu 11:00",
                26,
                "Confirmado"
        );

        when(seccionRepository.findById(1L))
                .thenReturn(Optional.of(existente));

        when(seccionRepository.save(existente))
                .thenReturn(existente);

        ResponseEntity<Seccion> respuesta =
                controller.actualizarSeccion(
                        1L,
                        nuevosDatos
                );

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());

        assertEquals("Pendiente", existente.getEstado());
        assertEquals("Lu 11:00", existente.getHorario());
        assertEquals(26, existente.getCuposDisponibles());

        verify(seccionRepository).save(existente);
    }
}
