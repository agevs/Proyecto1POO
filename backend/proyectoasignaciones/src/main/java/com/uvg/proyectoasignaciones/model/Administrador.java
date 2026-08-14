package com.uvg.proyectoasignaciones.model;

public class Administrador extends Usuario {
    private String cargo;

    public Administrador(int idUsuario, String nombre, String correo, String contrasena, String cargo) {
        super(idUsuario, nombre, correo, contrasena);
        this.cargo = cargo;
    }

    public String getCargo(){
            return cargo;
    }

    public void setCargo(String cargo) {
            this.cargo = cargo;
    }

    @Override
    public String toString() {
        return "Administrador{" +
                "idUsuario=" + getIdUsuario() +
                ", nombre='" + getNombre() + '\'' +
                ", correo='" + getCorreo() + '\'' +
                ", cargo='" + cargo + '\'' +
                '}';
    }    
    
}
