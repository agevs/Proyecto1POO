document.addEventListener("DOMContentLoaded", () => {

    // RF-01 + RF-12: verificar la sesion y aplicar permisos visuales.
    async function verificarSesionDashboard() {
        const API = "http://localhost:8080/api/auth";

        try {
            const respuesta = await fetch(`${API}/me`, {
                method: "GET",
                credentials: "include"
            });

            if (!respuesta.ok) {
                window.ControlRolesVista?.aplicarVistaRol(null);
                window.location.replace("login.html");
                return;
            }

            const usuario = await respuesta.json();

            // El rol proviene de la sesion validada por Spring Security.
            const rol = usuario.rol;

            window.ControlRolesVista?.aplicarVistaRol(rol);

            console.log("Sesion verificada correctamente.");

        } catch (error) {
            console.error("No se pudo verificar la sesion:", error);
            window.ControlRolesVista?.aplicarVistaRol(null);
            window.location.replace("login.html");
        }
    }

    verificarSesionDashboard();


    // ELEMENTOS DE LA INTERFAZ
    const linkAsignaciones =
        document.getElementById("link-asignaciones");

    const btnAbrirCatalogo =
        document.getElementById("btn-abrir-catalogo");

    const modalCatalogo =
        document.getElementById("modal-catalogo-general");

    const tabCatalogo =
        document.getElementById("tab-catalogo");

    const tabAlertas =
        document.getElementById("tab-alertas");


    // NAVEGACIÓN HACIA ASIGNACIONES
    if (linkAsignaciones) {

        linkAsignaciones.addEventListener(
            "click",
            (evento) => {

                evento.preventDefault();

                window.location.href =
                    "splash.html";
            }
        );
    }


    // ABRIR / CERRAR CATÁLOGO GENERAL
    if (btnAbrirCatalogo && modalCatalogo) {

        btnAbrirCatalogo.addEventListener(
            "click",
            () => {

                if (
                    modalCatalogo.style.display === "none" ||
                    modalCatalogo.style.display === ""
                ) {

                    modalCatalogo.style.display =
                        "block";

                    modalCatalogo.scrollIntoView({
                        behavior: "smooth"
                    });

                } else {

                    modalCatalogo.style.display =
                        "none";
                }
            }
        );
    }


    // CARGAS INICIALES
    cargarCursos();

    configurarPestanas(
        tabCatalogo,
        tabAlertas
    );

    cargarNotificaciones();
});


// RF-02: CONSULTAR CURSOS Y SECCIONES
// RF-04: CONSULTAR ESTADO DE SEGUIMIENTO

async function cargarCursos() {

    try {

        // OBTENER CURSOS
        const respuestaCursos =
            await fetch(
                "http://localhost:8080/api/cursos"
            );

        if (!respuestaCursos.ok) {

            throw new Error(
                "No se pudieron obtener los cursos"
            );
        }

        const cursos =
            await respuestaCursos.json();


        // OBTENER SEGUIMIENTOS ACTIVOS DEL ESTUDIANTE
        const sesionRespuesta = await fetch(
            "http://localhost:8080/api/auth/me",
            { credentials: "include" }
        );

        if (!sesionRespuesta.ok) {
            throw new Error("No hay sesion activa.");
        }

        const usuario = await sesionRespuesta.json();

        if (usuario.rol !== "ESTUDIANTE" || !usuario.carne) {
            throw new Error("Estudiante no autorizado.");
        }

        const respuestaSeguimientos = await fetch(
            `http://localhost:8080/api/seguimientos/estudiante/${encodeURIComponent(usuario.carne)}`,
            { credentials: "include" }
        );

        let seguimientos = [];

        if (respuestaSeguimientos.ok) {

            seguimientos =
                await respuestaSeguimientos.json();
        }


        console.log(
            "Cursos recibidos:",
            cursos
        );

        console.log(
            "Seguimientos activos:",
            seguimientos
        );


        mostrarCursos(
            cursos,
            seguimientos
        );

        configurarBuscador(
            cursos,
            seguimientos
        );

    } catch (error) {

        console.error(
            "Error al cargar cursos:",
            error
        );
    }
}


