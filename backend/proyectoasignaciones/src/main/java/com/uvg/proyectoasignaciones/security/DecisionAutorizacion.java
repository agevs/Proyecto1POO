package com.uvg.proyectoasignaciones.security;

import com.uvg.proyectoasignaciones.model.Usuario;
import com.uvg.proyectoasignaciones.security.ControlAccesoRol.Permiso;

/**
 * RF-12: decisión de autorización.
 *
 * Combina las reglas de rutas con los permisos
 * correspondientes al rol del usuario.
 *
 * Esta clase no autentica usuarios ni configura
 * Spring Security.
 */
public final class DecisionAutorizacion {

    private DecisionAutorizacion() {
        // Clase de utilidad.
    }

    /**
     * Evalúa una operación administrativa.
     *
     * Se deniega el acceso cuando:
     * - No existe un usuario.
     * - El método o la ruta son nulos.
     * - La ruta no es administrativa.
     * - No existe una regla reconocida.
     * - El usuario no tiene el permiso requerido.
     *
     * IMPORTANTE:
     * El usuario debe provenir de una identidad
     * autenticada y verificada por el servidor.
     */
    public static boolean puedeEjecutar(
            Usuario usuario,
            String metodo,
            String ruta) {

        if (usuario == null ||
                metodo == null ||
                ruta == null) {
            return false;
        }

        if (!ReglasAutorizacion.esRutaAdministrativa(ruta)) {
            return false;
        }

        Permiso permiso =
                ReglasAutorizacion.obtenerPermiso(
                        metodo, ruta);

        // Denegación por defecto:
        // ninguna regla reconocida = acceso denegado.
        if (permiso == null) {
            return false;
        }

        return ControlAccesoRol.tienePermiso(
                usuario, permiso);
    }
}