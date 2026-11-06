const formulario = document.getElementById("form-adelanto");
const resultado = document.getElementById("resultado");

const cursos = {
    CC3088: {
        anio: 3,
        requisito: "CC2016"
    },
    CC3089: {
        anio: 4,
        requisito: "CC3088"
    }
};

formulario.addEventListener("submit", async (evento) => {
    evento.preventDefault();

    const codigo = document.getElementById("curso").value;
    const curso = cursos[codigo];

    const datos = {
        promedio: Number(document.getElementById("promedio").value),
        anioEstudiante: Number(document.getElementById("anio").value),
        anioCurso: curso.anio,
        cursosAprobados: document.getElementById("aprobado").checked
            ? [curso.requisito]
            : [],
        prerrequisitos: [curso.requisito]
    };

    resultado.className = "resultado";
    resultado.textContent = "Evaluando condiciones academicas...";

    try {
        const respuestaCsrf = await fetch(
            "http://localhost:8080/api/auth/csrf",
            { credentials: "include" }
        );

        if (!respuestaCsrf.ok) {
            throw new Error("No se pudo obtener CSRF");
        }

        const csrf = await respuestaCsrf.json();

        if (!csrf.token || !csrf.headerName) {
            throw new Error("Token CSRF incompleto");
        }

        const respuesta = await fetch(
            "http://localhost:8080/api/adelantos/evaluar",
            {
                method: "POST",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json",
                    [csrf.headerName]: csrf.token
                },
                body: JSON.stringify(datos)
            }
        );

        if (!respuesta.ok) {
            resultado.className = "resultado rechazado";
            resultado.textContent =
                "No se pudo evaluar. HTTP " + respuesta.status +
                ". Comprueba la sesion y la proteccion CSRF.";
            return;
        }

        const evaluacion = await respuesta.json();

        resultado.className = evaluacion.elegible
            ? "resultado aprobado"
            : "resultado rechazado";

        resultado.textContent = evaluacion.mensaje;

    } catch (error) {
        resultado.className = "resultado rechazado";
        resultado.textContent =
            "No se pudo conectar con el backend.";
    }
});
