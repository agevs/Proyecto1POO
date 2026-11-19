package com.uvg.proyectoasignaciones.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uvg.proyectoasignaciones.model.CorreoSimulado;

@Repository
public interface CorreoSimuladoRepository
        extends JpaRepository<CorreoSimulado, Integer> {

    List<CorreoSimulado>
        findByEstudianteCarneOrderByIdCorreoDesc(
            String estudianteCarne
        );
}
