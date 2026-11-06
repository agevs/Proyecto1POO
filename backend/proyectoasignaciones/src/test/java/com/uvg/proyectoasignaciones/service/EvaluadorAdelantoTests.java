package com.uvg.proyectoasignaciones.service;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvaluadorAdelantoTests {

    private final EvaluadorAdelanto evaluador =
            new EvaluadorAdelanto();

    @Test
    void permiteAdelantoConPromedio90YRequisitos() {
        var resultado = evaluador.evaluar(
                90, 2, 3,
                Set.of("CC2016"),
                List.of("CC2016")
        );

        assertTrue(resultado.elegible());
    }

    @Test
    void rechazaPromedioInferiorA85() {
        var resultado = evaluador.evaluar(
                84, 2, 3,
                Set.of("CC2016"),
                List.of("CC2016")
        );

        assertFalse(resultado.elegible());
    }

    @Test
    void rechazaPrerrequisitoPendiente() {
        var resultado = evaluador.evaluar(
                95, 2, 3,
                Set.of(),
                List.of("CC2016")
        );

        assertFalse(resultado.elegible());
    }

    @Test
    void permitePromedioExactamente85() {
        var resultado = evaluador.evaluar(
                85, 2, 3,
                Set.of("CC2016"),
                List.of("CC2016")
        );

        assertTrue(resultado.elegible());
    }

    @Test
    void rechazaCursoDelMismoAnio() {
        var resultado = evaluador.evaluar(
                95, 2, 2,
                Set.of(),
                List.of()
        );

        assertFalse(resultado.elegible());
    }

    @Test
    void rechazaHistorialDesconocido() {
        var resultado = evaluador.evaluar(
                95, 2, 3,
                null,
                List.of("CC2016")
        );

        assertFalse(resultado.elegible());
    }
}
