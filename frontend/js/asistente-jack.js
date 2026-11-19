document.addEventListener("DOMContentLoaded", () => {
    // Mostrar Jack solo a estudiantes autenticados.
    const contenedorJack = document.querySelector(".jack-asistente");

    async function verificarAccesoJack() {
        if (!contenedorJack) return;

        contenedorJack.hidden = true;

        try {
            const respuesta = await fetch(
                "http://localhost:8080/api/auth/me",
                { credentials: "include" }
            );

            if (!respuesta.ok) return;

            const usuario = await respuesta.json();

            if (usuario.rol === "ESTUDIANTE") {
                contenedorJack.hidden = false;
            }
        } catch (error) {
            console.error("No se pudo verificar el acceso de Jack:", error);
        }
    }

    verificarAccesoJack();

    const abrir = document.getElementById("jack-abrir");
    const ventana = document.getElementById("jack-ventana");
    const cerrar = document.getElementById("jack-cerrar");
    const mensajes = document.getElementById("jack-mensajes");
    const formulario = document.getElementById("jack-formulario");
    const entrada = document.getElementById("jack-pregunta");

    if (!abrir || !ventana || !cerrar ||
        !mensajes || !formulario || !entrada) return;

    // Controles del personaje.
    const personaje = document.getElementById("jack-personaje");
    const mover = document.getElementById("jack-mover");
    const descansar = document.getElementById("jack-descansar");
    const despertar = document.getElementById("jack-despertar");
    const contenedor = document.querySelector(".jack-asistente");

    if (personaje && mover && descansar && despertar && contenedor) {
        mover.addEventListener("click", () => {
            const izquierda = contenedor.classList.toggle("jack-izquierda");
            contenedor.style.left = izquierda
                ? "min(calc(260px + 24px), 35vw)"
                : "";
            contenedor.style.right = izquierda ? "auto" : "20px";
        });

        descansar.addEventListener("click", () => {
            ventana.hidden = true;
            abrir.setAttribute("aria-expanded", "false");
            personaje.hidden = true;
            despertar.hidden = false;
        });

        despertar.addEventListener("click", () => {
            personaje.hidden = false;
            despertar.hidden = true;
            abrir.focus();
        });
    }

    // Descansar al consultar las pestañas academicas.
    function descansarJack() {
        if (!personaje || !despertar) return;

        ventana.hidden = true;
        abrir.hidden = false;
        abrir.setAttribute("aria-expanded", "false");

        const controles = document.querySelector(".jack-controles");
        if (controles) controles.hidden = false;

        personaje.hidden = true;
        despertar.hidden = false;
    }

    ["tab-catalogo", "tab-alertas", "tab-correos"].forEach((id) => {
        const tab = document.getElementById(id);

        if (tab) {
            tab.addEventListener("click", descansarJack);
        }
    });

    function mostrarVentana(abierta) {
        ventana.hidden = !abierta;
        abrir.setAttribute("aria-expanded", String(abierta));

        // Ocultar solo la imagen mientras el chat esta abierto.
        abrir.hidden = abierta;

        const controles = document.querySelector(".jack-controles");
        if (controles) {
            controles.hidden = abierta;
        }

        if (abierta) {
            entrada.focus();
        } else if (!personaje.hidden) {
            abrir.focus();
        }
    }

    abrir.addEventListener("click", () => {
        mostrarVentana(ventana.hidden);
    });

    cerrar.addEventListener("click", () => mostrarVentana(false));

    document.addEventListener("keydown", (evento) => {
        if (evento.key === "Escape" && !ventana.hidden) {
            mostrarVentana(false);
        }
    });

    function agregarMensaje(texto, tipo) {
        const elemento = document.createElement("p");
        elemento.className = "jack-mensaje " + tipo;
        elemento.textContent = texto;
        mensajes.appendChild(elemento);
        mensajes.scrollTop = mensajes.scrollHeight;
    }

    function agregarAcceso(texto, destino) {
        const enlace = document.createElement("a");
        enlace.className = "jack-acceso";
        enlace.textContent = texto;
        enlace.href = destino;
        mensajes.appendChild(enlace);
        mensajes.scrollTop = mensajes.scrollHeight;
    }

    function normalizar(texto) {
        return texto.toLowerCase()
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "");
    }

    function abrirCatalogo(pestana) {
        const modal = document.getElementById("modal-catalogo-general");
        const boton = document.getElementById("btn-abrir-catalogo");
        const tab = document.getElementById(pestana);

        if (!modal || !boton || !tab) {
            sessionStorage.setItem("jack-pestana-destino", pestana);
            window.location.href = "dashboard-asignacion.html";
            return;
        }

        if (boton.hidden) {
            agregarMensaje(
                "Esta sección no está disponible para tu usuario.",
                "jack"
            );
            return;
        }

        if (getComputedStyle(modal).display === "none") {
            boton.click();
        }

        tab.click();
        modal.scrollIntoView({ behavior: "smooth", block: "start" });
        mostrarVentana(false);
    }

    // Recuperar la pestaña solicitada desde otra pagina.
    async function abrirPestanaPendiente() {
        const destino = sessionStorage.getItem("jack-pestana-destino");

        if (!["tab-catalogo", "tab-alertas", "tab-correos"].includes(destino)) {
            return;
        }

        sessionStorage.removeItem("jack-pestana-destino");

        try {
            const respuesta = await fetch(
                "http://localhost:8080/api/auth/me",
                { credentials: "include" }
            );

            if (!respuesta.ok) return;

            const usuario = await respuesta.json();

            if (usuario.rol !== "ESTUDIANTE") return;

            const boton = document.getElementById("btn-abrir-catalogo");
            const modal = document.getElementById("modal-catalogo-general");
            const tab = document.getElementById(destino);

            if (!boton || !modal || !tab || boton.hidden || tab.hidden) {
                return;
            }

            if (getComputedStyle(modal).display === "none") {
                boton.click();
            }

            tab.click();
            modal.scrollIntoView({ behavior: "smooth", block: "start" });

        } catch (error) {
            console.error("No se pudo abrir la pestaña solicitada:", error);
        }
    }

    abrirPestanaPendiente();

    function responder(pregunta) {
        const texto = normalizar(pregunta.trim());

        if (!texto) return;

        agregarMensaje(pregunta.trim(), "usuario");

        if (texto.includes("adelant") ||
            texto.includes("promedio") ||
            texto.includes("prerrequisito")) {
            agregarMensaje(
                "Para solicitar un adelanto debes tener un promedio minimo de 85, seleccionar un curso del siguiente año academico y haber aprobado sus prerrequisitos. Puedes comprobarlo en la simulacion de adelantos.",
                "jack"
            );
            agregarAcceso(
                "Ir a simulacion de adelantos",
                "adelanto-cursos.html"
            );
        } else if (texto.includes("pensum")) {
            agregarMensaje(
                "Puedes consultar tu plan de estudios en Pensum academico.",
                "jack"
            );
        } else if (texto.includes("alerta") ||
                   texto.includes("notificacion")) {
            agregarMensaje(
                "Puedes consultar tus notificaciones en Mis Alertas.",
                "jack"
            );
        } else if (texto.includes("correo")) {
            agregarMensaje(
                "Puedes consultar tus mensajes de prueba en Correos simulados.",
                "jack"
            );
        } else if (texto.includes("catalogo") ||
                   texto.includes("curso") ||
                   texto.includes("seccion")) {
            agregarMensaje(
                "En el Catalogo de cursos puedes consultar cursos, secciones y sus estados.",
                "jack"
            );
        } else if (texto.includes("hola") ||
                   texto.includes("ayuda")) {
            agregarMensaje(
                "¡Hola! Puedo orientarte sobre pensum, catálogo, alertas, correos simulados y adelantos.",
                "jack"
            );
        } else {
            agregarMensaje(
                "Todavía no tengo una respuesta para esa pregunta. Prueba con pensum, catálogo, alertas, correos o adelantos.",
                "jack"
            );
        }
    }

    // Consejos academicos de Jack.
    const botonConsejo = document.getElementById("jack-consejo");

    const consejos = [
        "Puedes consultar tu plan de estudios desde Pensum academico.",
        "En el Catalogo de cursos puedes revisar las secciones disponibles.",
        "Puedes seguir secciones para recibir alertas sobre cambios de estado.",
        "Los correos simulados permiten visualizar mensajes de prueba sin enviarlos externamente.",
        "La simulacion de adelantos requiere un promedio minimo de 85 y prerrequisitos aprobados."
    ];

    let indiceConsejo = 0;

    if (botonConsejo) {
        botonConsejo.addEventListener("click", () => {
            if (ventana.hidden) {
                mostrarVentana(true);
            }

            agregarMensaje(
                "¿Sabias que...? " + consejos[indiceConsejo],
                "jack"
            );

            indiceConsejo = (indiceConsejo + 1) % consejos.length;
        });
    }

    formulario.addEventListener("submit", (evento) => {
        evento.preventDefault();
        const pregunta = entrada.value.trim();
        if (!pregunta) return;
        responder(pregunta);
        entrada.value = "";
        entrada.focus();
    });

    document.querySelectorAll("[data-jack-accion]").forEach((boton) => {
        boton.addEventListener("click", () => {
            const accion = boton.dataset.jackAccion;

            if (accion === "pensum") {
                window.location.href = "pensum-academico.html";
            } else if (accion === "catalogo") {
                abrirCatalogo("tab-catalogo");
            } else if (accion === "alertas") {
                abrirCatalogo("tab-alertas");
            } else if (accion === "correos") {
                abrirCatalogo("tab-correos");
            } else if (accion === "adelantos") {
                window.location.href = "adelanto-cursos.html";
            }
        });
    });
});
