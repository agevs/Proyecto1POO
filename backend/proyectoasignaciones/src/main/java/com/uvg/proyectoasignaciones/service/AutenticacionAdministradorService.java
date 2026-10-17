package com.uvg.proyectoasignaciones.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AutenticacionAdministradorService {

    private final PasswordEncoder passwordEncoder;

    public AutenticacionAdministradorService(
            PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public boolean autenticar(String correo, String contrasena) {

        if (correo == null || contrasena == null
                || correo.isBlank() || contrasena.isBlank()) {
            return false;
        }

        String correoAdmin = System.getenv("UVG_ADMIN_EMAIL");
        String hashAdmin = System.getenv("UVG_ADMIN_PASSWORD_HASH");

        if (correoAdmin == null || correoAdmin.isBlank()
                || hashAdmin == null || hashAdmin.isBlank()) {
            return false;
        }

        if (!hashAdmin.startsWith("$2")) {
            return false;
        }

        if (!correoAdmin.trim().equalsIgnoreCase(correo.trim())) {
            return false;
        }

        return passwordEncoder.matches(contrasena, hashAdmin);
    }
}
