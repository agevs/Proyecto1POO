package com.uvg.proyectoasignaciones.api;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uvg.proyectoasignaciones.model.CorreoSimulado;
import com.uvg.proyectoasignaciones.model.Estudiante;
import com.uvg.proyectoasignaciones.service.AutorizacionEstudianteService;
import com.uvg.proyectoasignaciones.service.correo.RegistroCorreoSimulado;

@RestController
@Profile("correo-simulado")
@RequestMapping("/api/correos-simulados")
@CrossOrigin(origins = {
    "http://localhost:5500",
    "http://127.0.0.1:5500"
})
public class CorreoSimuladoRestController {

    private final RegistroCorreoSimulado registro;
    private final AutorizacionEstudianteService autorizacion;

    public CorreoSimuladoRestController(
            RegistroCorreoSimulado registro,
            AutorizacionEstudianteService autorizacion) {
        this.registro = registro;
        this.autorizacion = autorizacion;
    }

    @GetMapping
    public ResponseEntity<List<CorreoSimulado>> obtenerCorreos() {

        Optional<Estudiante> estudiante =
                autorizacion.obtenerEstudianteAutenticado();

        if (estudiante.isEmpty()) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                registro.obtenerPorEstudiante(
                        estudiante.get().getCarne()
                )
        );
    }
}
