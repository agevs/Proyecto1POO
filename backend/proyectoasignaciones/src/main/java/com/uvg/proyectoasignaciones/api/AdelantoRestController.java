package com.uvg.proyectoasignaciones.api;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uvg.proyectoasignaciones.service.EvaluadorAdelanto;

@RestController
@RequestMapping("/api/adelantos")
public class AdelantoRestController {

    private final EvaluadorAdelanto evaluador =
            new EvaluadorAdelanto();

    public record SolicitudEvaluacion(
            double promedio,
            int anioEstudiante,
            int anioCurso,
            Set<String> cursosAprobados,
            List<String> prerrequisitos
    ) {}

    @PostMapping("/evaluar")
    public ResponseEntity<EvaluadorAdelanto.Resultado> evaluar(
            @RequestBody SolicitudEvaluacion solicitud) {

        if (solicitud == null) {
            return ResponseEntity.badRequest().build();
        }

        var resultado = evaluador.evaluar(
                solicitud.promedio(),
                solicitud.anioEstudiante(),
                solicitud.anioCurso(),
                solicitud.cursosAprobados(),
                solicitud.prerrequisitos()
        );

        return ResponseEntity.ok(resultado);
    }
}
