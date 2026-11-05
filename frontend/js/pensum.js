"use strict";

const API_PENSUM = "http://localhost:8080/api";
const contenedor = document.getElementById("pensum");
const estado = document.getElementById("estado");

function crearElemento(etiqueta, texto) {
    const elemento = document.createElement(etiqueta);
    elemento.textContent = texto;
    return elemento;
}

function crearCurso(curso) {
    const detalle = document.createElement("details");

    detalle.append(
        crearElemento(
            "summary",
            `${curso.codigoCurso} — ${curso.nombreCurso}`
        ),
        crearElemento("p", `Código: ${curso.codigoCurso}`),
        crearElemento("p", `Nombre: ${curso.nombreCurso}`),
        crearElemento("p", `Créditos: ${curso.creditos ?? "Sin información"}`)
    );

    return detalle;
}

function crearGrupo(titulo, cursos) {
    const seccion = document.createElement("section");
    seccion.className = "semestre";
    seccion.append(crearElemento("h3", titulo));

    cursos.forEach(curso => seccion.append(crearCurso(curso)));
    return seccion;
}

function tieneUbicacion(curso) {
    return Number.isInteger(curso.anio)
        && curso.anio > 0
        && Number.isInteger(curso.semestre)
        && [1, 2].includes(curso.semestre);
}

function mostrarCursos(cursos) {
    contenedor.replaceChildren();

    const ubicados = cursos.filter(tieneUbicacion);
    const pendientes = cursos.filter(curso => !tieneUbicacion(curso));
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

    estado.textContent = cursos.length === 0
        ? "No hay cursos registrados."
        : `${cursos.length} curso(s) cargado(s). `
        + "Esta vista todavía no representa el pensum oficial completo.";
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

        const respuesta = await fetch(`${API_PENSUM}/cursos`, {
            credentials: "include",
            cache: "no-store"
        });

        if (!respuesta.ok) {
            throw new Error("No se pudieron consultar los cursos.");
        }

        const cursos = await respuesta.json();

        if (!Array.isArray(cursos)) {
            throw new Error("El servidor devolvió un formato inesperado.");
        }

        mostrarCursos(cursos);
    } catch (error) {
        estado.textContent =
            `${error.message} Comprueba que el backend esté encendido y recarga la página.`;
    }
}

cargarPensum();