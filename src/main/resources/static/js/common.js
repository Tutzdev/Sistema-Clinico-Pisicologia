(() => {
    const storageKeys = ["accessToken", "email", "role", "usuarioId"];

    const parseProblem = async (response) => {
        let body = {};
        try {
            body = await response.json();
        } catch (_) {
            body = {};
        }
        return {
            status: response.status,
            message: body.detail || (response.status === 403
                ? "Você não tem permissão para realizar esta ação."
                : "Não foi possível concluir a operação."),
            validationErrors: body.validationErrors || {}
        };
    };

    const clearAuth = () => storageKeys.forEach((key) => sessionStorage.removeItem(key));

    const request = async (url, options = {}, authenticated = true) => {
        const headers = new Headers(options.headers || {});
        if (options.body && !headers.has("Content-Type")) {
            headers.set("Content-Type", "application/json");
        }
        if (authenticated) {
            const token = sessionStorage.getItem("accessToken");
            if (token) headers.set("Authorization", `Bearer ${token}`);
        }
        let response;
        try {
            response = await fetch(url, { ...options, headers });
        } catch (_) {
            throw { status: 0, message: "Sem conexão com o servidor. Verifique sua internet e tente novamente.", validationErrors: {} };
        }
        if (response.status === 401 && authenticated) {
            clearAuth();
            window.location.assign("/login");
            throw { status: 401, message: "Sua sessão expirou. Entre novamente.", validationErrors: {} };
        }
        if (!response.ok) throw await parseProblem(response);
        if (response.status === 204) return null;
        const contentType = response.headers.get("content-type") || "";
        return contentType.includes("json") ? response.json() : response.text();
    };

    const toast = (message, type = "success") => {
        let region = document.querySelector(".toast-region");
        if (!region) {
            region = document.createElement("div");
            region.className = "toast-region";
            region.setAttribute("aria-live", "polite");
            document.body.append(region);
        }
        const item = document.createElement("div");
        item.className = `toast toast--${type}`;
        item.textContent = message;
        region.append(item);
        window.setTimeout(() => item.remove(), 4500);
    };

    const clearFieldErrors = (form) => {
        form.querySelectorAll(".field-error").forEach((item) => { item.textContent = ""; });
        form.querySelectorAll("[aria-invalid]").forEach((item) => item.removeAttribute("aria-invalid"));
    };

    const showFieldErrors = (form, errors = {}) => {
        clearFieldErrors(form);
        Object.entries(errors).forEach(([name, message]) => {
            const simpleName = name.split(".").pop();
            const input = form.elements.namedItem(simpleName);
            const error = form.querySelector(`[data-error-for="${CSS.escape(simpleName)}"]`);
            if (input) input.setAttribute("aria-invalid", "true");
            if (error) error.textContent = message;
        });
    };

    const setLoading = (button, loading, text = "Aguarde…") => {
        if (!button) return;
        if (loading) {
            button.dataset.originalText = button.textContent;
            button.textContent = text;
            button.disabled = true;
            button.classList.add("is-loading");
        } else {
            button.textContent = button.dataset.originalText || button.textContent;
            button.disabled = false;
            button.classList.remove("is-loading");
        }
    };

    const formatDateTime = (value) => value
        ? new Intl.DateTimeFormat("pt-BR", { dateStyle: "long", timeStyle: "short" }).format(new Date(value))
        : "—";

    const formatPhone = (value = "") => {
        const digits = value.replace(/\D/g, "").slice(0, 11);
        if (digits.length <= 10) return digits.replace(/^(\d{0,2})(\d{0,4})(\d{0,4}).*/, (_, a, b, c) =>
            [a && `(${a}`, a.length === 2 && ") ", b, c && `-${c}`].filter(Boolean).join(""));
        return digits.replace(/^(\d{2})(\d{5})(\d{0,4}).*/, "($1) $2-$3");
    };

    const openModal = (modal) => {
        modal.hidden = false;
        document.body.classList.add("modal-open");
        modal.querySelector("input, select, button")?.focus();
    };
    const closeModal = (modal) => {
        modal.hidden = true;
        document.body.classList.remove("modal-open");
    };

    const initShell = () => {
        const menu = document.querySelector("[data-menu-toggle]");
        const sidebar = document.querySelector(".sidebar");
        menu?.addEventListener("click", () => {
            const open = sidebar.classList.toggle("is-open");
            menu.setAttribute("aria-expanded", String(open));
        });
        document.querySelector("[data-sidebar-overlay]")?.addEventListener("click", () => sidebar.classList.remove("is-open"));
        document.querySelectorAll("[data-admin-only]").forEach((el) => {
            el.hidden = sessionStorage.getItem("role") === "PROFISSIONAL";
        });
        document.querySelectorAll("[data-user-email]").forEach((el) => {
            el.textContent = sessionStorage.getItem("email") || "Usuário autenticado";
        });
        document.querySelectorAll("[data-user-role]").forEach((el) => {
            el.textContent = sessionStorage.getItem("role") === "PROFISSIONAL" ? "Profissional" : "Administrador";
        });
        document.querySelectorAll("[data-modal-close]").forEach((button) =>
            button.addEventListener("click", () => closeModal(button.closest(".modal"))));
        document.querySelectorAll(".modal").forEach((modal) =>
            modal.addEventListener("click", (event) => { if (event.target === modal) closeModal(modal); }));
        document.addEventListener("keydown", (event) => {
            if (event.key === "Escape") document.querySelectorAll(".modal:not([hidden])").forEach(closeModal);
        });
        document.querySelector("[data-logout]")?.addEventListener("click", () => {
            clearAuth();
        });
    };

    window.Clinic = {
        request, parseProblem, toast, clearAuth, clearFieldErrors, showFieldErrors,
        setLoading, formatDateTime, formatPhone, openModal, closeModal
    };
    document.addEventListener("DOMContentLoaded", initShell);
})();
