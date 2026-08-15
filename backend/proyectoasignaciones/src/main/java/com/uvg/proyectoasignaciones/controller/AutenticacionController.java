package com.uvg.proyectoasignaciones.controller;
import java.util.ArrayList;
import com.uvg.proyectoasignaciones.model.Usuario;

public class AutenticacionController {
    private ArrayList<Usuario> usuarios;
    private Usuario usuarioActual;

    public AutenticacionController(ArrayList<Usuario> usuarios) {
        this.usuarios = usuarios;
        this.usuarioActual = null;
    }

    public boolean iniciarSesion(String correo, String contrasena) {

        for (Usuario usuario : usuarios) {
            if (usuario.validarCredenciales(correo, contrasena)) {
                this.usuarioActual = usuario;
                return true;
            }
        }

        return false;
    }

    public void cerrarSesion() {
        this.usuarioActual = null;
    }

    public boolean haySesionActiva() {
        return usuarioActual != null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    public ArrayList<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(ArrayList<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    public String toString() {
        return "AutenticacionController{" +
                "cantidadUsuarios=" + usuarios.size() +
                ", usuarioActual=" +
                (usuarioActual != null ? usuarioActual.getCorreo() : "Sin sesión") +
                '}';
    }


}
