package com.uvg.proyectoasignaciones.data;

import java.util.ArrayList;
import java.util.List;

import com.uvg.proyectoasignaciones.model.CursoPensum;

/**
 * Pensum 2025 de Ingeniería en Ciencia de la Computación y Tecnologías
 * de la Información (UVG). Fuente: plan de estudios de la carrera.
 *
 * Los créditos son los de la columna C del plan de estudios.
 * Los códigos oficiales solo se incluyen cuando están confirmados en
 * el portal de asignación; el resto queda en null hasta confirmarlos.
 *
 * Rutas: 0 = común; 1 y 2 = rutas alternas de matemática (un estudiante
 * cursa la Ruta 1 o la Ruta 2, no ambas). Los totales por ciclo del
 * plan corresponden a los cursos comunes más la Ruta 1.
 */
public final class DatosPensum {

    private DatosPensum() {
        // Clase de utilidad: no necesita instancias.
    }

    public static List<CursoPensum> cursos() {

        List<CursoPensum> l = new ArrayList<>();

        // ---------------- AÑO 1 ----------------
        // Ciclo 1
        l.add(c(null, "Ciencias de la Vida", 6, 1, 1, 0));
        l.add(c(null, "Algoritmos y Programación Básica", 4, 1, 1, 0));
        l.add(c(null, "Álgebra y Geometría Analítica", 6, 1, 1, 1));
        l.add(c(null, "Pensamiento Cuantitativo", 6, 1, 1, 2));
        l.add(c(null, "Comunicación Efectiva", 4, 1, 1, 0));
        l.add(c(null, "Química General", 7, 1, 1, 0));
        l.add(c(null, "Introducción a la Ingeniería", 3, 1, 1, 0));
        l.add(c(null, "Coaching para la Excelencia", 1, 1, 1, 0));
        // Ciclo 2
        l.add(c("CC2008", "Programación Orientada a Objetos", 6, 1, 2, 0));
        l.add(c("CU170", "Estadística 1", 4, 1, 2, 0));
        l.add(c("CU171", "Ciudadanía Global e Intercultural", 3, 1, 2, 0));
        l.add(c("CC3054", "Organización de Computadoras y Assembler", 6, 1, 2, 0));
        l.add(c(null, "Pensamiento Cuantitativo", 6, 1, 2, 1));
        l.add(c(null, "Cálculo 1", 6, 1, 2, 2));
        l.add(c("CU172", "Guatemala en el Contexto Mundial", 3, 1, 2, 0));

        // ---------------- AÑO 2 ----------------
        // Ciclo 1
        l.add(c(null, "Cálculo 1", 6, 2, 1, 1));
        l.add(c(null, "Cálculo 2", 4, 2, 1, 2));
        l.add(c(null, "Física 1", 7, 2, 1, 0));
        l.add(c(null, "Programación de Microprocesadores", 5, 2, 1, 0));
        l.add(c(null, "Retos Ambientales y Sostenibilidad", 4, 2, 1, 0));
        l.add(c(null, "Algoritmos y Estructuras de Datos", 5, 2, 1, 0));
        l.add(c(null, "Álgebra Lineal 1", 4, 2, 1, 0));
        // Ciclo 2
        l.add(c(null, "Física 2", 7, 2, 2, 0));
        l.add(c(null, "Matemática Discreta 1", 4, 2, 2, 0));
        l.add(c(null, "Programación Plataformas Móviles", 5, 2, 2, 0));
        l.add(c("CU175", "Investigación y Pensamiento Científico", 3, 2, 2, 0));
        l.add(c(null, "Internet of Things", 4, 2, 2, 0));
        l.add(c(null, "Cálculo 2", 4, 2, 2, 1));
        l.add(c(null, "Ecuaciones Diferenciales 1", 6, 2, 2, 2));

        // ---------------- AÑO 3 ----------------
        // Ciclo 1
        l.add(c(null, "Bases de Datos 1", 6, 3, 1, 0));
        l.add(c(null, "Ingeniería de Software 1", 5, 3, 1, 0));
        l.add(c(null, "Sistemas y Tecnologías Web", 5, 3, 1, 0));
        l.add(c(null, "Interacción Humano Computador", 5, 3, 1, 0));
        l.add(c(null, "Teoría de Probabilidades", 4, 3, 1, 0));
        l.add(c(null, "Ecuaciones Diferenciales 1", 6, 3, 1, 1));
        l.add(c(null, "Selectivo de desarrollo social, emocional o laboral", 3, 3, 1, 2));
        // Ciclo 2
        l.add(c("CU177", "Emprendimiento e Innovación", 3, 3, 2, 0));
        l.add(c(null, "Ingeniería de Software 2", 5, 3, 2, 0));
        l.add(c(null, "Gráficas por Computadora", 5, 3, 2, 0));
        l.add(c(null, "Selectivo de diseño y creatividad", 3, 3, 2, 0));
        l.add(c(null, "Lógica Matemática", 4, 3, 2, 0));
        l.add(c(null, "Teoría de la Computación", 5, 3, 2, 0));

        // ---------------- AÑO 4 ----------------
        // Ciclo 1
        l.add(c(null, "Inteligencia Artificial", 5, 4, 1, 0));
        l.add(c(null, "Minería de Datos", 4, 4, 1, 0));
        l.add(c(null, "Bases de Datos 2", 5, 4, 1, 0));
        l.add(c(null, "Diseño de Lenguajes de Programación", 6, 4, 1, 0));
        l.add(c(null, "Sistemas Operativos", 6, 4, 1, 0));
        l.add(c(null, "Análisis y Diseño de Algoritmos", 4, 4, 1, 0));
        // Ciclo 2
        l.add(c(null, "Computación Paralela y Distribuida", 5, 4, 2, 0));
        l.add(c(null, "Data Science", 5, 4, 2, 0));
        l.add(c(null, "Deep Learning y Sistemas Inteligentes", 5, 4, 2, 0));
        l.add(c(null, "Redes", 6, 4, 2, 0));
        l.add(c(null, "Construcción de Compiladores", 4, 4, 2, 0));
        l.add(c(null, "Modelación y Simulación", 5, 4, 2, 0));

        // ---------------- AÑO 5 ----------------
        // Ciclo 1
        l.add(c(null, "Diseño e Innovación en Ingeniería 1", 4, 5, 1, 0));
        l.add(c(null, "Administración de Proyectos TI", 4, 5, 1, 0));
        l.add(c(null, "Práctica profesional", 12, 5, 1, 0));
        l.add(c(null, "Seguridad en Sistemas de Computación", 4, 5, 1, 0));
        l.add(c(null, "Selectivo de desarrollo social, emocional o laboral", 3, 5, 1, 1));
        l.add(c(null, "Selectivo de especialidad", 4, 5, 1, 2));
        l.add(c(null, "Selectivo de especialidad", 4, 5, 1, 0));
        // Ciclo 2
        l.add(c(null, "Administración y Mantenimiento de Sistemas", 4, 5, 2, 0));
        l.add(c(null, "Selectivo de especialidad", 4, 5, 2, 0));
        l.add(c(null, "Selectivo de especialidad", 4, 5, 2, 0));
        l.add(c(null, "Selectivo de especialidad", 4, 5, 2, 0));
        l.add(c(null, "Selectivo de especialidad", 4, 5, 2, 0));
        l.add(c(null, "Trabajo de graduación", 10, 5, 2, 0));

        return l;
    }

    private static CursoPensum c(
            String codigo,
            String nombre,
            int creditos,
            int anio,
            int semestre,
            int ruta) {
        return new CursoPensum(codigo, nombre, creditos, anio, semestre, ruta);
    }
}
