package com.uvg.proyectoasignaciones.api;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uvg.proyectoasignaciones.controller.CursoController;
import com.uvg.proyectoasignaciones.controller.SeguimientoController;
import com.uvg.proyectoasignaciones.data.DatosSistema;
import com.uvg.proyectoasignaciones.model.Estudiante;
import com.uvg.proyectoasignaciones.model.Seccion;
import com.uvg.proyectoasignaciones.model.Seguimiento;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/seguimientos")
public class SeguimientoRestController {

    private final SeguimientoController seguimientoController;
    private final CursoController cursoController;
    private final DatosSistema datosSistema;

    public SeguimientoRestController(DatosSistema datosSistema) {

        this.datosSistema = datosSistema;

        this.seguimientoController =
                new SeguimientoController(
                        datosSistema.getSeguimientos()
                );

        this.cursoController =
                new CursoController(
                        datosSistema.getCursos()
                );
    }

    // CREAR / ACTIVAR SEGUIMIENTO

    @PostMapping
    public Seguimiento crearSeguimiento(
            @RequestParam String codigoCurso,
            @RequestParam int numeroSeccion) {

        Estudiante estudiante =
                datosSistema.getEstudianteActual();

        Seccion seccion =
                cursoController.buscarSeccion(
                        codigoCurso,
                        numeroSeccion
                );

        if (estudiante == null || seccion == null) {
            return null;
        }

        return seguimientoController.crearSeguimiento(
                estudiante,
                seccion
        );
    }

    // OBTENER SEGUIMIENTOS ACTIVOS

    @GetMapping
    public ArrayList<Seguimiento> obtenerSeguimientos() {

        Estudiante estudiante =
                datosSistema.getEstudianteActual();

        if (estudiante == null) {
            return new ArrayList<>();
        }

        return seguimientoController
                .obtenerSeguimientosActivos(estudiante);
    }
}