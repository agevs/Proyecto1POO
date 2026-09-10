package com.uvg.proyectoasignaciones.data;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.uvg.proyectoasignaciones.model.Curso;
import com.uvg.proyectoasignaciones.model.Docente;
import com.uvg.proyectoasignaciones.model.Estudiante;
import com.uvg.proyectoasignaciones.model.Notificacion;
import com.uvg.proyectoasignaciones.model.Seccion;
import com.uvg.proyectoasignaciones.model.Seguimiento;
import com.uvg.proyectoasignaciones.model.Usuario;

@Component
public class DatosSistema {

    private final ArrayList<Usuario> usuarios;
    private final ArrayList<Curso> cursos;
    private final ArrayList<Docente> docentes;
    private final ArrayList<Seguimiento> seguimientos;
    private final ArrayList<Notificacion> notificaciones;

    private Estudiante estudianteActual;

    public DatosSistema() {

        usuarios = new ArrayList<>();
        cursos = new ArrayList<>();
        docentes = new ArrayList<>();
        seguimientos = new ArrayList<>();
        notificaciones = new ArrayList<>();

        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {

        // ESTUDIANTE TEMPORAL


        estudianteActual = new Estudiante(
                1,
                "Margarita de los Angeles Avilés González",
                "estudiante@uvg.edu.gt",
                "1234",
                "26594"
        );

        usuarios.add(estudianteActual);

        // CURSO DE PRUEBA

        Curso cursoPOO = new Curso(
                "CC2008",
                "Programación Orientada a Objetos 1",
                4
        );

        // SECCIÓN DE PRUEBA

        Seccion seccion10 = new Seccion(
                10,
                "Lu 10:00 AM / Mi 10:00 AM",
                25,
                "Docente en confirmación"
        );

        cursoPOO.agregarSeccion(seccion10);

        cursos.add(cursoPOO);
    }

    public ArrayList<Usuario> getUsuarios() {
        return usuarios;
    }

    public ArrayList<Curso> getCursos() {
        return cursos;
    }

    public ArrayList<Docente> getDocentes() {
        return docentes;
    }

    public ArrayList<Seguimiento> getSeguimientos() {
        return seguimientos;
    }

    public ArrayList<Notificacion> getNotificaciones() {
        return notificaciones;
    }

    public Estudiante getEstudianteActual() {
        return estudianteActual;
    }

    public void setEstudianteActual(Estudiante estudianteActual) {
        this.estudianteActual = estudianteActual;
    }
}