package com.uvg.proyectoasignaciones.controller;

import java.util.ArrayList;
import java.util.List;


import com.uvg.proyectoasignaciones.model.Estudiante;
import com.uvg.proyectoasignaciones.model.Seccion;
import com.uvg.proyectoasignaciones.model.Seguimiento;
import com.uvg.proyectoasignaciones.repository.SeguimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/seguimientos")
public class SeguimientoController {

    @Autowired 
    private SeguimientoRepository seguimientoRepository;

    private ArrayList<Seguimiento> seguimientos;
    
    public SeguimientoController(){

    }
    
    public SeguimientoController(ArrayList<Seguimiento> seguimientos){
        this.seguimientos = seguimientos != null ? seguimientos : new ArrayList<>();
    }   

    @GetMapping("/estudiante/{carne}")
    public String verSeccionesSeguidas(@PathVariable String carne, Model model) {
        List<Seguimiento> listaSeguimientos = seguimientoRepository != null ? seguimientoRepository.findByEstudianteCarneAndActivoTrue(carne) : new ArrayList<>();
        model.addAttribute("seguimientos", listaSeguimientos);
        return "seguimientos";
    }

    @GetMapping 
    public String verTodosLosSeguimientos(Model model) {
        List<Seguimiento> lista = seguimientoRepository != null ? seguimientoRepository.findByActivoTrue() : seguimientos;
        model.addAttribute("seguimientos", lista);
        return "seguimientos";
    }


    public Seguimiento crearSeguimiento(Estudiante estudiante, Seccion seccion) {
        Seguimiento seguimientoExistente = buscarSeguimiento(estudiante, seccion);
            if (seguimientoExistente != null) {
                seguimientoExistente.activar();
                if (seguimientoRepository != null) seguimientoRepository.save(seguimientoExistente);
                return seguimientoExistente;
            }

            int nuevoId = seguimientos.size() + 1;
            Seguimiento nuevoSeguimiento = new Seguimiento(nuevoId, estudiante, seccion);
            seguimientos.add(nuevoSeguimiento);
            if (estudiante != null) estudiante.agregarSeguimiento(nuevoSeguimiento);

            if (seguimientoRepository != null) {
                seguimientoRepository.save(nuevoSeguimiento);
            }
            return nuevoSeguimiento;
        }

        public boolean desactivarSeguimiento(Estudiante estudiante, Seccion seccion) {
            Seguimiento seguimiento = buscarSeguimiento(estudiante, seccion);
            if (seguimiento != null) {
                seguimiento.desactivar();
                if (seguimientoRepository != null) seguimientoRepository.save(seguimiento);
                return true;
            }
            return false;
        }

        public Seguimiento buscarSeguimiento(Estudiante estudiante, Seccion seccion) {
            for (Seguimiento seguimiento : seguimientos) {
                if (seguimiento.getEstudiante() != null && seguimiento.getEstudiante().equals(estudiante)  &&  seguimiento.getSeccion() != null && seguimiento.getSeccion().equals(seccion)) {
                    return seguimiento;
                }
            }
            return null;
        }

        public ArrayList<Seguimiento> obtenerSeguimientosEstudiante(Estudiante estudiante) {
            ArrayList<Seguimiento> resultado = new ArrayList<>();
            for (Seguimiento seguimiento : seguimientos) {
                if (seguimiento.getEstudiante() != null && seguimiento.getEstudiante().equals(estudiante)) {
                    resultado.add(seguimiento);
                }
            }
            return resultado;
        }

        public ArrayList<Seguimiento> obtenerSeguimientosActivos(Estudiante estudiante) {
            ArrayList<Seguimiento> resultado = new ArrayList<>();
            for (Seguimiento seguimiento : seguimientos) {
                if (seguimiento.getEstudiante() != null && seguimiento.getEstudiante().equals(estudiante) && seguimiento.isActivo()) {
                    resultado.add(seguimiento);
                }
            }
            return resultado;
        }


        public boolean existeSeguimiento(Estudiante estudiante, Seccion seccion) {
            return buscarSeguimiento(estudiante, seccion) != null;
        }

        public ArrayList<Seguimiento> getSeguimientos() {
            return seguimientos;
        }

        public void setSeguimientos(ArrayList<Seguimiento> seguimientos) {
            this.seguimientos = seguimientos;
        }
    }