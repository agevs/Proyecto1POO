package com.uvg.proyectoasignaciones.api;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.uvg.proyectoasignaciones.controller.DocenteController;
import com.uvg.proyectoasignaciones.model.Docente;
import com.uvg.proyectoasignaciones.model.Seccion;

@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500"})
@RestController
@RequestMapping("/api/administracion")
public class DocenteRestController {
    private final DocenteController controller;

    public DocenteRestController(DocenteController controller) {
        this.controller = controller;
    }

    @GetMapping("/docentes")
    public List<Docente> listar() {
        return controller.listar();
    }

    @PostMapping("/docentes")
    @ResponseStatus(HttpStatus.CREATED)
    public Docente registrar(@RequestBody SolicitudDocente solicitud) {
        return controller.registrar(solicitud.idDocente(), solicitud.nombre(), solicitud.correo());
    }

    @PatchMapping("/cursos/{codigoCurso}/secciones/{numeroSeccion}/docente")
    public Seccion confirmar(@PathVariable String codigoCurso, @PathVariable int numeroSeccion,
            @RequestBody SolicitudConfirmacion solicitud) {
        return controller.confirmar(codigoCurso, numeroSeccion, solicitud.idDocente());
    }

    public record SolicitudDocente(Integer idDocente, String nombre, String correo) {}
    public record SolicitudConfirmacion(Integer idDocente) {}
}
