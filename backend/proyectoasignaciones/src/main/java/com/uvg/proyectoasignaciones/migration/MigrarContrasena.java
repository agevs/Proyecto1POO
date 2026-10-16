package com.uvg.proyectoasignaciones.migration;

import java.io.Console;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MigrarContrasena {

    private static final String URL = "jdbc:sqlite:proyecto.db";
    private static final String CARNE = "26594";

    public static void main(String[] args) {

        Console consola = System.console();

        if (consola == null) {
            System.out.println(
                "No se detecto una consola interactiva."
            );
            return;
        }

        char[] anterior = null;
        char[] nueva = null;
        char[] confirmacion = null;

        try (Connection conexion =
                DriverManager.getConnection(URL)) {

            String contrasenaGuardada;

            String consulta = """
                SELECT contrasena
                FROM estudiante
                WHERE carne = ?
                """;

            try (PreparedStatement ps =
                    conexion.prepareStatement(consulta)) {

                ps.setString(1, CARNE);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        System.out.println(
                            "No se encontro el estudiante."
                        );
                        return;
                    }

                    contrasenaGuardada =
                            rs.getString("contrasena");

                    if (rs.next()) {
                        System.out.println(
                            "Se encontraron registros duplicados."
                        );
                        return;
                    }
                }
            }

            if (contrasenaGuardada == null) {
                System.out.println(
                    "El estudiante no tiene contrasena."
                );
                return;
            }

            BCryptPasswordEncoder encoder =
                    new BCryptPasswordEncoder();

            if (contrasenaGuardada.matches(
                    "^\\$2[aby]\\$\\d{2}\\$.{53}$")) {

                System.out.println(
                    "La contrasena ya tiene formato BCrypt."
                );
                return;
            }

            anterior = consola.readPassword(
                    "Contrasena anterior: "
            );

            if (anterior == null ||
                    !Arrays.equals(
                        anterior,
                        contrasenaGuardada.toCharArray()
                    )) {

                System.out.println(
                    "La contrasena anterior no coincide."
                );
                return;
            }

            nueva = consola.readPassword(
                    "Nueva contrasena (minimo 8 caracteres): "
            );

            confirmacion = consola.readPassword(
                    "Confirmar nueva contrasena: "
            );

            if (nueva == null ||
                    nueva.length < 8 ||
                    confirmacion == null ||
                    !Arrays.equals(nueva, confirmacion)) {

                System.out.println(
                    "La nueva contrasena no es valida " +
                    "o no coincide con la confirmacion."
                );
                return;
            }

            String hash = encoder.encode(
                    new String(nueva)
            );

            System.out.print(
                "Escribe SI para confirmar la migracion: "
            );

            String respuesta = consola.readLine();

            if (!"SI".equals(respuesta)) {
                System.out.println(
                    "Migracion cancelada."
                );
                return;
            }

            String actualizacion = """
                UPDATE estudiante
                SET contrasena = ?
                WHERE carne = ?
                AND contrasena = ?
                """;

            try (PreparedStatement ps =
                    conexion.prepareStatement(actualizacion)) {

                ps.setString(1, hash);
                ps.setString(2, CARNE);
                ps.setString(3, contrasenaGuardada);

                int filas = ps.executeUpdate();

                if (filas == 1) {
                    System.out.println(
                        "Contrasena migrada correctamente a BCrypt."
                    );
                } else {
                    System.out.println(
                        "No se modifico ningun registro."
                    );
                }
            }

        } catch (Exception error) {
            System.err.println(
                "Error de migracion: " + error.getMessage()
            );

        } finally {
            if (anterior != null) {
                Arrays.fill(anterior, '\0');
            }
            if (nueva != null) {
                Arrays.fill(nueva, '\0');
            }
            if (confirmacion != null) {
                Arrays.fill(confirmacion, '\0');
            }
        }
    }
}

