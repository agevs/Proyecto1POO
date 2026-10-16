
package com.uvg.proyectoasignaciones.data;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.uvg.proyectoasignaciones.model.Curso;
import com.uvg.proyectoasignaciones.model.Estudiante;
import com.uvg.proyectoasignaciones.model.Seccion;
import com.uvg.proyectoasignaciones.repository.CursoRepository;
import com.uvg.proyectoasignaciones.repository.EstudianteRepository;

@Component
public class CargadorDatos implements CommandLineRunner {

    private final CursoRepository cursoRepository;
    private final EstudianteRepository estudianteRepository;
    private final PasswordEncoder passwordEncoder;

    public CargadorDatos(
            CursoRepository cursoRepository,
            EstudianteRepository estudianteRepository,
            PasswordEncoder passwordEncoder) {

        this.cursoRepository = cursoRepository;
        this.estudianteRepository = estudianteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // Crear curso y seccion inicial si no existen
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
                    "Curso y seccion inicial guardados en SQLite."
            );
        }

        // Crear estudiante de prueba solo si no existe
        if (estudianteRepository.findByCarne("26594").isEmpty()) {

            String contrasenaInicial =
                    System.getenv("UVG_TEST_PASSWORD");

            if (contrasenaInicial == null ||
                    contrasenaInicial.isBlank()) {

                System.out.println(
                    "No se creo el estudiante de prueba: " +
                    "UVG_TEST_PASSWORD no esta configurada."
                );

                return;
            }

            String hash = passwordEncoder.encode(
                    contrasenaInicial
            );

            Estudiante estudiante = new Estudiante(
                    1,
                    "Margarita de los Angeles Avilés González",
                    "estudiante@uvg.edu.gt",
                    hash,
                    "26594"
            );

            estudianteRepository.save(estudiante);

            System.out.println(
                    "Estudiante inicial guardado con BCrypt."
            );
        }
    }
}

