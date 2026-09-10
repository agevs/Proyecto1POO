package com.uvg.proyectoasignaciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uvg.proyectoasignaciones.model.Curso;

public interface CursoRepository extends JpaRepository<Curso, String> {

}