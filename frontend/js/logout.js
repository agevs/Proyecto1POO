"use strict";

// RF-01: Cierre de sesión para estudiantes y administradores.
document.addEventListener("DOMContentLoaded", () => {
    const boton = document.getElementById("btn-cerrar-sesion");

    if (!boton) return;

    const API = "http://localhost:8080/api/auth";

    boton.addEventListener("click", async () => {
        boton.disabled = true;
        boton.textContent = "CERRANDO SESIÓN...";

        try {
            // Obtener token CSRF de la sesión.
            const respuestaCsrf = await fetch(`${API}/csrf`, {
                method: "GET",
                credentials: "include",
                cache: "no-store"
            });

            if (!respuestaCsrf.ok) {
                throw new Error("No se pudo obtener el token CSRF");
            }

            const csrf = await respuestaCsrf.json();

            // Solicitar al backend que invalide la sesión.
            const respuestaLogout = await fetch(`${API}/logout`, {
                method: "POST",
                credentials: "include",
                headers: {
                    [csrf.headerName]: csrf.token
                }
            });

            if (!respuestaLogout.ok) {
                throw new Error(
                    `Error al cerrar sesión: HTTP ${respuestaLogout.status}`
                );
            }

            // Regresar al formulario de autenticación.
            window.location.replace("login.html");

        } catch (error) {
            console.error("Error al cerrar sesión:", error);
            alert("No se pudo cerrar la sesión. Inténtalo nuevamente.");

            boton.disabled = false;
            boton.textContent = "Cerrar sesión";
        }
    });
});
