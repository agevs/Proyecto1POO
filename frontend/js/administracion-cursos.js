"use strict";

const API_CURSOS = "http://localhost:8080/api";
const campo = id => document.getElementById(id);
let catalogo = [];

function mensaje(texto, error = false) {
    campo("mensaje").textContent = texto;
    campo("mensaje").dataset.error = String(error);
}

async function solicitar(ruta, metodo = "GET", datos) {
    const headers = {};

    if (datos !== undefined) {
        headers["Content-Type"] = "application/json";
    }

    if (!["GET", "HEAD", "OPTIONS"].includes(metodo)) {
        const respuestaCsrf = await fetch(
            API_CURSOS + "/auth/csrf",
            { credentials: "include", cache: "no-store" }
        );

        if (!respuestaCsrf.ok) {
            throw new Error("No se pudo obtener el token CSRF.");
        }

        const csrf = await respuestaCsrf.json();
        headers[csrf.headerName || "X-XSRF-TOKEN"] = csrf.token;
    }

    const respuesta = await fetch(API_CURSOS + ruta, {
        method: metodo,
        credentials: "include",
        headers,
        body: datos === undefined ? undefined : JSON.stringify(datos)
    });

    if (!respuesta.ok) {
        const errores = {
            400: "Revisa los datos ingresados.",
            401: "Debes iniciar sesión.",
            403: "No tienes permisos de administrador.",
            404: "El curso o la sección no existe.",
            409: "El registro ya existe."
        };

        throw new Error(
            errores[respuesta.status] ||
            `Error del servidor: HTTP ${respuesta.status}`
        );
    }

    return respuesta.status === 204 ? null : respuesta.json();
}

function llenarSelect(id, elementos, valor, etiqueta) {
    const select = campo(id);
    select.replaceChildren(new Option("Selecciona una opción", ""));

    elementos.forEach(elemento => {
        select.add(new Option(etiqueta(elemento), valor(elemento)));
    });
}

async function cargarCursos() {
    catalogo = await solicitar("/cursos");

    const etiqueta = c =>
        `${c.codigoCurso} - ${c.nombreCurso}`;

    ["curso-editar", "curso-seccion", "curso-seccion-editar", "curso-estado"]
        .forEach(id => llenarSelect(
            id, catalogo, c => c.codigoCurso, etiqueta
        ));

    actualizarCurso();
    actualizarSecciones();
    actualizarSeccionesEstado();
}

function actualizarCurso() {
    const curso = catalogo.find(
        c => c.codigoCurso === campo("curso-editar").value
    );

    campo("editar-nombre").value = curso?.nombreCurso || "";
    campo("editar-creditos").value = curso?.creditos ?? "";
}

function actualizarSecciones() {
    const curso = catalogo.find(
        c => c.codigoCurso === campo("curso-seccion-editar").value
    );

    llenarSelect(
        "seccion-editar",
        curso?.secciones || [],
        s => s.idSeccion,
        s => `Seccion ${s.numeroSeccion} - ${s.horario}`
    );

    actualizarDatosSeccion();
}

function actualizarDatosSeccion() {
    const curso = catalogo.find(
        c => c.codigoCurso === campo("curso-seccion-editar").value
    );

    const seccion = curso?.secciones?.find(
        s => String(s.idSeccion) === campo("seccion-editar").value
    );

    campo("editar-numero").value = seccion?.numeroSeccion ?? "";
    campo("editar-horario").value = seccion?.horario || "";
    campo("editar-cupos").value = seccion?.cuposDisponibles ?? "";
}

async function ejecutar(formulario, operacion) {
    const boton = formulario.querySelector('button[type="submit"]');
    boton.disabled = true;

    try {
        await operacion();
        mensaje("Operacion guardada correctamente en SQLite.");
        await cargarCursos();
    } catch (error) {
        mensaje(error.message, true);
    } finally {
        boton.disabled = false;
    }
}

campo("curso-editar").addEventListener("change", actualizarCurso);
campo("curso-seccion-editar").addEventListener("change", actualizarSecciones);
campo("seccion-editar").addEventListener("change", actualizarDatosSeccion);

campo("crear-curso").addEventListener("submit", evento => {
    evento.preventDefault();

    ejecutar(evento.currentTarget, async () => {
        await solicitar("/cursos", "POST", {
            codigoCurso: campo("nuevo-codigo").value.trim(),
            nombreCurso: campo("nuevo-nombre").value.trim(),
            creditos: Number(campo("nuevo-creditos").value)
        });

        campo("crear-curso").reset();
    });
});

