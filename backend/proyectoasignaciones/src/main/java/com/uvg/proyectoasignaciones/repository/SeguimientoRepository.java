package com.uvg.proyectoasignaciones.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uvg.proyectoasignaciones.model.Seguimiento;

@Repository
public interface SeguimientoRepository
        extends JpaRepository<Seguimiento, Integer> {

    // RF-06: seguimientos activos de un estudiante
    List<Seguimiento> findByEstudianteCarneAndActivoTrue(
            String carne
    );

    // RF-06: todos los seguimientos activos
    List<Seguimiento> findByActivoTrue();

    // RF-04: evitar seguir dos veces la misma sección
    Optional<Seguimiento>
            findByEstudianteCarneAndSeccionIdSeccionAndActivoTrue(
                    String carne,
                    Long idSeccion
            );

    // RF-07: estudiantes que siguen una sección
    List<Seguimiento>
            findBySeccionIdSeccionAndActivoTrue(
                    Long idSeccion
            );
}