function configurarBuscador(
    cursos,
    seguimientos
) {

    const buscador =
        document.getElementById(
            "buscador-cursos"
        );

    if (
        !buscador ||
        buscador.dataset.configurado === "true"
    ) {
        return;
    }

    buscador.dataset.configurado = "true";

    buscador.addEventListener(
        "input",
        (evento) => {

            const termino = normalizarTexto(
                evento.target.value
            );

            const cursosFiltrados = !termino
                ? cursos
                : cursos.filter(
                    curso => normalizarTexto(
                        `${curso.codigoCurso} ${curso.nombreCurso}`
                    ).includes(termino)
                );

            mostrarCursos(
                cursosFiltrados,
                seguimientos
            );
        }
    );
}


function normalizarTexto(valor) {

    return String(valor ?? "")
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase()
        .trim();
}


// MOSTRAR CURSOS EN LA TABLA

function mostrarCursos(
    cursos,
    seguimientos
) {

    const tabla =
        document.getElementById(
            "tabla-cursos-body"
        );

    if (!tabla) {
        return;
    }

    tabla.innerHTML = "";


    // NUEVO: MOSTRAR MENSAJE CUANDO NO HAY RESULTADOS
    if (cursos.length === 0){

    const fila =
        document.createElement("tr");

    fila.className =
        "fila-sin-resultados";

    fila.innerHTML = `
        <td colspan="6">
            No se encontraron cursos.
        </td>
    `;

    tabla.appendChild(fila);

    return;
	}


    cursos.forEach(curso => {

        curso.secciones.forEach(seccion => {

            const fila =
                document.createElement("tr");


            // DOCENTE
            const nombreDocente =
                seccion.docente
                    ? seccion.docente.nombre
                    : "STAFF";


            // ESTADO DE LA SECCIÓN
            const claseEstado =
                seccion.estado === "Confirmado"
                    ? "badge-success"
                    : "badge-warning";


            // COMPROBAR SI YA SE SIGUE ESTA SECCIÓN
            const seguimientoActual =
                seguimientos.find(
                    seguimiento =>
                        seguimiento.activo === true &&
                        seguimiento.seccion &&
                        seguimiento.seccion.idSeccion ===
                            seccion.idSeccion
                );

            const estaSiguiendo = Boolean(
                seguimientoActual
            );
			fila.innerHTML = `<td>${curso.codigoCurso}</td><td>${curso.nombreCurso}<br><small>${seccion.horario}</small></td><td>Sección ${seccion.numeroSeccion}</td><td>${escaparTexto(nombreDocente)}</td><td><span class="badge-status ${claseEstado}">${seccion.estado}</span></td><td><button type="button" class="btn-ver-ficha" data-codigo="${curso.codigoCurso}" data-seccion="${seccion.numeroSeccion}">Ver Ficha</button><button type="button" class="btn-uvg btn-seguir" data-codigo="${curso.codigoCurso}" data-seccion="${seccion.numeroSeccion}" data-siguiendo="${estaSiguiendo}">${estaSiguiendo ? "🔔 Siguiendo" : "🔔 Seguir"}</button></td>`;



            fila.innerHTML = `
                <td>${curso.codigoCurso}</td>
                <td>
                    ${curso.nombreCurso}
                    <br>
                    <small>${seccion.horario}</small>
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
                        type="button"
                        class="btn-ver-ficha"
                        data-codigo="${curso.codigoCurso}"
                        data-seccion="${seccion.numeroSeccion}">
                        Ver Ficha
                    </button>

                    <button
                        type="button"
                        class="btn-uvg btn-seguir"
                        data-codigo="${curso.codigoCurso}"
                        data-seccion="${seccion.numeroSeccion}"
                        data-siguiendo="${estaSiguiendo}">
                        ${
                            estaSiguiendo
                                ? "🔔 Siguiendo"
                                : "🔔 Seguir"
                        }
                    </button>
                </td>
            `;

            tabla.appendChild(fila);
        });
    });


    // CONFIGURAR BOTONES SEGUIR
    const botonesSeguir =
        tabla.querySelectorAll(
            ".btn-seguir"
        );


    botonesSeguir.forEach(boton => {

        boton.addEventListener(
            "click",
            async (evento) => {

                evento.preventDefault();
                evento.stopPropagation();


                const botonSeleccionado =
                    evento.currentTarget;


                const codigoCurso =
                    botonSeleccionado.getAttribute(
                        "data-codigo"
                    );


                const numeroSeccion =
                    botonSeleccionado.getAttribute(
                        "data-seccion"
                    );


                await alternarSeguimiento(
                    codigoCurso,
                    numeroSeccion,
                    botonSeleccionado,
                    botonSeleccionado.dataset.siguiendo === "true"
                );
            }
        );
    });
}
// RF-04 / RF-05:
// SEGUIR O DEJAR DE SEGUIR UNA SECCIÓN


