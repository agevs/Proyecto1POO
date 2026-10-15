package com.uvg.proyectoasignaciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uvg.proyectoasignaciones.model.Docente;

public interface DocenteRepository extends JpaRepository<Docente, Integer> {
}
