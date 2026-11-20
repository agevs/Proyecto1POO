package com.uvg.proyectoasignaciones.service.correo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.uvg.proyectoasignaciones.model.CorreoSimulado;
import com.uvg.proyectoasignaciones.repository.CorreoSimuladoRepository;

@Service
@Profile("correo-simulado")
public class RegistroCorreoSimulado {

    private final CorreoSimuladoRepository repository;

    public RegistroCorreoSimulado(
            CorreoSimuladoRepository repository) {
        this.repository = repository;
    }

    public void registrar(
            String carne,
            String destinatario,
            String asunto,
            String mensaje) {

        if (carne == null || carne.isBlank()
                || destinatario == null || destinatario.isBlank()
                || asunto == null || asunto.isBlank()
                || mensaje == null || mensaje.isBlank()) {
            throw new IllegalArgumentException(
                    "Los datos del correo son obligatorios");
        }

        CorreoSimulado correo = new CorreoSimulado(
                carne,
                destinatario,
                asunto,
                mensaje,
                LocalDateTime.now().toString()
        );

        repository.save(correo);
    }

    public List<CorreoSimulado> obtenerPorEstudiante(
            String carne) {
        return repository
                .findByEstudianteCarneOrderByIdCorreoDesc(carne);
    }
}
