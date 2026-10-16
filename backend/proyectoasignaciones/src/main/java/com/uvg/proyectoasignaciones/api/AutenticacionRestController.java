package com.uvg.proyectoasignaciones.api;

import com.uvg.proyectoasignaciones.model.Estudiante;
import com.uvg.proyectoasignaciones.service.AutenticacionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AutenticacionRestController {

    private final AutenticacionService autenticacionService;

    public AutenticacionRestController(
            AutenticacionService autenticacionService) {

        this.autenticacionService = autenticacionService;
    }

    public record SolicitudLogin(
            String correo,
            String contrasena) {
    }

    public record RespuestaLogin(
            int idUsuario,
            String nombre,
            String correo,
            String carne,
            String rol) {
    }

    @PostMapping("/login")
    public ResponseEntity<?> iniciarSesion(
            @RequestBody SolicitudLogin solicitud,
            HttpServletRequest request) {

        if (solicitud == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "mensaje", "Datos obligatorios"
                    ));
        }

        Optional<Estudiante> resultado =
                autenticacionService.autenticar(
                        solicitud.correo(),
                        solicitud.contrasena()
                );

        if (resultado.isEmpty()) {
            return ResponseEntity.status(
                    HttpStatus.UNAUTHORIZED
            ).body(Map.of(
                    "mensaje", "Credenciales incorrectas"
            ));
        }

        Estudiante estudiante = resultado.get();

        // Crear autenticacion reconocida por Spring Security.
        Authentication autenticacion =
                new UsernamePasswordAuthenticationToken(
                        estudiante.getCorreo(),
                        null,
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_ESTUDIANTE"
                                )
                        )
                );

        SecurityContext contexto =
                SecurityContextHolder.createEmptyContext();

        contexto.setAuthentication(autenticacion);

        // Evitar reutilizar la sesion anterior.
        HttpSession anterior = request.getSession(false);

        if (anterior != null) {
            anterior.invalidate();
        }

        HttpSession sesion = request.getSession(true);

        // Conservar identificador del estudiante.
        sesion.setAttribute(
                "usuarioId",
                estudiante.getIdUsuario()
        );

        sesion.setAttribute("rol", "ESTUDIANTE");

        // Guardar el contexto de Spring Security.
        sesion.setAttribute(
                HttpSessionSecurityContextRepository
                        .SPRING_SECURITY_CONTEXT_KEY,
                contexto
        );

        SecurityContextHolder.setContext(contexto);

        return ResponseEntity.ok(
                new RespuestaLogin(
                        estudiante.getIdUsuario(),
                        estudiante.getNombre(),
                        estudiante.getCorreo(),
                        estudiante.getCarne(),
                        "ESTUDIANTE"
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> cerrarSesion(
            HttpServletRequest request) {

        HttpSession sesion = request.getSession(false);

        if (sesion != null) {
            sesion.invalidate();
        }

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(
                Map.of("mensaje", "Sesion cerrada")
        );
    }

    @GetMapping("/me")
    public ResponseEntity<?> obtenerSesion(
            HttpServletRequest request) {

        HttpSession sesion = request.getSession(false);

        Authentication autenticacion =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        if (sesion == null ||
                sesion.getAttribute("usuarioId") == null ||
                autenticacion == null ||
                !autenticacion.isAuthenticated()) {

            return ResponseEntity.status(
                    HttpStatus.UNAUTHORIZED
            ).body(Map.of(
                    "mensaje", "No hay sesion activa"
            ));
        }

        return ResponseEntity.ok(
                Map.of(
                        "idUsuario",
                        sesion.getAttribute("usuarioId"),
                        "rol",
                        sesion.getAttribute("rol")
                )
        );
    }
}


