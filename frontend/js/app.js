document.addEventListener("DOMContentLoaded", () => {

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
        const respuestaSeguimientos =
            await fetch(
                "http://localhost:8080/api/seguimientos/estudiante/26594"
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

    } catch (error) {

        console.error(
            "Error al cargar cursos:",
            error
        );
    }
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
            const estaSiguiendo =
                seguimientos.some(
                    seguimiento =>

                        seguimiento.activo === true &&

                        seguimiento.seccion &&

                        seguimiento.seccion.idSeccion ===
                            seccion.idSeccion
                );


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

                    <span
                        class="badge-status ${claseEstado}">

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
                        ${estaSiguiendo ? "disabled" : ""}>

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

        // SI YA ESTÁ SIGUIENDO,
        // NO AGREGAR EVENTO
        if (boton.disabled) {
            return;
        }


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


                await seguirSeccion(
                    codigoCurso,
                    numeroSeccion,
                    botonSeleccionado
                );
            }
        );
    });
}

// RF-04: SEGUIR UNA SECCIÓN

async function seguirSeccion(
    codigoCurso,
    numeroSeccion,
    botonElemento
) {

    const idEstudiante = 26594;


    try {

        // EVITAR DOBLE CLIC
        botonElemento.disabled =
            true;


        const respuesta =
            await fetch(
                "http://localhost:8080/api/seguimientos",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        carnetEstudiante:
                            idEstudiante,

                        codigoCurso:
                            codigoCurso,

                        seccion:
                            parseInt(
                                numeroSeccion
                            )
                    })
                }
            );


        if (!respuesta.ok) {

            const mensajeError =
                await respuesta.text();


            throw new Error(
                mensajeError ||
                "No se pudo registrar el seguimiento."
            );
        }


        const resultado =
            await respuesta.json();


        console.log(
            "Seguimiento registrado:",
            resultado
        );


        // ACTUALIZAR BOTÓN
        botonElemento.textContent =
            "🔔 Siguiendo";

        botonElemento.disabled =
            true;


        alert(
            `Has comenzado a seguir exitosamente ` +
            `la sección ${numeroSeccion} ` +
            `del curso ${codigoCurso}`
        );


    } catch (error) {

        console.error(
            "Error al registrar seguimiento:",
            error
        );


        botonElemento.disabled =
            false;


        botonElemento.textContent =
            "🔔 Seguir";


        alert(
            "Ocurrió un error al intentar seguir " +
            "la sección. Inténtalo de nuevo."
        );
    }
}

// RF-07: PESTAÑAS CATÁLOGO / MIS ALERTAS

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


    // MOSTRAR CATÁLOGO
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


            await cargarNotificaciones();
        }
    );
}

// RF-07: OBTENER NOTIFICACIONES

async function cargarNotificaciones() {

    const lista =
        document.getElementById(
            "lista-alertas"
        );

    const estado =
        document.getElementById(
            "estado-alertas"
        );


    if (!lista || !estado) {
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

// RF-07: MOSTRAR NOTIFICACIONES

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


                        await marcarNotificacionComoLeida(
                            notificacion.idNotificacion
                        );
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

// RF-07: MARCAR NOTIFICACIÓN COMO LEÍDA

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


        // MANTENER OCULTO EL CATÁLOGO
        if (panelCatalogo) {

            panelCatalogo.hidden =
                true;
        }


        // MANTENER VISIBLES LAS ALERTAS
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


        // VOLVER VISUALMENTE AL PANEL DE ALERTAS
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
    }
}