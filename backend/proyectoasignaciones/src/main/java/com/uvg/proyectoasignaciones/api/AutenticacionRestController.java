
package com.uvg.proyectoasignaciones.api;

import com.uvg.proyectoasignaciones.model.Estudiante;
import com.uvg.proyectoasignaciones.service.AutenticacionService;
import com.uvg.proyectoasignaciones.service.AutenticacionAdministradorService;
import com.uvg.proyectoasignaciones.service.AutorizacionEstudianteService;

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
    private final AutenticacionAdministradorService administradorService;
    private final AutorizacionEstudianteService autorizacionEstudianteService;

    public AutenticacionRestController(
            AutenticacionService autenticacionService,
            AutenticacionAdministradorService administradorService,
            AutorizacionEstudianteService autorizacionEstudianteService) {

        this.autenticacionService = autenticacionService;
        this.administradorService = administradorService;
        this.autorizacionEstudianteService = autorizacionEstudianteService;
    }

    // RF-01: Datos recibidos para iniciar sesion.
    public record SolicitudLogin(
            String correo,
            String contrasena) {
    }

    // RF-01: Respuesta del inicio de sesion.
    public record RespuestaLogin(
            int idUsuario,
            String nombre,
            String correo,
            String carne,
            String rol) {
    }

    // RF-01: INICIAR SESION
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

        if (administradorService.autenticar(
                solicitud.correo(), solicitud.contrasena())) {

            Authentication autenticacion =
                    new UsernamePasswordAuthenticationToken(
                            solicitud.correo().trim(),
                            null,
                            List.of(new SimpleGrantedAuthority(
                                    "ROLE_ADMINISTRADOR"))
                    );

            SecurityContext contexto =
                    SecurityContextHolder.createEmptyContext();
            contexto.setAuthentication(autenticacion);

            HttpSession anterior = request.getSession(false);
            if (anterior != null) {
                anterior.invalidate();
            }

            HttpSession sesion = request.getSession(true);
            sesion.setAttribute("usuarioId", -1);
            sesion.setAttribute("rol", "ADMINISTRADOR");
            sesion.setAttribute(
                    HttpSessionSecurityContextRepository
                            .SPRING_SECURITY_CONTEXT_KEY,
                    contexto
            );

            SecurityContextHolder.setContext(contexto);

            return ResponseEntity.ok(
                    new RespuestaLogin(
                            -1,
                            "Administrador",
                            solicitud.correo().trim(),
                            "",
                            "ADMINISTRADOR"
                    )
            );
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

        // Conservar identificador y rol del estudiante.
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

    // RF-01: CERRAR SESION
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

    // RF-01 + RF-12: CONSULTAR SESION AUTENTICADA
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

        boolean esAdministrador = autenticacion.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority()
                        .equals("ROLE_ADMINISTRADOR"));

        if (esAdministrador
                && "ADMINISTRADOR".equals(sesion.getAttribute("rol"))) {
            return ResponseEntity.ok(Map.of(
                    "idUsuario", -1,
                    "rol", "ADMINISTRADOR",
                    "correo", autenticacion.getName()
            ));
        }

        // Obtener el estudiante desde la identidad
        // autenticada por Spring Security.
        Optional<Estudiante> estudiante =
                autorizacionEstudianteService
                        .obtenerEstudianteAutenticado();

        if (estudiante.isEmpty()) {
            return ResponseEntity.status(
                    HttpStatus.FORBIDDEN
            ).body(Map.of(
                    "mensaje", "Estudiante no autorizado"
            ));
        }

        Estudiante estudianteAutenticado = estudiante.get();

        // Devolver el carne real del estudiante.
        // No utilizar localStorage ni valores fijos.
        return ResponseEntity.ok(
                Map.of(
                        "idUsuario",
                        estudianteAutenticado.getIdUsuario(),
                        "rol",
                        "ESTUDIANTE",
                        "carne",
                        estudianteAutenticado.getCarne()
                )
        );
    }
}
