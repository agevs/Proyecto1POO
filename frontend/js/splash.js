"use strict";

(async function () {
    const inicio = Date.now();

    try {
        const respuesta = await fetch(
            "http://localhost:8080/api/auth/me",
            {
                credentials: "include",
                cache: "no-store"
            }
        );

        if (!respuesta.ok) {
            throw new Error("Sesion no valida");
        }

        const usuario = await respuesta.json();

        let destino = "login.html";

        if (usuario.rol === "ADMINISTRADOR") {
            destino = "administracion-docentes.html";
        } else if (usuario.rol === "ESTUDIANTE") {
            destino = "dashboard-asignacion.html";
        }

        const espera = Math.max(0, 1500 - (Date.now() - inicio));

        setTimeout(() => {
            window.location.replace(destino);
        }, espera);

    } catch (error) {
        window.location.replace("login.html");
    }
})();
