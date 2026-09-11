package com.uvg.proyectoasignaciones.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uvg.proyectoasignaciones.model.Estudiante;

@Repository
public interface EstudianteRepository
        extends JpaRepository<Estudiante, Integer> {

    Optional<Estudiante> findByCarne(String carne);
}
