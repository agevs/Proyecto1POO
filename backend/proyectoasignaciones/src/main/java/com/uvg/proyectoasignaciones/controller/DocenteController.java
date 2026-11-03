package com.uvg.proyectoasignaciones.controller;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.uvg.proyectoasignaciones.model.*;
import com.uvg.proyectoasignaciones.repository.*;

@Service
public class DocenteController {
    private final DocenteRepository docentes;
    private final CursoRepository cursos;
    private final SeguimientoRepository seguimientos;
    private final NotificacionController notificaciones;

    public DocenteController(DocenteRepository docentes, CursoRepository cursos,
            SeguimientoRepository seguimientos, NotificacionController notificaciones) {
        this.docentes = docentes;
        this.cursos = cursos;
        this.seguimientos = seguimientos;
        this.notificaciones = notificaciones;
    }

    public List<Docente> listar() {
        return docentes.findAll();
    }

    @Transactional
    public Docente registrar(Integer id, String nombre, String correo) {
        if (id == null || id <= 0 || nombre == null || nombre.isBlank()
                || correo == null || !correo.trim().matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Indica un ID positivo, nombre y correo válido");
        }
        if (docentes.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El ID de docente ya existe");
        }
        return docentes.save(new Docente(id, nombre.trim(), correo.trim()));
    }

    @Transactional
    public Seccion confirmar(String codigo, int numero, Integer idDocente) {
        if (idDocente == null || idDocente <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El docente es obligatorio");
        }
        Curso curso = cursos.findById(codigo).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));
        Seccion seccion = curso.getSecciones().stream()
                .filter(s -> s.getNumeroSeccion() == numero).findFirst().orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Sección no encontrada"));
        Docente docente = docentes.findById(idDocente).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Docente no encontrado"));
        // Repetir la misma confirmación no genera alertas duplicadas.
        if (seccion.getDocente() != null
                && seccion.getDocente().getIdDocente() == idDocente
                && "Confirmado".equals(seccion.getEstado())) {
            return seccion;
        }
        seccion.asignarDocente(docente);
        seccion.actualizarEstado("Confirmado");
        cursos.save(curso);
        for (Seguimiento seguimiento : seguimientos
                .findBySeccionIdSeccionAndActivoTrue(seccion.getIdSeccion())) {
            notificaciones.crearNotificacion(seguimiento.getEstudiante(), seccion,
                    "Se confirmó al docente " + docente.getNombre() + " para la sección "
                    + numero + " del curso " + codigo);
        }
        return seccion;
    }
}
