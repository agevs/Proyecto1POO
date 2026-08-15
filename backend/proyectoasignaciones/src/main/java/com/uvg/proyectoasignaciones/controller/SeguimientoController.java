package com.uvg.proyectoasignaciones.controller;
import java.util.ArrayList;

import com.uvg.proyectoasignaciones.model.Estudiante;
import com.uvg.proyectoasignaciones.model.Seccion;
import com.uvg.proyectoasignaciones.model.Seguimiento;

public class SeguimientoController {
    private ArrayList<Seguimiento> seguimientos;

    public SeguimientoController(ArrayList<Seguimiento> seguimientos) {
        this.seguimientos = seguimientos;
    }

    public Seguimiento crearSeguimiento(
            Estudiante estudiante,
            Seccion seccion) {

        Seguimiento seguimientoExistente =
                buscarSeguimiento(estudiante, seccion);

        if (seguimientoExistente != null) {
            seguimientoExistente.activar();
            return seguimientoExistente;
        }

        int nuevoId = seguimientos.size() + 1;

        Seguimiento nuevoSeguimiento =
                new Seguimiento(nuevoId, estudiante, seccion);

        seguimientos.add(nuevoSeguimiento);
        estudiante.agregarSeguimiento(nuevoSeguimiento);

        return nuevoSeguimiento;
    }

    public boolean desactivarSeguimiento(
            Estudiante estudiante,
            Seccion seccion) {

        Seguimiento seguimiento =
                buscarSeguimiento(estudiante, seccion);

        if (seguimiento != null) {
            seguimiento.desactivar();
            return true;
        }

        return false;
    }

    public Seguimiento buscarSeguimiento(
            Estudiante estudiante,
            Seccion seccion) {

        for (Seguimiento seguimiento : seguimientos) {

            if (seguimiento.getEstudiante().equals(estudiante)
                    && seguimiento.getSeccion().equals(seccion)) {

                return seguimiento;
            }
        }

        return null;
    }

    public ArrayList<Seguimiento> obtenerSeguimientosEstudiante(
            Estudiante estudiante) {

        ArrayList<Seguimiento> resultado = new ArrayList<>();

        for (Seguimiento seguimiento : seguimientos) {
            if (seguimiento.getEstudiante().equals(estudiante)) {
                resultado.add(seguimiento);
            }
        }

        return resultado;
    }

    public ArrayList<Seguimiento> obtenerSeguimientosActivos(
            Estudiante estudiante) {

        ArrayList<Seguimiento> resultado = new ArrayList<>();

        for (Seguimiento seguimiento : seguimientos) {

            if (seguimiento.getEstudiante().equals(estudiante)
                    && seguimiento.isActivo()) {

                resultado.add(seguimiento);
            }
        }

        return resultado;
    }

    public boolean existeSeguimiento(
            Estudiante estudiante,
            Seccion seccion) {

        return buscarSeguimiento(estudiante, seccion) != null;
    }

    public ArrayList<Seguimiento> getSeguimientos() {
        return seguimientos;
    }

    public void setSeguimientos(ArrayList<Seguimiento> seguimientos) {
        this.seguimientos = seguimientos;
    }

    @Override
    public String toString() {
        return "SeguimientoController{" +
                "cantidadSeguimientos=" + seguimientos.size() +
                '}';
    }
}
