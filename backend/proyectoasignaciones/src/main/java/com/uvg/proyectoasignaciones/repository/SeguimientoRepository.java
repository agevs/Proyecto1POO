package com.uvg.proyectoasignaciones.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uvg.proyectoasignaciones.model.Seguimiento;

@Repository
public interface SeguimientoRepository
        extends JpaRepository<Seguimiento, Integer> {

    List<Seguimiento> findByEstudianteCarneAndActivoTrue(
            String carne
    );

    List<Seguimiento> findByActivoTrue();

    Optional<Seguimiento>
            findByEstudianteCarneAndSeccionIdSeccionAndActivoTrue(
                    String carne,
                    Long idSeccion
            );
}