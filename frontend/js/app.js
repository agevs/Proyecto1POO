document.addEventListener("DOMContentLoaded", () => {

    // Elementos de la interfaz
    const linkAsignaciones = document.getElementById("link-asignaciones");
    const btnAbrirCatalogo = document.getElementById("btn-abrir-catalogo");
    const modalCatalogo = document.getElementById("modal-catalogo-general");
    const tabCatalogo = document.getElementById("tab-catalogo");
    const tabAlertas = document.getElementById("tab-alertas");

    // NAVEGACIÓN HACIA ASIGNACIONES
    // Si estamos en el index y se hace clic en Asignaciones
    if (linkAsignaciones) {
        linkAsignaciones.addEventListener("click", (e) => {
            e.preventDefault();

            // Redirige a la pantalla de carga
            window.location.href = "splash.html";
        });
    }
    // ABRIR / CERRAR CATÁLOGO GENERAL
    if (btnAbrirCatalogo && modalCatalogo) {

        btnAbrirCatalogo.addEventListener("click", () => {

            if (
                modalCatalogo.style.display === "none" ||
                modalCatalogo.style.display === ""
            ) {

                modalCatalogo.style.display = "block";

                modalCatalogo.scrollIntoView({
                    behavior: "smooth"
                });

            } else {

                modalCatalogo.style.display = "none";
            }
        });
    }
    // CARGAR CURSOS DESDE SPRING BOOT
    cargarCursos();
    configurarPestanas(tabCatalogo, tabAlertas);
    cargarNotificaciones();
});

// OBTENER CURSOS DEL BACKEND

async function cargarCursos() {

    try {

        const respuesta = await fetch(
            "http://localhost:8080/api/cursos"
        );

        if (!respuesta.ok) {
            throw new Error(
                "No se pudieron obtener los cursos"
            );
        }

        // Convierte la respuesta JSON en objetos JavaScript
        const cursos = await respuesta.json();

        console.log("Cursos recibidos:", cursos);

        // Enviar los cursos a la función que construye la tabla
        mostrarCursos(cursos);

    } catch (error) {

        console.error(
            "Error al cargar cursos:",
            error
        );
    }
}

// MOSTRAR CURSOS EN LA TABLA

function mostrarCursos(cursos) {

    const tabla = document.getElementById(
        "tabla-cursos-body"
    );

    // Si estamos en una página que no tiene la tabla,
    // simplemente no hacemos nada.
    if (!tabla) {
        return;
    }

    // Limpiar cualquier contenido anterior
    tabla.innerHTML = "";


    // Recorrer todos los cursos recibidos
    cursos.forEach(curso => {

        // Cada curso puede tener varias secciones
        curso.secciones.forEach(seccion => {

            const fila = document.createElement("tr");

            // DOCENTE

            // Si todavía no existe docente, mostrar STAFF
            const nombreDocente = seccion.docente
                ? seccion.docente.nombre
                : "STAFF";

            // ESTADO

            // Determinar qué estilo utilizar según el estado
            const claseEstado =
                seccion.estado === "Confirmado"
                    ? "badge-success"
                    : "badge-warning";

            // CONSTRUIR FILA

            fila.innerHTML = `

                <td>
                    ${curso.codigoCurso}
                </td>

                <td>
                    ${curso.nombreCurso}
                    <br>
                    <small>
                        ${seccion.horario}
                    </small>
                </td>

                <td>
                    Sección ${seccion.numeroSeccion}
                </td>

                <td>
                    ${nombreDocente}
                </td>

                <td>
                    <span class="badge-status ${claseEstado}">
                        ${seccion.estado}
                    </span>
                </td>

                <td>

                    <button
                        class="btn-ver-ficha"
                        data-codigo="${curso.codigoCurso}"
                        data-seccion="${seccion.numeroSeccion}">
                        Ver Ficha
                    </button>

                    <button
                        class="btn-uvg btn-seguir"
                        data-codigo="${curso.codigoCurso}"
                        data-seccion="${seccion.numeroSeccion}">
                        🔔 Seguir
                    </button>

                </td>
            `;


            // Agregar la fila a la tabla
            tabla.appendChild(fila);
        });
    });
}

