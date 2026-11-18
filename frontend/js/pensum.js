"use strict";

// Pantalla "Pensum académico".
// Datos: GET /api/pensum (pensum oficial, Pensum 2025). Requiere sesión.

const API_PENSUM = "http://localhost:8080/api";
const contenedor = document.getElementById("pensum");
const estado = document.getElementById("estado");

let cursosPensum = [];
let rutaElegida = 1;

// Iconos (SVG en línea, sin librerías externas). Cada uno cumple una función:
// ayudar a identificar de un vistazo para qué sirve cada dato.
const ICONOS = {
    anio: '<rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4M8 2v4M3 10h18"/>',
    semestre: '<circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/>',
    curso: '<path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>',
    codigo: '<path d="M4 9h16M4 15h16M10 3 8 21M16 3l-2 18"/>',
    nombre: '<polyline points="4 7 4 4 20 4 20 7"/><line x1="9" y1="20" x2="15" y2="20"/><line x1="12" y1="4" x2="12" y2="20"/>',
    creditos: '<polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>',
    comun: '<polygon points="12 2 2 7 12 12 22 7 12 2"/><polyline points="2 17 12 22 22 17"/><polyline points="2 12 12 17 22 12"/>',
    ruta: '<circle cx="6" cy="19" r="3"/><path d="M9 19h8.5a3.5 3.5 0 0 0 0-7h-11a3.5 3.5 0 0 1 0-7H15"/><circle cx="18" cy="5" r="3"/>',
    pendiente: '<circle cx="12" cy="12" r="10"/><path d="M12 8v4M12 16h.01"/>',
    info: '<circle cx="12" cy="12" r="10"/><path d="M12 16v-4M12 8h.01"/>',
    expandir: '<polyline points="7 13 12 18 17 13"/><polyline points="7 6 12 11 17 6"/>',
    contraer: '<polyline points="7 11 12 6 17 11"/><polyline points="7 18 12 13 17 18"/>'
};

// Significado de cada icono (se muestra en la leyenda de la pantalla).
const LEYENDA = [
    ["curso", "Curso del pensum. Haz clic en la fila para ver su información."],
    ["codigo", "Código oficial del curso."],
    ["creditos", "Créditos del curso, o total de créditos del semestre."],
    ["anio", "Año de la carrera en el que se lleva el curso."],
    ["semestre", "Semestre del año (1 o 2)."],
    ["comun", "Curso común: se lleva con cualquier ruta de matemática."],
    ["ruta", "Curso propio de la Ruta 1 o la Ruta 2 de matemática."],
    ["pendiente", "El código oficial todavía está por confirmar."]
];

const SVG_NS = "http://www.w3.org/2000/svg";

function crearIcono(nombre) {
    const contenedorIcono = document.createElement("span");
    contenedorIcono.className = `icono icono-${nombre}`;
    contenedorIcono.setAttribute("aria-hidden", "true");

    const svg = document.createElementNS(SVG_NS, "svg");
    svg.setAttribute("viewBox", "0 0 24 24");
    svg.setAttribute("focusable", "false");
    // Los trazos son constantes del código (no vienen del servidor).
    svg.innerHTML = ICONOS[nombre];

    contenedorIcono.append(svg);
    return contenedorIcono;
}

function crearElemento(etiqueta, texto, clase) {
    const elemento = document.createElement(etiqueta);
    elemento.textContent = texto;
    if (clase) {
        elemento.className = clase;
    }
    return elemento;
}

function textoRuta(ruta) {
    return ruta === 0 ? "Común" : `Ruta ${ruta}`;
}

// Etiqueta de color con icono: "Común", "Ruta 1", "Por confirmar"...
function crearEtiqueta(icono, texto, clase) {
    const etiqueta = document.createElement("span");
    etiqueta.className = `etiqueta ${clase}`;
    etiqueta.append(crearIcono(icono), document.createTextNode(texto));
    return etiqueta;
}

function crearTituloConIcono(etiqueta, icono, texto) {
    const titulo = document.createElement(etiqueta);
    titulo.append(crearIcono(icono), document.createTextNode(texto));
    return titulo;
}

// Una fila del detalle: icono + nombre del dato + valor.
function crearDato(icono, nombre, valor) {
    const fila = document.createElement("p");
    fila.className = "dato";
    fila.append(
        crearIcono(icono),
        crearElemento("span", nombre, "dato-nombre"),
        crearElemento("span", valor, "dato-valor")
    );
    return fila;
}

function crearResumenCurso(curso) {
    const resumen = document.createElement("summary");

    resumen.append(
        crearIcono("curso"),
        curso.codigo
            ? crearElemento("span", curso.codigo, "codigo")
            : crearEtiqueta("pendiente", "Por confirmar", "etiqueta-pendiente"),
        crearElemento("span", curso.nombre, "nombre-curso"),
        crearEtiqueta(
            "creditos",
            `${curso.creditos ?? "?"} cr.`,
            "etiqueta-creditos"
        ),
        curso.ruta === 0
            ? crearEtiqueta("comun", "Común", "etiqueta-comun")
            : crearEtiqueta("ruta", textoRuta(curso.ruta), "etiqueta-ruta")
    );

    return resumen;
}

