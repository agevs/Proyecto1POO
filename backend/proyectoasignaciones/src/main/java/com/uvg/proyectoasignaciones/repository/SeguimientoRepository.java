package com.uvg.proyectoasignaciones.repository;

import java.util.List;

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

    // RF-04 / RF-05:
    // Se usa List para tolerar posibles duplicados históricos
    // sin provocar NonUniqueResultException.
    List<Seguimiento>
            findByEstudianteCarneAndSeccionIdSeccionAndActivoTrue(
                    String carne,
                    Long idSeccion
            );

    // RF-04:
    // Buscar todos los seguimientos anteriores de una sección,
    // ordenados del más reciente al más antiguo.
    // Esto permite reactivar el seguimiento más reciente.
    List<Seguimiento>
            findByEstudianteCarneAndSeccionIdSeccionOrderByIdSeguimientoDesc(
                    String carne,
                    Long idSeccion
            );

    // RF-07: estudiantes que siguen una sección
    List<Seguimiento>
            findBySeccionIdSeccionAndActivoTrue(
                    Long idSeccion
            );
}