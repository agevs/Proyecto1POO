package com.uvg.proyectoasignaciones;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;
import com.uvg.proyectoasignaciones.controller.DocenteController;
import com.uvg.proyectoasignaciones.api.CursoRestController;
import com.uvg.proyectoasignaciones.repository.CursoRepository;
import com.uvg.proyectoasignaciones.model.Curso;
import com.uvg.proyectoasignaciones.model.Seccion;
import static org.junit.jupiter.api.Assertions.*;

class RF10PersistenciaTests {
    @TempDir Path carpeta;

    private ConfigurableApplicationContext iniciar(Path archivo) {
        return new SpringApplicationBuilder(ProyectoasignacionesApplication.class)
                .run("--spring.main.web-application-type=none",
                     "--spring.datasource.url=jdbc:sqlite:" + archivo.toAbsolutePath(),
                     "--spring.jpa.hibernate.ddl-auto=update",
                     "--spring.jpa.show-sql=false");
    }

    @Test
    void confirmaYConservaDocenteEnCatalogoDespuesDeReiniciar() {
        Path archivo = carpeta.resolve("rf10.db");
        try (var contexto = iniciar(archivo)) {
            var servicio = contexto.getBean(DocenteController.class);
            var repositorio = contexto.getBean(CursoRepository.class);
            Curso curso = new Curso("RF10-TEST", "Curso de prueba RF10", 4);
            curso.agregarSeccion(new Seccion(10, "Lunes 10:00", 20, "Docente en confirmación"));
            curso.agregarSeccion(new Seccion(20, "Martes 10:00", 20, "Docente en confirmación"));
            repositorio.save(curso);
            servicio.registrar(901, "Docente de prueba", "prueba@example.com");
            var confirmada = servicio.confirmar("RF10-TEST", 10, 901);
            assertEquals("Confirmado", confirmada.getEstado());
            assertEquals(901, confirmada.getDocente().getIdDocente());
            servicio.confirmar("RF10-TEST", 10, 901);
        }
        // Segundo contexto: obliga a reconstruir los datos desde el archivo SQLite.
        try (var contexto = iniciar(archivo)) {
            new TransactionTemplate(contexto.getBean(PlatformTransactionManager.class))
                    .executeWithoutResult(estado -> {
                var catalogo = contexto.getBean(CursoRestController.class).obtenerCursos();
                var curso = catalogo.stream().filter(c -> c.getCodigoCurso().equals("RF10-TEST"))
                        .findFirst().orElseThrow();
                var seccion = curso.getSecciones().stream()
                        .filter(s -> s.getNumeroSeccion() == 10).findFirst().orElseThrow();
                assertEquals("Docente de prueba", seccion.getDocente().getNombre());
                assertEquals("Confirmado", seccion.getEstado());
                assertNull(curso.getSecciones().stream().filter(s -> s.getNumeroSeccion() == 20)
                        .findFirst().orElseThrow().getDocente());
            });
            var servicio = contexto.getBean(DocenteController.class);
            assertEquals(HttpStatus.CONFLICT, assertThrows(ResponseStatusException.class,
                    () -> servicio.registrar(901, "Otro", "otro@example.com")).getStatusCode());
            assertEquals(HttpStatus.BAD_REQUEST, assertThrows(ResponseStatusException.class,
                    () -> servicio.registrar(0, " ", "invalido")).getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, assertThrows(ResponseStatusException.class,
                    () -> servicio.confirmar("RF10-TEST", 10, 999)).getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, assertThrows(ResponseStatusException.class,
                    () -> servicio.confirmar("RF10-TEST", 99, 901)).getStatusCode());
            assertEquals(HttpStatus.NOT_FOUND, assertThrows(ResponseStatusException.class,
                    () -> servicio.confirmar("NO-EXISTE", 10, 901)).getStatusCode());
        }
    }
}
