package com.uvg.proyectoasignaciones.repository;

import com.uvg.proyectoasignaciones.model.Seguimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository

public interface SeguimientoRepository extends JpaRepository<Seguimiento, Integer> {
    List<Seguimiento> findByEstudianteCarneAndActivoTrue(String carne);
    List<Seguimiento> findByActivoTrue();
}
