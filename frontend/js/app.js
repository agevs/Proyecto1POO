document.addEventListener("DOMContentLoaded", () => {

    // Elementos de la interfaz
    const linkAsignaciones = document.getElementById("link-asignaciones");
    const btnAbrirCatalogo = document.getElementById("btn-abrir-catalogo");
    const modalCatalogo = document.getElementById("modal-catalogo-general");

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