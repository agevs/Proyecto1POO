package com.uvg.proyectoasignaciones.data;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.uvg.proyectoasignaciones.model.Curso;
import com.uvg.proyectoasignaciones.model.Seccion;
import com.uvg.proyectoasignaciones.repository.CursoRepository;

@Component
public class CargadorDatos implements CommandLineRunner {

    private final CursoRepository cursoRepository;

    public CargadorDatos(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    @Override
    public void run(String... args) {

        if (cursoRepository.count() == 0) {

            Curso cursoPOO = new Curso(
                    "CC2008",
                    "Programación Orientada a Objetos 1",
                    4
            );

            Seccion seccion10 = new Seccion(
                    10,
                    "Lu 10:00 AM / Mi 10:00 AM",
                    25,
                    "Docente en confirmación"
            );

            cursoPOO.agregarSeccion(seccion10);

            cursoRepository.save(cursoPOO);

            System.out.println(
                    "Datos iniciales guardados en SQLite."
            );
        }
    }
}

