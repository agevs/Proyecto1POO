document.addEventListener("DOMContentLoaded", () => {

    const formulario = document.getElementById("login-form");
    const boton = document.getElementById("btn-login");
    const mensajeError = document.getElementById("login-error");

    const API = "http://localhost:8080/api/auth";

    if (!formulario) {
        return;
    }

    // Obtener token CSRF asociado a la sesion.
    async function obtenerCsrf() {

        const respuesta = await fetch(`${API}/csrf`, {
            method: "GET",
            credentials: "include",
            cache: "no-store"
        });

        if (!respuesta.ok) {
            throw new Error("No se pudo obtener el token CSRF");
        }

        return await respuesta.json();
    }

    formulario.addEventListener("submit", async (evento) => {

        evento.preventDefault();

        const correo = document
            .getElementById("correo").value.trim();

        const contrasena = document
            .getElementById("contrasena").value;

        mensajeError.textContent = "";
        boton.disabled = true;
        boton.textContent = "VERIFICANDO...";

        try {

            // 1. Obtener token antes del login.
            const csrf = await obtenerCsrf();

            // 2. Enviar credenciales con token CSRF.
            const respuesta = await fetch(`${API}/login`, {
                method: "POST",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json",
                    [csrf.headerName]: csrf.token
                },
                body: JSON.stringify({
                    correo,
                    contrasena
                })
            });

            if (!respuesta.ok) {

                mensajeError.textContent =
                    respuesta.status === 401
                        ? "Correo o contraseña incorrectos."
                        : "No se pudo iniciar sesión.";

                return;
            }

            // 3. Verificar que la sesion este activa.
            const sesion = await fetch(`${API}/me`, {
                method: "GET",
                credentials: "include"
            });

            if (!sesion.ok) {
                mensajeError.textContent =
                    "No se pudo verificar la sesión.";
                return;
            }

            // 4. No almacenar contrasenas en localStorage.
            const usuario = await sesion.json();

            if (usuario.rol === "ADMINISTRADOR" ||
                usuario.rol === "ESTUDIANTE") {
                window.location.href = "splash.html";
            } else {
                mensajeError.textContent =
                    "El usuario no tiene un rol autorizado.";
            }

        } catch (error) {

            console.error("Error de conexión:", error);

            mensajeError.textContent =
                "No se pudo conectar con el servidor.";

        } finally {

            boton.disabled = false;
            boton.textContent = "INICIAR SESIÓN";
        }
    });
});