// RF-07: CONSULTAR Y MOSTRAR NOTIFICACIONES

function configurarPestanas(tabCatalogo, tabAlertas) {
    const panelCatalogo = document.getElementById("panel-catalogo");
    const panelAlertas = document.getElementById("panel-alertas");

    if (!tabCatalogo || !tabAlertas || !panelCatalogo || !panelAlertas) {
        return;
    }

    tabCatalogo.addEventListener("click", () => {
        panelCatalogo.hidden = false;
        panelAlertas.hidden = true;
        tabCatalogo.classList.add("catalogo-tab-activa");
        tabAlertas.classList.remove("catalogo-tab-activa");
    });

    tabAlertas.addEventListener("click", () => {
        panelCatalogo.hidden = true;
        panelAlertas.hidden = false;
        tabAlertas.classList.add("catalogo-tab-activa");
        tabCatalogo.classList.remove("catalogo-tab-activa");
        cargarNotificaciones();
    });
}

async function cargarNotificaciones() {
    const lista = document.getElementById("lista-alertas");
    const estado = document.getElementById("estado-alertas");

    if (!lista || !estado) {
        return;
    }

    estado.textContent = "Cargando alertas...";

    try {
        const respuesta = await fetch("http://localhost:8080/api/notificaciones");
        if (!respuesta.ok) {
            throw new Error("No se pudieron obtener las notificaciones");
        }

        mostrarNotificaciones(await respuesta.json());
    } catch (error) {
        console.error("Error al cargar notificaciones:", error);
        lista.innerHTML = "";
        estado.textContent = "No fue posible cargar las alertas. Verifica que el backend esté activo.";
    }
}

function mostrarNotificaciones(notificaciones) {
    const lista = document.getElementById("lista-alertas");
    const estado = document.getElementById("estado-alertas");
    const cantidad = document.getElementById("cantidad-alertas");

    if (!lista || !estado || !cantidad) {
        return;
    }

    const noLeidas = notificaciones.filter(notificacion => !notificacion.leida).length;
    cantidad.textContent = noLeidas;
    lista.innerHTML = "";

    if (notificaciones.length === 0) {
        estado.textContent = "No tienes alertas por el momento.";
        return;
    }

    estado.textContent = `${notificaciones.length} alerta(s), ${noLeidas} sin leer`;
    notificaciones.forEach(notificacion => {
        const tarjeta = document.createElement("article");
        tarjeta.className = `alerta ${notificacion.leida ? "" : "alerta-no-leida"}`;

        const contenido = document.createElement("div");
        const mensaje = document.createElement("p");
        mensaje.textContent = notificacion.mensaje;

        const detalle = document.createElement("small");
        detalle.className = "alerta-meta";
        const seccion = notificacion.seccion
            ? `Sección ${notificacion.seccion.numeroSeccion} · `
            : "";
        detalle.textContent = seccion + formatearFecha(notificacion.fecha);

        contenido.append(mensaje, detalle);
        tarjeta.appendChild(contenido);

        if (!notificacion.leida) {
            const boton = document.createElement("button");
            boton.type = "button";
            boton.className = "btn-marcar-leida";
            boton.textContent = "Marcar como leída";
            boton.addEventListener("click", () => marcarNotificacionComoLeida(notificacion.idNotificacion));
            tarjeta.appendChild(boton);
        }

        lista.appendChild(tarjeta);
    });
}

function formatearFecha(fecha) {
    const valor = new Date(fecha);
    return Number.isNaN(valor.getTime()) ? fecha : valor.toLocaleString("es-GT");
}

async function marcarNotificacionComoLeida(idNotificacion) {
    try {
        const respuesta = await fetch(
            `http://localhost:8080/api/notificaciones/${idNotificacion}/leida`,
            { method: "PATCH" }
        );

        if (!respuesta.ok) {
            throw new Error("No se pudo actualizar la notificación");
        }

        await cargarNotificaciones();
    } catch (error) {
        console.error("Error al marcar la notificación:", error);
    }
}
