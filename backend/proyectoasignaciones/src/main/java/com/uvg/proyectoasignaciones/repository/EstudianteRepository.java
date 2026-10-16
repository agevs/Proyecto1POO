
package com.uvg.proyectoasignaciones.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uvg.proyectoasignaciones.model.Estudiante;

@Repository
public interface EstudianteRepository
        extends JpaRepository<Estudiante, Integer> {

    // Buscar estudiante por carne (RF-04, RF-05 y RF-06)
    Optional<Estudiante> findByCarne(String carne);

    // RF-01: Buscar estudiante por correo para iniciar sesion
    Optional<Estudiante> findByCorreo(String correo);
}

