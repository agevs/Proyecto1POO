package com.uvg.proyectoasignaciones.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uvg.proyectoasignaciones.model.Notificacion;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    List<Notificacion> findByEstudianteCarneOrderByIdNotificacionDesc(String estudianteCarne);

    List<Notificacion> findByEstudianteCarneAndLeidaFalseOrderByIdNotificacionDesc(
            String estudianteCarne);
}
