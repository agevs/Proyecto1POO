"use strict";

// Pantalla "Pensum académico".
// Datos: GET /api/pensum (pensum oficial, Pensum 2025). Requiere sesión.

const API_PENSUM = "http://localhost:8080/api";
const contenedor = document.getElementById("pensum");
const estado = document.getElementById("estado");

let cursosPensum = [];
let rutaElegida = 1;

function crearElemento(etiqueta, texto) {
    const elemento = document.createElement(etiqueta);
    elemento.textContent = texto;
    return elemento;
}

function textoRuta(ruta) {
    return ruta === 0 ? "Común" : `Ruta ${ruta}`;
}

function crearCurso(curso) {
    const detalle = document.createElement("details");
    const codigo = curso.codigo ?? "Por confirmar";

    detalle.append(
        crearElemento(
            "summary",
            curso.codigo
                ? `${curso.codigo} — ${curso.nombre}`
                : curso.nombre
        ),
        crearElemento("p", `Código: ${codigo}`),
        crearElemento("p", `Nombre: ${curso.nombre}`),
        crearElemento("p", `Créditos: ${curso.creditos ?? "Sin información"}`),
        crearElemento("p", `Año ${curso.anio}, semestre ${curso.semestre}`),
        crearElemento("p", `Tipo: ${textoRuta(curso.ruta)}`)
    );

    return detalle;
}

function crearGrupo(titulo, cursos) {
    const seccion = document.createElement("section");
    seccion.className = "semestre";

    const creditos = cursos.reduce((suma, c) => suma + (c.creditos ?? 0), 0);
    seccion.append(crearElemento("h3", `${titulo} · ${creditos} créditos`));

    cursos.forEach(curso => seccion.append(crearCurso(curso)));
    return seccion;
}

function tieneUbicacion(curso) {
    return Number.isInteger(curso.anio)
        && curso.anio > 0
        && Number.isInteger(curso.semestre)
        && [1, 2].includes(curso.semestre);
}

function mostrarCursos() {
    contenedor.replaceChildren();

    // Cursos comunes (ruta 0) más los de la ruta elegida.
    const visibles = cursosPensum.filter(
        curso => curso.ruta === 0 || curso.ruta === rutaElegida
    );

    const ubicados = visibles.filter(tieneUbicacion);
    const pendientes = visibles.filter(curso => !tieneUbicacion(curso));
    const anios = [...new Set(ubicados.map(curso => curso.anio))]
        .sort((a, b) => a - b);

    for (const anio of anios) {
        contenedor.append(crearElemento("h2", `Año ${anio}`));

        for (const semestre of [1, 2]) {
            const grupo = ubicados.filter(
                curso => curso.anio === anio && curso.semestre === semestre
            );

            if (grupo.length > 0) {
                contenedor.append(
                    crearGrupo(`Semestre ${semestre}`, grupo)
                );
            }
        }
    }

    if (pendientes.length > 0) {
        contenedor.append(
            crearGrupo(
                "Ubicación en el pensum pendiente de verificar",
                pendientes
            )
        );
    }

    estado.textContent = visibles.length === 0
        ? "No hay cursos registrados."
        : `${visibles.length} curso(s) del Pensum 2025 (${textoRuta(rutaElegida)}).`;
}

function crearSelectorRuta() {
    const etiqueta = crearElemento("label", "Ruta de matemática: ");
    etiqueta.htmlFor = "ruta";

    const selector = document.createElement("select");
    selector.id = "ruta";
    selector.append(new Option("Ruta 1", "1"), new Option("Ruta 2", "2"));
    selector.addEventListener("change", () => {
        rutaElegida = Number(selector.value);
        mostrarCursos();
    });

    const barra = document.createElement("p");
    barra.append(etiqueta, selector);
    contenedor.before(barra);
}

async function cargarPensum() {
    try {
        const sesion = await fetch(`${API_PENSUM}/auth/me`, {
            credentials: "include",
            cache: "no-store"
        });

        if (sesion.status === 401) {
            window.location.replace("login.html");
            return;
        }

        if (!sesion.ok) {
            throw new Error("No se pudo verificar la sesión.");
        }

        const respuesta = await fetch(`${API_PENSUM}/pensum`, {
            credentials: "include",
            cache: "no-store"
        });

        if (!respuesta.ok) {
            throw new Error("No se pudo consultar el pensum.");
        }

        const cursos = await respuesta.json();

        if (!Array.isArray(cursos)) {
            throw new Error("El servidor devolvió un formato inesperado.");
        }

        cursosPensum = cursos;
        crearSelectorRuta();
        mostrarCursos();
    } catch (error) {
        estado.textContent =
            `${error.message} Comprueba que el backend esté encendido y recarga la página.`;
    }
}

cargarPensum();