async function alternarSeguimiento(
    codigoCurso, numeroSeccion, botonElemento, estaSiguiendo
) {
    const API = "http://localhost:8080/api";
    botonElemento.disabled = true;

    try {
        const sesionRespuesta = await fetch(`${API}/auth/me`, {
            credentials: "include"
        });

        if (!sesionRespuesta.ok) {
            throw new Error("Sesion no valida. Inicia sesion nuevamente.");
        }

        const usuario = await sesionRespuesta.json();

        if (usuario.rol !== "ESTUDIANTE" || !usuario.carne) {
            throw new Error("No se pudo verificar el estudiante.");
        }

        const csrfRespuesta = await fetch(`${API}/auth/csrf`, {
            credentials: "include"
        });

        if (!csrfRespuesta.ok) {
            throw new Error("No se pudo obtener el token CSRF.");
        }

        const csrf = await csrfRespuesta.json();

        if (!csrf.token || !csrf.headerName) {
            throw new Error("Token CSRF invalido.");
        }

        const respuesta = await fetch(
            estaSiguiendo
                ? `${API}/seguimientos/desactivar`
                : `${API}/seguimientos`,
            {
                method: estaSiguiendo ? "PATCH" : "POST",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json",
                    [csrf.headerName]: csrf.token
                },
                body: JSON.stringify({
                    carnetEstudiante: usuario.carne,
                    codigoCurso: codigoCurso,
                    seccion: Number(numeroSeccion)
                })
            }
        );

        if (!respuesta.ok) {
            const mensaje = await respuesta.text();
            throw new Error(`HTTP ${respuesta.status}: ${mensaje}`);
        }

        const resultado = await respuesta.json();
        console.log("Seguimiento actualizado:", resultado);

        const nuevoEstado = !estaSiguiendo;
        botonElemento.dataset.siguiendo = String(nuevoEstado);
        botonElemento.textContent = nuevoEstado
            ? "🔔 Siguiendo"
            : "🔔 Seguir";

        alert(nuevoEstado
            ? "Seguimiento registrado correctamente."
            : "Seguimiento desactivado correctamente.");

    } catch (error) {
        console.error("Error al actualizar seguimiento:", error);
        botonElemento.dataset.siguiendo = String(estaSiguiendo);
        botonElemento.textContent = estaSiguiendo
            ? "🔔 Siguiendo"
            : "🔔 Seguir";
        alert(error.message);
    } finally {
        botonElemento.disabled = false;
    }
}

// RF-07:
// PESTAÑAS CATÁLOGO GENERAL / MIS ALERTAS

