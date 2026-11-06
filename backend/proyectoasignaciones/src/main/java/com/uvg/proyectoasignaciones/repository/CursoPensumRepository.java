package com.uvg.proyectoasignaciones.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uvg.proyectoasignaciones.model.CursoPensum;

public interface CursoPensumRepository
        extends JpaRepository<CursoPensum, Long> {

    List<CursoPensum> findAllByOrderByAnioAscSemestreAscIdAsc();
}
