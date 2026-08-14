package com.uvg.proyectoasignaciones.model;

public class Usuario {
    private int idUsuario;
    private String nombre;
    private String correo;
    private String contrasena;

    public Usuario(int idUsuario, String nombre, String correo, String contrasena) {
    this.idUsuario = idUsuario;
    this.nombre = nombre;
    this.correo = correo;
    this.contrasena = contrasena;
    }

    public boolean validarCredenciales(String correo, String contrasena) {
    return this.correo.equals(correo) && this.contrasena.equals(contrasena);
    }

    public int getIdUsuario() {
    return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
    this.idUsuario = idUsuario;
    }

    public String getNombre() {
    return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                '}';
    }

}
