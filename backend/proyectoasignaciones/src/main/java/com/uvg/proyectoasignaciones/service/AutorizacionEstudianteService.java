
package com.uvg.proyectoasignaciones.service;

import com.uvg.proyectoasignaciones.model.Estudiante;
import com.uvg.proyectoasignaciones.repository.EstudianteRepository;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AutorizacionEstudianteService {

    private final EstudianteRepository estudianteRepository;

    public AutorizacionEstudianteService(
            EstudianteRepository estudianteRepository) {

        this.estudianteRepository = estudianteRepository;
    }

    // Obtener al estudiante identificado por Spring Security.
    public Optional<Estudiante> obtenerEstudianteAutenticado() {

        Authentication autenticacion =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        if (autenticacion == null
                || !autenticacion.isAuthenticated()) {
            return Optional.empty();
        }

        boolean esEstudiante = autenticacion.getAuthorities()
                .stream()
                .anyMatch(autoridad ->
                        "ROLE_ESTUDIANTE".equals(
                                autoridad.getAuthority()
                        )
                );

        if (!esEstudiante) {
            return Optional.empty();
        }

        return estudianteRepository.findByCorreo(
                autenticacion.getName()
        );
    }

    // Comprobar que el carne solicitado pertenece
    // al estudiante autenticado.
    public boolean puedeAccederACarne(String carne) {

        if (carne == null || carne.isBlank()) {
            return false;
        }

        return obtenerEstudianteAutenticado()
                .map(estudiante ->
                        carne.equals(estudiante.getCarne())
                )
                .orElse(false);
    }
}
