"use strict";
const API = "http://localhost:8080/api";
const porId = id => document.getElementById(id);
let cursos = [];
function mensaje(texto, error = false) {
    porId("mensaje").textContent = texto;
    porId("mensaje").dataset.error = String(error);
}
async function solicitar(ruta, metodo = "GET", datos) {
    const headers = {};

    if (datos !== undefined) {
        headers["Content-Type"] = "application/json";
    }

    if (!["GET", "HEAD", "OPTIONS"].includes(metodo)) {
        const csrfRespuesta = await fetch(API + "/auth/csrf", {
            credentials: "include"
        });

        if (!csrfRespuesta.ok) {
            throw new Error("No se pudo obtener el token CSRF.");
        }

        const csrf = await csrfRespuesta.json();

        if (!csrf.token) {
            throw new Error("Token CSRF no disponible.");
        }

        headers[csrf.headerName || "X-XSRF-TOKEN"] = csrf.token;
    }

    const respuesta = await fetch(API + ruta, {
        method: metodo,
        credentials: "include",
        headers,
        body: datos !== undefined ? JSON.stringify(datos) : undefined
    });

    if (!respuesta.ok) {
        const errores = {
            400: "Revisa los datos obligatorios.",
            401: "Debes iniciar sesion.",
            403: "No tienes permisos de administrador.",
            404: "El recurso solicitado no existe.",
            409: "Ya existe un docente con ese ID."
        };

        throw new Error(
            errores[respuesta.status] ||
            "No se pudo completar la operacion."
        );
    }

    return respuesta.json();
}
function opciones(id, valores, valor, texto) {
    const select = porId(id);
    select.replaceChildren(new Option("Selecciona una opción", ""));
    valores.forEach(item => select.add(new Option(texto(item), valor(item))));
}
function actualizarSecciones() {
    const curso = cursos.find(c => c.codigoCurso === porId("curso").value);
    opciones("seccion", curso?.secciones || [], s => s.numeroSeccion,
        s => `Sección ${s.numeroSeccion} — ${s.horario}`);
    actualizarActual();
}
function actualizarActual() {
    const curso = cursos.find(c => c.codigoCurso === porId("curso").value);
    const seccion = curso?.secciones.find(s => String(s.numeroSeccion) === porId("seccion").value);
    porId("docente-actual").textContent = seccion
        ? `Docente actual: ${seccion.docente?.nombre || "STAFF"}. Estado: ${seccion.estado}` : "";
}
async function cargar() {
    const [listaCursos, docentes] = await Promise.all([
        solicitar("/cursos"), solicitar("/administracion/docentes")]);
    cursos = listaCursos;
    opciones("curso", cursos, c => c.codigoCurso, c => `${c.codigoCurso} — ${c.nombreCurso}`);
    opciones("docente", docentes, d => d.idDocente, d => `${d.nombre} (${d.idDocente})`);
    actualizarSecciones();
    porId("confirmar").disabled = docentes.length === 0 || cursos.length === 0;
}
porId("curso").addEventListener("change", actualizarSecciones);
porId("seccion").addEventListener("change", actualizarActual);
porId("registro").addEventListener("submit", async evento => {
    evento.preventDefault();
    const boton = evento.currentTarget.querySelector("button");
    boton.disabled = true;
    let guardado = false;
    try {
        const docente = await solicitar("/administracion/docentes", "POST", {
            idDocente: Number(porId("id-docente").value),
            nombre: porId("nombre").value.trim(), correo: porId("correo").value.trim()});
        guardado = true;
        porId("registro").reset();
        await cargar();
        porId("docente").value = String(docente.idDocente);
        mensaje("Docente guardado. Selecciona el curso y la sección para confirmarlo.");
    } catch (error) {
        mensaje(guardado ? "El docente se guardó, pero no se pudieron actualizar las listas. Recarga la página."
            : error.message, true);
    } finally { boton.disabled = false; }
});
porId("confirmacion").addEventListener("submit", async evento => {
    evento.preventDefault();
    const boton = porId("confirmar");
    boton.disabled = true;
    try {
        const codigo = porId("curso").value;
        const numero = porId("seccion").value;
        const seccion = await solicitar(`/administracion/cursos/${encodeURIComponent(codigo)}/secciones/${numero}/docente`,
            "PATCH", {idDocente: Number(porId("docente").value)});
        const curso = cursos.find(c => c.codigoCurso === codigo);
        const indice = curso.secciones.findIndex(s => String(s.numeroSeccion) === numero);
        curso.secciones[indice] = seccion;
        actualizarActual();
        mensaje(`Docente ${seccion.docente.nombre} confirmado y guardado. Vuelve al catálogo para consultarlo.`);
    } catch (error) { mensaje(error.message, true); }
    finally { boton.disabled = false; }
});
cargar().then(() => mensaje("Datos cargados. Puedes registrar o seleccionar un docente."))
    .catch(error => mensaje("No se pudieron cargar los datos. Verifica que el backend esté activo. " + error.message, true));
