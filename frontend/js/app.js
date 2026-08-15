document.addEventListener("DOMContentLoaded", () => {
    const linkAsignaciones = document.getElementById("link-asignaciones");
    const btnAbrirCatalogo = document.getElementById("btn-abrir-catalogo");
    const modalCatalogo = document.getElementById("modal-catalogo-general");

    // Si estan en el index y dan clic al enlace de asignaciones
    if (linkAsignaciones) {
        linkAsignaciones.addEventListener("click", (e) => {
            e.preventDefault();
            // Redirige a la pantalla de carga (splash)
            window.location.href = "splash.html";
        });
    }

    // Si estan en el dashboard, manejar la apertura del Catálogo General (Iteración 3)
    if (btnAbrirCatalogo && modalCatalogo) {
        btnAbrirCatalogo.addEventListener("click", () => {
            if (modalCatalogo.style.display === "none" || modalCatalogo.style.display === "") {
                modalCatalogo.style.display = "block";
                modalCatalogo.scrollIntoView({ behavior: 'smooth' });
            } else {
                modalCatalogo.style.display = "none";
            }
        });
    }
});