function configurarPestanas(
    tabCatalogo,
    tabAlertas
) {

    const panelCatalogo =
        document.getElementById(
            "panel-catalogo"
        );

    const panelAlertas =
        document.getElementById(
            "panel-alertas"
        );


    if (
        !tabCatalogo ||
        !tabAlertas ||
        !panelCatalogo ||
        !panelAlertas
    ) {

        return;
    }


    // MOSTRAR CATÁLOGO GENERAL
    tabCatalogo.addEventListener(
        "click",
        (evento) => {

            evento.preventDefault();


            panelCatalogo.hidden =
                false;

            panelAlertas.hidden =
                true;


            tabCatalogo.classList.add(
                "catalogo-tab-activa"
            );

            tabAlertas.classList.remove(
                "catalogo-tab-activa"
            );
        }
    );


    // MOSTRAR MIS ALERTAS
    tabAlertas.addEventListener(
        "click",
        async (evento) => {

            evento.preventDefault();


            panelCatalogo.hidden =
                true;

            panelAlertas.hidden =
                false;


            tabAlertas.classList.add(
                "catalogo-tab-activa"
            );

            tabCatalogo.classList.remove(
                "catalogo-tab-activa"
            );


            // Actualizar las alertas al abrir la pestaña.
            await cargarNotificaciones();
        }
    );
}


// RF-07:
// OBTENER NOTIFICACIONES

async function cargarNotificaciones() {

    const lista =
        document.getElementById(
            "lista-alertas"
        );

    const estado =
        document.getElementById(
            "estado-alertas"
        );


    if (
        !lista ||
        !estado
    ) {

        return;
    }


    estado.textContent =
        "Cargando alertas...";


    try {

        const respuesta =
            await fetch(
                "http://localhost:8080/api/notificaciones"
            );


        if (!respuesta.ok) {

            throw new Error(
                "No se pudieron obtener las notificaciones"
            );
        }


        const notificaciones =
            await respuesta.json();


        mostrarNotificaciones(
            notificaciones
        );


    } catch (error) {

        console.error(
            "Error al cargar notificaciones:",
            error
        );


        lista.innerHTML =
            "";


        estado.textContent =
            "No fue posible cargar las alertas. " +
            "Verifica que el backend esté activo.";
    }
}
// RF-07:
// MOSTRAR NOTIFICACIONES

function mostrarNotificaciones(
    notificaciones
) {

    const lista =
        document.getElementById(
            "lista-alertas"
        );

    const estado =
        document.getElementById(
            "estado-alertas"
        );

    const cantidad =
        document.getElementById(
            "cantidad-alertas"
        );


    if (
        !lista ||
        !estado ||
        !cantidad
    ) {

        return;
    }


    // CONTAR ALERTAS NO LEÍDAS
    const noLeidas =
        notificaciones.filter(
            notificacion =>
                !notificacion.leida
        ).length;


    cantidad.textContent =
        noLeidas;


    lista.innerHTML =
        "";


    // NO HAY NOTIFICACIONES
    if (notificaciones.length === 0) {

        estado.textContent =
            "No tienes alertas por el momento.";

        return;
    }


    estado.textContent =
        `${notificaciones.length} alerta(s), ` +
        `${noLeidas} sin leer`;


    notificaciones.forEach(
        notificacion => {

            const tarjeta =
                document.createElement(
                    "article"
                );


            tarjeta.className =
                `alerta ${
                    notificacion.leida
                        ? ""
                        : "alerta-no-leida"
                }`;


            const contenido =
                document.createElement(
                    "div"
                );


            const mensaje =
                document.createElement(
                    "p"
                );


            mensaje.textContent =
                notificacion.mensaje;


            const detalle =
                document.createElement(
                    "small"
                );


            detalle.className =
                "alerta-meta";


            const textoSeccion =
                notificacion.seccion
                    ? `Sección ${
                        notificacion
                            .seccion
                            .numeroSeccion
                    } · `
                    : "";


            detalle.textContent =
                textoSeccion +
                formatearFecha(
                    notificacion.fecha
                );


            contenido.append(
                mensaje,
                detalle
            );


            tarjeta.appendChild(
                contenido
            );


            // BOTÓN MARCAR COMO LEÍDA
            if (!notificacion.leida) {

                const boton =
                    document.createElement(
                        "button"
                    );


                // EVITAR QUE ACTÚE COMO SUBMIT
                boton.type =
                    "button";


                boton.className =
                    "btn-marcar-leida";


                boton.textContent =
                    "Marcar como leída";


                boton.addEventListener(
                    "click",
                    async (evento) => {

                        evento.preventDefault();
                        evento.stopPropagation();


                        // Evitar doble clic mientras se procesa.
                        boton.disabled =
                            true;


                        try {

                            await marcarNotificacionComoLeida(
                                notificacion.idNotificacion
                            );

                        } finally {

                            /*
                             * Si la actualización fue correcta,
                             * mostrarNotificaciones reconstruirá
                             * la tarjeta y este botón desaparecerá.
                             *
                             * Si hubo un error, volvemos a
                             * habilitar el botón existente.
                             */
                            if (boton.isConnected) {

                                boton.disabled =
                                    false;
                            }
                        }
                    }
                );


                tarjeta.appendChild(
                    boton
                );
            }


            lista.appendChild(
                tarjeta
            );
        }
    );
}