campo("editar-curso").addEventListener("submit", evento => {
    evento.preventDefault();

    ejecutar(evento.currentTarget, async () => {
        const codigo = campo("curso-editar").value;

        await solicitar(
            `/cursos/${encodeURIComponent(codigo)}`,
            "PUT",
            {
                nombreCurso: campo("editar-nombre").value.trim(),
                creditos: Number(campo("editar-creditos").value)
            }
        );
    });
});

campo("crear-seccion").addEventListener("submit", evento => {
    evento.preventDefault();

    ejecutar(evento.currentTarget, async () => {
        const codigo = campo("curso-seccion").value;

        await solicitar(
            `/cursos/${encodeURIComponent(codigo)}/secciones`,
            "POST",
            {
                numeroSeccion: Number(campo("nueva-seccion").value),
                horario: campo("nuevo-horario").value.trim(),
                cuposDisponibles: Number(campo("nuevos-cupos").value),
                estado: campo("nuevo-estado").value
            }
        );

        campo("crear-seccion").reset();
    });
});

campo("editar-seccion").addEventListener("submit", evento => {
    evento.preventDefault();

    ejecutar(evento.currentTarget, async () => {
        const id = campo("seccion-editar").value;

        // RF-11: conservar el estado actual de la seccion.
        const codigoCurso = campo("curso-seccion-editar").value;
        const curso = catalogo.find(
            c => c.codigoCurso === codigoCurso
        );

        const seccion = curso?.secciones?.find(
            s => String(s.idSeccion) === id
        );

        if (!seccion) {
            throw new Error("Selecciona una seccion valida.");
        }

        await solicitar(
            `/cursos/secciones/${encodeURIComponent(id)}`,
            "PUT",
            {
                numeroSeccion: Number(campo("editar-numero").value),
                horario: campo("editar-horario").value.trim(),
                cuposDisponibles: Number(campo("editar-cupos").value),
                estado: seccion.estado
            }
        );
    });
});

async function iniciar() {
    try {
        const usuario = await solicitar("/auth/me");

        if (usuario.rol !== "ADMINISTRADOR") {
            throw new Error(
                "Esta pantalla requiere una sesion administrativa."
            );
        }

        await cargarCursos();

        mensaje("Datos cargados. Puedes administrar cursos y secciones.");

    } catch (error) {
        mensaje(error.message, true);

        document.querySelectorAll("form").forEach(
            formulario => formulario.hidden = true
        );
    }
}

document.querySelectorAll("form").forEach(
    formulario => formulario.hidden = true
);

iniciar().then(() => {
    if (campo("mensaje").dataset.error !== "true") {
        document.querySelectorAll("form").forEach(
            formulario => formulario.hidden = false
        );
    }
});


// RF-09: Seleccionar las secciones del curso.
function actualizarSeccionesEstado() {
    const codigo = campo("curso-estado").value;

    const curso = catalogo.find(
        c => c.codigoCurso === codigo
    );

    llenarSelect(
        "seccion-estado",
        curso?.secciones || [],
        s => s.idSeccion,
        s => `Seccion ${s.numeroSeccion} - ${s.estado}`
    );

    actualizarEstadoSeleccionado();
}

// RF-09: Mostrar el estado actual de la seccion.
function actualizarEstadoSeleccionado() {
    const codigo = campo("curso-estado").value;
    const id = campo("seccion-estado").value;

    const curso = catalogo.find(
        c => c.codigoCurso === codigo
    );

    const seccion = curso?.secciones?.find(
        s => String(s.idSeccion) === id
    );

    campo("nuevo-estado-rf09").value =
        seccion?.estado === "Confirmado"
            ? "Confirmado"
            : "Pendiente";
}

// RF-09: Eventos de seleccion.
campo("curso-estado").addEventListener(
    "change",
    actualizarSeccionesEstado
);

campo("seccion-estado").addEventListener(
    "change",
    actualizarEstadoSeleccionado
);

// RF-09: Guardar el cambio mediante PATCH.
campo("cambiar-estado-seccion").addEventListener(
    "submit",
    evento => {
        evento.preventDefault();

        ejecutar(evento.currentTarget, async () => {
            const codigo = campo("curso-estado").value;
            const id = campo("seccion-estado").value;

            const curso = catalogo.find(
                c => c.codigoCurso === codigo
            );

            const seccion = curso?.secciones?.find(
                s => String(s.idSeccion) === id
            );

            if (!seccion) {
                throw new Error("Selecciona una seccion valida.");
            }

            const estado = campo("nuevo-estado-rf09").value;

            await solicitar(
                `/administracion/cursos/${encodeURIComponent(codigo)}` +
                `/secciones/${seccion.numeroSeccion}/estado`,
                "PATCH",
                { estado }
            );
        });
    }
);
