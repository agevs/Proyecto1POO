package com.uvg.proyectoasignaciones.service;

import java.util.List;
import java.util.Set;

public class EvaluadorAdelanto {

    public record Resultado(
            boolean elegible,
            String mensaje
    ) {}

    public Resultado evaluar(
            double promedio,
            int anioEstudiante,
            int anioCurso,
            Set<String> cursosAprobados,
            List<String> prerrequisitos) {

        if (!Double.isFinite(promedio)
                || promedio < 0 || promedio > 100
                || anioEstudiante < 1 || anioEstudiante > 5
                || anioCurso < 1 || anioCurso > 5
                || cursosAprobados == null
                || prerrequisitos == null) {

            return new Resultado(
                    false, "Datos academicos invalidos o incompletos"
            );
        }

        if (promedio < 85) {
            return new Resultado(
                    false, "Promedio insuficiente. Minimo requerido: 85"
            );
        }

        if (anioCurso <= anioEstudiante) {
            return new Resultado(
                    false, "El curso no pertenece a un anio posterior"
            );
        }

        for (String requisito : prerrequisitos) {
            if (requisito == null || requisito.isBlank()
                    || !cursosAprobados.contains(requisito)) {

                return new Resultado(
                        false, "Falta aprobar un prerrequisito"
                );
            }
        }

        return new Resultado(
                true, "Cumple las condiciones para solicitar adelanto"
        );
    }
}
