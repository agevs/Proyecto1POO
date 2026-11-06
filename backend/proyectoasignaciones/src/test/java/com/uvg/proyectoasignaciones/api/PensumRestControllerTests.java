package com.uvg.proyectoasignaciones.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.uvg.proyectoasignaciones.data.DatosPensum;
import com.uvg.proyectoasignaciones.model.CursoPensum;
import com.uvg.proyectoasignaciones.repository.CursoPensumRepository;

@ExtendWith(MockitoExtension.class)
class PensumRestControllerTests {

    @Mock
    private CursoPensumRepository repositorio;

    /** Créditos de cada ciclo según el plan de estudios (Ruta 1 + comunes). */
    private static final int[][] TOTALES = {
        // año 1  ciclo1 ciclo2
        {1, 1, 31}, {1, 2, 28},
        {2, 1, 31}, {2, 2, 27},
        {3, 1, 31}, {3, 2, 25},
        {4, 1, 30}, {4, 2, 30},
        {5, 1, 31}, {5, 2, 30}
    };

    private int creditosRuta1(int anio, int semestre) {
        return DatosPensum.cursos().stream()
                .filter(c -> c.getAnio() == anio && c.getSemestre() == semestre)
                .filter(c -> c.getRuta() != 2)
                .mapToInt(CursoPensum::getCreditos)
                .sum();
    }

    @Test
    void creditosPorCicloCoincidenConElPlanDeEstudios() {
        for (int[] t : TOTALES) {
            assertEquals(t[2], creditosRuta1(t[0], t[1]),
                    "Créditos del año " + t[0] + " ciclo " + t[1]);
        }
    }

    @Test
    void cadaCursoTieneAnioSemestreYCreditosValidos() {
        for (CursoPensum c : DatosPensum.cursos()) {
            assertTrue(c.getAnio() >= 1 && c.getAnio() <= 5, c.toString());
            assertTrue(c.getSemestre() == 1 || c.getSemestre() == 2, c.toString());
            assertTrue(c.getCreditos() > 0, c.toString());
        }
    }

    @Test
    void filtraPorAnioYSemestre() {
        when(repositorio.findAllByOrderByAnioAscSemestreAscIdAsc())
                .thenReturn(DatosPensum.cursos());

        PensumRestController controller =
                new PensumRestController(repositorio);

        List<CursoPensum> resultado =
                controller.obtenerPensum(1, 2, 1);

        assertTrue(resultado.stream().allMatch(
                c -> c.getAnio() == 1 && c.getSemestre() == 2));
        assertTrue(resultado.stream().anyMatch(
                c -> "CC2008".equals(c.getCodigo())));
        // Ruta 1 no incluye el curso exclusivo de la Ruta 2.
        assertTrue(resultado.stream().noneMatch(
                c -> c.getRuta() == 2));
    }
}