// FORMATEAR FECHA

function formatearFecha(fecha) {

    if (!fecha) {

        return "";
    }


    const valor =
        new Date(fecha);


    if (
        Number.isNaN(
            valor.getTime()
        )
    ) {

        return fecha;
    }


    return valor.toLocaleString(
        "es-GT"
    );
}


// RF-07:
// MARCAR NOTIFICACIÓN COMO LEÍDA

async function marcarNotificacionComoLeida(
    idNotificacion
) {

    const modalCatalogo =
        document.getElementById(
            "modal-catalogo-general"
        );

    const panelCatalogo =
        document.getElementById(
            "panel-catalogo"
        );

    const panelAlertas =
        document.getElementById(
            "panel-alertas"
        );

    const tabCatalogo =
        document.getElementById(
            "tab-catalogo"
        );

    const tabAlertas =
        document.getElementById(
            "tab-alertas"
        );


    try {

        // MARCAR NOTIFICACIÓN COMO LEÍDA
        const respuesta =
            await fetch(
                `http://localhost:8080/api/notificaciones/${idNotificacion}/leida`,
                {
                    method: "PATCH"
                }
            );


        if (!respuesta.ok) {

            throw new Error(
                "No se pudo actualizar la notificación"
            );
        }


        // VOLVER A CONSULTAR LAS NOTIFICACIONES
        const respuestaNotificaciones =
            await fetch(
                "http://localhost:8080/api/notificaciones"
            );


        if (!respuestaNotificaciones.ok) {

            throw new Error(
                "No se pudieron recargar las notificaciones"
            );
        }


        const notificaciones =
            await respuestaNotificaciones.json();


        // ACTUALIZAR ÚNICAMENTE LAS ALERTAS
        mostrarNotificaciones(
            notificaciones
        );


        // ASEGURAR QUE EL CATÁLOGO SIGA ABIERTO
        if (modalCatalogo) {

            modalCatalogo.style.display =
                "block";
        }


        // MANTENER OCULTO EL CATÁLOGO GENERAL
        if (panelCatalogo) {

            panelCatalogo.hidden =
                true;
        }


        // MANTENER VISIBLE EL PANEL DE ALERTAS
        if (panelAlertas) {

            panelAlertas.hidden =
                false;
        }


        // MANTENER LA PESTAÑA CORRECTA ACTIVA
        if (tabCatalogo) {

            tabCatalogo.classList.remove(
                "catalogo-tab-activa"
            );
        }


        if (tabAlertas) {

            tabAlertas.classList.add(
                "catalogo-tab-activa"
            );
        }


        /*
         * Mantener visualmente al usuario
         * dentro de la zona del catálogo/alertas.
         */
        if (modalCatalogo) {

            modalCatalogo.scrollIntoView({
                behavior: "auto",
                block: "start"
            });
        }


    } catch (error) {

        console.error(
            "Error al marcar la notificación como leída:",
            error
        );

        alert(
            error && error.message
                ? error.message
                : "No se pudo marcar la notificación como leída."
        );

        throw error;
    }
}

// RF-10: mostrar nombres ingresados como texto seguro en el catálogo.
function escaparTexto(valor) {
    const elemento = document.createElement("span");
    elemento.textContent = valor ?? "";
    return elemento.innerHTML;
}
