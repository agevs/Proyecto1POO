package com.uvg.proyectoasignaciones.security;

import com.uvg.proyectoasignaciones.security.ControlAccesoRol.Permiso;

/**
 * RF-12: Reglas de autorización para las rutas REST.
 *
 * Esta clase identifica el permiso necesario para
 * ejecutar determinadas operaciones del sistema.
 *
 * La autenticación será proporcionada por Spring Security.
 */
public final class ReglasAutorizacion {

    private ReglasAutorizacion() {
        // Clase de utilidad: no necesita instancias.
    }

    /**
     * Devuelve el permiso requerido por una operación.
     *
     * Un resultado null significa que esta clase no
     * reconoce la ruta. NO significa acceso permitido.
     */
    public static Permiso obtenerPermiso(
            String metodo,
            String ruta) {

        if (metodo == null || ruta == null) {
            return null;
        }

        // RF-10: registro, listado y confirmación de docentes.
        if (ruta.equals("/api/administracion/docentes")
                && (metodo.equalsIgnoreCase("GET") || metodo.equalsIgnoreCase("POST"))) {
            return Permiso.REGISTRAR_DOCENTE;
        }
        if (metodo.equalsIgnoreCase("PATCH")
                && ruta.matches("/api/administracion/cursos/[^/]+/secciones/[0-9]+/docente")) {
            return Permiso.REGISTRAR_DOCENTE;
        }

        // Operación administrativa: cambiar estado.
        // PATCH /api/administracion/cursos/{codigo}
        //       /secciones/{numero}/estado

        String[] partes = ruta.split("/", -1);

        if (metodo.equalsIgnoreCase("PATCH")
                && partes.length == 8
                && partes[0].isEmpty()
                && partes[1].equals("api")
                && partes[2].equals("administracion")
                && partes[3].equals("cursos")
                && !partes[4].isBlank()
                && partes[5].equals("secciones")
                && partes[6].matches("[0-9]+")
                && partes[7].equals("estado")) {

            return Permiso.GESTIONAR_ESTADO;
        }

        return null;
    }

    /**
     * Comprueba si la ruta pertenece al área
     * administrativa del sistema.
     */
    public static boolean esRutaAdministrativa(String ruta) {

        if (ruta == null) {
            return false;
        }

        return ruta.equals("/api/administracion")
                || ruta.startsWith("/api/administracion/");
    }
}
