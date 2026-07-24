(() => {
    document.addEventListener("DOMContentLoaded", () => {
        const form = document.querySelector("#login-form");
        const password = document.querySelector("#senha");
        const toggle = document.querySelector("[data-password-toggle]");
        toggle.addEventListener("click", () => {
            const visible = password.type === "text";
            password.type = visible ? "password" : "text";
            toggle.textContent = visible ? "Mostrar" : "Ocultar";
            toggle.setAttribute("aria-label", visible ? "Mostrar senha" : "Ocultar senha");
        });
        form.addEventListener("submit", async (event) => {
            event.preventDefault();
            Clinic.clearFieldErrors(form);
            const button = form.querySelector("[type=submit]");
            Clinic.setLoading(button, true, "Entrando…");
            try {
                const data = await Clinic.request("/api/auth/login", {
                    method: "POST",
                    body: JSON.stringify({ email: form.email.value.trim(), senha: form.senha.value })
                }, false);
                sessionStorage.setItem("accessToken", data.accessToken);
                sessionStorage.setItem("email", data.email);
                sessionStorage.setItem("role", data.role);
                sessionStorage.setItem("usuarioId", String(data.usuarioId));
                if (data.profissionalId) {
                    sessionStorage.setItem("profissionalId", String(data.profissionalId));
                    sessionStorage.setItem("profissionalNome", data.profissionalNome);
                }
                window.location.assign(
                    data.role === "PROFISSIONAL"
                        ? "/profissional"
                        : "/admin"
                );
            } catch (error) {
                Clinic.showFieldErrors(form, error.validationErrors);
                const message = error.status === 401 ? "E-mail ou senha inválidos." : error.message;
                document.querySelector("#login-message").textContent = message;
            } finally {
                Clinic.setLoading(button, false);
            }
        });
    });
})();
