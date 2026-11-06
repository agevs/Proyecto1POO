package com.uvg.proyectoasignaciones.data;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.uvg.proyectoasignaciones.repository.CursoPensumRepository;

/**
 * Carga el pensum en la base de datos al iniciar la aplicación.
 *
 * Los datos del pensum son de referencia y de solo lectura, por lo que
 * se reemplazan completos en cada arranque. Así, corregir un dato en
 * {@link DatosPensum} se refleja sin borrar manualmente proyecto.db.
 * No toca el catálogo de cursos ni las secciones.
 */
@Component
public class CargadorPensum implements CommandLineRunner {

    private final CursoPensumRepository repositorio;

    public CargadorPensum(CursoPensumRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    @Transactional
    public void run(String... args) {
        repositorio.deleteAll();
        repositorio.saveAll(DatosPensum.cursos());

        System.out.println(
                "Pensum 2025 cargado: " + repositorio.count() + " cursos."
        );
    }
}