function crearCurso(curso) {
    const detalle = document.createElement("details");
    const codigo = curso.codigo ?? "Por confirmar";
    const panel = document.createElement("div");
    panel.className = "detalle-curso";

    panel.append(
        crearDato("codigo", "Código", codigo),
        crearDato("nombre", "Nombre", curso.nombre),
        crearDato(
            "creditos",
            "Créditos",
            String(curso.creditos ?? "Sin información")
        ),
        crearDato("anio", "Año", String(curso.anio)),
        crearDato("semestre", "Semestre", String(curso.semestre)),
        crearDato("ruta", "Tipo", textoRuta(curso.ruta))
    );

    detalle.append(crearResumenCurso(curso), panel);
    return detalle;
}

function crearGrupo(titulo, cursos) {
    const seccion = document.createElement("section");
    seccion.className = "semestre";

    const creditos = cursos.reduce((suma, c) => suma + (c.creditos ?? 0), 0);
    const encabezado = crearTituloConIcono("h3", "semestre", titulo);
    encabezado.append(
        crearEtiqueta("creditos", `${creditos} créditos`, "etiqueta-total")
    );
    seccion.append(encabezado);

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
        contenedor.append(crearTituloConIcono("h2", "anio", `Año ${anio}`));

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

    actualizarResumen(visibles, anios.length);
    mostrarEstado(
        visibles.length === 0
            ? "No hay cursos registrados."
            : `${visibles.length} curso(s) del Pensum 2025 (${textoRuta(rutaElegida)}).`
    );
}

let resumenGeneral = null;

function crearTarjetaResumen(icono, titulo) {
    const tarjeta = document.createElement("div");
    tarjeta.className = "tarjeta-resumen";

    const valor = document.createElement("strong");
    tarjeta.append(
        crearIcono(icono),
        valor,
        crearElemento("span", titulo)
    );
    return { tarjeta, valor };
}

function crearResumenGeneral() {
    const cursos = crearTarjetaResumen("curso", "cursos");
    const creditos = crearTarjetaResumen("creditos", "créditos en total");
    const anios = crearTarjetaResumen("anio", "años de carrera");

    const contenedorResumen = document.createElement("div");
    contenedorResumen.className = "resumen-general";
    contenedorResumen.append(cursos.tarjeta, creditos.tarjeta, anios.tarjeta);

    resumenGeneral = {
        contenedor: contenedorResumen,
        cursos: cursos.valor,
        creditos: creditos.valor,
        anios: anios.valor
    };
    return contenedorResumen;
}

function actualizarResumen(visibles, anios) {
    resumenGeneral.cursos.textContent = String(visibles.length);
    resumenGeneral.creditos.textContent = String(
        visibles.reduce((suma, c) => suma + (c.creditos ?? 0), 0)
    );
    resumenGeneral.anios.textContent = String(anios);
}

function crearLeyenda() {
    const leyenda = document.createElement("details");
    leyenda.className = "leyenda";

    const titulo = document.createElement("summary");
    titulo.append(
        crearIcono("info"),
        document.createTextNode("¿Qué significa cada icono?")
    );

    const lista = document.createElement("ul");
    for (const [icono, significado] of LEYENDA) {
        const item = document.createElement("li");
        item.append(crearIcono(icono), crearElemento("span", significado));
        lista.append(item);
    }

    leyenda.append(titulo, lista);
    return leyenda;
}

function cambiarTodos(abrir) {
    contenedor.querySelectorAll("details").forEach(detalle => {
        detalle.open = abrir;
    });
}

function crearBotonMasivo(icono, texto, abrir) {
    const boton = document.createElement("button");
    boton.type = "button";
    boton.className = "boton-masivo";
    boton.append(crearIcono(icono), document.createTextNode(texto));
    boton.addEventListener("click", () => cambiarTodos(abrir));
    return boton;
}

function mostrarEstado(mensaje, esError = false) {
    estado.textContent = mensaje;
    estado.classList.toggle("error", esError);
}

function crearSelectorRuta() {
    const etiqueta = crearElemento("label", "Ruta de matemática:");
    etiqueta.htmlFor = "ruta";

    const selector = document.createElement("select");
    selector.id = "ruta";
    selector.append(new Option("Ruta 1", "1"), new Option("Ruta 2", "2"));
    selector.addEventListener("change", () => {
        rutaElegida = Number(selector.value);
        mostrarCursos();
    });

    const barra = document.createElement("div");
    barra.className = "selector-ruta";
    barra.append(
        crearIcono("ruta"),
        etiqueta,
        selector,
        crearBotonMasivo("expandir", "Expandir todo", true),
        crearBotonMasivo("contraer", "Contraer todo", false)
    );
    contenedor.before(crearResumenGeneral(), barra, crearLeyenda());
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
        mostrarEstado(
            `${error.message} Comprueba que el backend esté encendido y recarga la página.`,
            true
        );
    }
}

cargarPensum();
