/* RF-12: presentacion por rol. NO es un mecanismo de seguridad. */
(function () {
  "use strict";
  const ROLES = Object.freeze({ ESTUDIANTE: "ESTUDIANTE", ADMINISTRADOR: "ADMINISTRADOR" });

  // El rol debe venir de una sesion autenticada y validada por el servidor.
  // No usar localStorage como prueba de identidad o autorizacion.
  function aplicarVistaRol(rolVerificado) {
    const rol = Object.values(ROLES).includes(rolVerificado) ? rolVerificado : null;
    document.querySelectorAll("[data-rol-permitido]").forEach((elemento) => {
      const rolesPermitidos = elemento.dataset.rolPermitido.split(",").map((r) => r.trim());
      elemento.hidden = !rol || !rolesPermitidos.includes(rol);
    });
  }

  window.ControlRolesVista = Object.freeze({ aplicarVistaRol });
})();
