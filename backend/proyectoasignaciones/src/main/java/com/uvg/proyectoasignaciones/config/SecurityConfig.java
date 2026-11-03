
package com.uvg.proyectoasignaciones.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CorsConfigurationSource corsConfigurationSource)
            throws Exception {

        HttpSessionCsrfTokenRepository csrfRepository =
                new HttpSessionCsrfTokenRepository();

        csrfRepository.setHeaderName("X-XSRF-TOKEN");

        http
            .cors(cors -> cors.configurationSource(
                    corsConfigurationSource
            ))

            // RF-01: Proteger operaciones que modifican datos.
            .csrf(csrf -> csrf
                    .csrfTokenRepository(csrfRepository)
            )

            .sessionManagement(session -> session
                    .sessionCreationPolicy(
                            SessionCreationPolicy.IF_REQUIRED
                    )
            )

            .authorizeHttpRequests(auth -> auth

                    // Obtener token e iniciar sesion.
                    .requestMatchers(
                            "/api/auth/csrf",
                            "/api/auth/login"
                    ).permitAll()

                    // RF-11: Solo administradores modifican cursos y secciones.
                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/cursos",
                            "/api/cursos/*/secciones"
                    ).hasRole("ADMINISTRADOR")

                    .requestMatchers(
                            HttpMethod.PUT,
                            "/api/cursos/*",
                            "/api/cursos/secciones/*"
                    ).hasRole("ADMINISTRADOR")

                    // Consulta publica del catalogo.
                    .requestMatchers(
                            HttpMethod.GET,
                            "/api/cursos",
                            "/api/cursos/**"
                    ).permitAll()

                    // Funciones administrativas.
                    .requestMatchers(
                            "/api/administracion/**"
                    ).hasRole("ADMINISTRADOR")

                    // Funciones estudiantiles.
                    .requestMatchers(
                            "/api/seguimientos/**",
                            "/api/notificaciones/**"
                    ).hasRole("ESTUDIANTE")

                    // Consultar y cerrar sesion.
                    .requestMatchers(
                            "/api/auth/me",
                            "/api/auth/logout"
                    ).authenticated()

                    .anyRequest().authenticated()
            )

            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(
                            new HttpStatusEntryPoint(
                                    HttpStatus.UNAUTHORIZED
                            )
                    )
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
