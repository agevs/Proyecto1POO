package com.uvg.proyectoasignaciones.model;

import jakarta.persistence.*;

@Entity
@Table(name = "correos_simulados")
public class CorreoSimulado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCorreo;

    private String estudianteCarne;
    private String destinatario;
    private String asunto;
    private String fecha;

    @Column(length = 4000)
    private String mensaje;

    protected CorreoSimulado() {
    }

    public CorreoSimulado(
            String estudianteCarne,
            String destinatario,
            String asunto,
            String mensaje,
            String fecha) {

        this.estudianteCarne = estudianteCarne;
        this.destinatario = destinatario;
        this.asunto = asunto;
        this.mensaje = mensaje;
        this.fecha = fecha;
    }

    public Integer getIdCorreo() {
        return idCorreo;
    }

    public String getEstudianteCarne() {
        return estudianteCarne;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public String getAsunto() {
        return asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getFecha() {
        return fecha;
    }
}
