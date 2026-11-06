package com.uvg.proyectoasignaciones.api;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uvg.proyectoasignaciones.model.CursoPensum;
import com.uvg.proyectoasignaciones.repository.CursoPensumRepository;

/**
 * Consulta del pensum académico (solo lectura).
 *
 * GET /api/pensum
 *     Devuelve una lista plana ordenada por año y semestre. Cada
 *     elemento trae: codigo, nombre, creditos, anio, semestre, ruta.
 *
 * Filtros opcionales (combinables):
 *     ?anio=1..5   ?semestre=1|2   ?ruta=1|2
 *
 * Con ?ruta=N se devuelven los cursos comunes (ruta 0) más los de esa
 * ruta. Los filtros por año y semestre usan los mismos campos numéricos
 * que se envían en cada curso.
 */
@CrossOrigin(origins = {
    "http://127.0.0.1:5500",
    "http://localhost:5500",
    "null"
})
@RestController
@RequestMapping("/api/pensum")
public class PensumRestController {

    private final CursoPensumRepository repositorio;

    public PensumRestController(CursoPensumRepository repositorio) {
        this.repositorio = repositorio;
    }

    @GetMapping
    public List<CursoPensum> obtenerPensum(
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer semestre,
            @RequestParam(required = false) Integer ruta) {

        return repositorio.findAllByOrderByAnioAscSemestreAscIdAsc()
                .stream()
                .filter(c -> anio == null || c.getAnio() == anio)
                .filter(c -> semestre == null || c.getSemestre() == semestre)
                .filter(c -> ruta == null
                        || c.getRuta() == CursoPensum.RUTA_COMUN
                        || c.getRuta() == ruta)
                .toList();
    }
}
