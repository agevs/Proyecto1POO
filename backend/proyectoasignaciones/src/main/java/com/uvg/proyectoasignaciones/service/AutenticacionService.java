package com.uvg.proyectoasignaciones.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uvg.proyectoasignaciones.model.Estudiante;
import com.uvg.proyectoasignaciones.repository.EstudianteRepository;

@Service
public class AutenticacionService {

    private final EstudianteRepository estudianteRepository;
    private final PasswordEncoder passwordEncoder;

    public AutenticacionService(
            EstudianteRepository estudianteRepository,
            PasswordEncoder passwordEncoder) {

        this.estudianteRepository = estudianteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // RF-01: Validar las credenciales de un estudiante.
    public Optional<Estudiante> autenticar(
            String correo,
            String contrasena) {

        if (correo == null || contrasena == null
                || correo.isBlank() || contrasena.isBlank()) {
            return Optional.empty();
        }

        Optional<Estudiante> estudianteOptional =
                estudianteRepository.findByCorreo(correo.trim());

        if (estudianteOptional.isEmpty()) {
            return Optional.empty();
        }

        Estudiante estudiante = estudianteOptional.get();

        String hashAlmacenado = estudiante.getContrasena();

        if (hashAlmacenado == null
                || !hashAlmacenado.startsWith("$2")) {
            return Optional.empty();
        }

        if (!passwordEncoder.matches(contrasena, hashAlmacenado)) {
            return Optional.empty();
        }

        return Optional.of(estudiante);
    }
}
