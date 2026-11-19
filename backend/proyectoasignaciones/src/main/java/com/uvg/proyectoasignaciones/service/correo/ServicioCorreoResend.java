package com.uvg.proyectoasignaciones.service.correo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("correo-real")
public class ServicioCorreoResend implements ServicioCorreo {

    private static final URI API =
            URI.create("https://api.resend.com/emails");

    private final HttpClient cliente = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper json = new ObjectMapper();

    @Override
    public void enviarNotificacion(
            String destinatario,
            String asunto,
            String mensaje) {

        if (destinatario == null || destinatario.isBlank()
                || asunto == null || asunto.isBlank()
                || mensaje == null || mensaje.isBlank()) {
            throw new IllegalArgumentException(
                    "Los datos del correo son obligatorios"
            );
        }

        String clave = System.getenv("RESEND_API_KEY");
        String remitente = System.getenv("RESEND_FROM_EMAIL");

        if (clave == null || clave.isBlank()
                || remitente == null || remitente.isBlank()) {
            throw new IllegalStateException(
                    "Resend no esta configurado"
            );
        }

        try {
            ObjectNode datos = json.createObjectNode();

            datos.put("from", remitente);
            datos.putArray("to").add(destinatario);
            datos.put("subject", asunto);
            datos.put("text", mensaje);

            HttpRequest solicitud = HttpRequest.newBuilder(API)
                    .timeout(Duration.ofSeconds(15))
                    .header("Authorization", "Bearer " + clave)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            json.writeValueAsString(datos)
                    ))
                    .build();

            HttpResponse<String> respuesta = cliente.send(
                    solicitud,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (respuesta.statusCode() < 200
                    || respuesta.statusCode() >= 300) {
                throw new IllegalStateException(
                        "Resend rechazo el envio. HTTP "
                                + respuesta.statusCode()
                );
            }

        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Envio de correo interrumpido", error
            );

        } catch (java.io.IOException error) {
            throw new IllegalStateException(
                    "No se pudo conectar con Resend", error
            );
        }
    }
}
