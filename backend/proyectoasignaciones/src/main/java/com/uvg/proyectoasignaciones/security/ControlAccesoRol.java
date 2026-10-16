package com.uvg.proyectoasignaciones.security;

import com.uvg.proyectoasignaciones.model.Administrador;
import com.uvg.proyectoasignaciones.model.Estudiante;
import com.uvg.proyectoasignaciones.model.Usuario;

/** RF-12: reglas de autorizacion por rol, independientes del inicio de sesion. */
public final class ControlAccesoRol {
    public enum Rol { ESTUDIANTE, ADMINISTRADOR, SIN_AUTENTICAR }
    public enum Permiso {
        CONSULTAR_CURSOS, CONSULTAR_ESTADO, SEGUIR_SECCION,
        DEJAR_SEGUIR, CONSULTAR_SEGUIMIENTOS, CONSULTAR_NOTIFICACIONES,
        GESTIONAR_ESTADO, REGISTRAR_DOCENTE, GESTIONAR_CURSOS_SECCIONES
    }

    private ControlAccesoRol() { }

    public static Rol obtenerRol(Usuario usuario) {
        if (usuario instanceof Administrador) return Rol.ADMINISTRADOR;
        if (usuario instanceof Estudiante) return Rol.ESTUDIANTE;
        return Rol.SIN_AUTENTICAR;
    }

    public static boolean tienePermiso(Usuario usuario, Permiso permiso) {
        if (permiso == null) return false;
        Rol rol = obtenerRol(usuario);
        if (rol == Rol.ADMINISTRADOR) return true;
        if (rol != Rol.ESTUDIANTE) return false;
        return switch (permiso) {
            case CONSULTAR_CURSOS, CONSULTAR_ESTADO, SEGUIR_SECCION,
                 DEJAR_SEGUIR, CONSULTAR_SEGUIMIENTOS,
                 CONSULTAR_NOTIFICACIONES -> true;
            default -> false;
        };
    }
}
