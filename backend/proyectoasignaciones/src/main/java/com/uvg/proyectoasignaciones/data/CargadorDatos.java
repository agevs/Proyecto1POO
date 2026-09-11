package com.uvg.proyectoasignaciones.data;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.uvg.proyectoasignaciones.model.Curso;
import com.uvg.proyectoasignaciones.model.Estudiante;
import com.uvg.proyectoasignaciones.model.Seccion;
import com.uvg.proyectoasignaciones.repository.CursoRepository;
import com.uvg.proyectoasignaciones.repository.EstudianteRepository;

@Component
public class CargadorDatos implements CommandLineRunner {

    private final CursoRepository cursoRepository;
    private final EstudianteRepository estudianteRepository;

    public CargadorDatos(
            CursoRepository cursoRepository,
            EstudianteRepository estudianteRepository) {

        this.cursoRepository = cursoRepository;
        this.estudianteRepository = estudianteRepository;
    }

    @Override
    public void run(String... args) {

        // CARGAR CURSO Y SECCION INICIAL
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
                    "Curso y sección inicial guardados en SQLite."
            );
        }

        // CARGAR ESTUDIANTE INICIAL
        if (estudianteRepository.findByCarne("26594").isEmpty()) {

            Estudiante estudiante = new Estudiante(
                    1,
                    "Margarita de los Angeles Avilés González",
                    "estudiante@uvg.edu.gt",
                    "1234",
                    "26594"
            );

            estudianteRepository.save(estudiante);

            System.out.println(
                    "Estudiante inicial guardado en SQLite."
            );
        }
    }
}

