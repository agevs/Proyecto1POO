package com.uvg.proyectoasignaciones.controller;

import com.uvg.proyectoasignaciones.model.Seguimiento;
import com.uvg.proyectoasignaciones.repository.SeguimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/seguimientos")
public class SeguimientoRestController {
    
    @Autowired
    private SeguimientoRepository seguimientoRepository;

    @GetMapping("/estudiante/{carne}")
    public List<Seguimiento> obtenerSeguimientosPorEstudiante(@PathVariable String carne) {
        return seguimientoRepository.findByEstudianteCarneAndActivoTrue(carne);
    }

    @GetMapping
    public List<Seguimiento> obtenerTodosLosSeguimientosActivos() {
        return seguimientoRepository.findByActivoTrue();
    }
}
