(() => {
    let step = 1, slot = null;
    document.addEventListener("DOMContentLoaded", () => {
        const root = document.querySelector(".booking-container");
        const form = document.querySelector("#booking-patient-form");
        if (!form) return;
        const showStep = (next) => {
            step = next;
            document.querySelectorAll("[data-step]").forEach((section) => { section.hidden = Number(section.dataset.step) !== step; });
            document.querySelectorAll("[data-step-indicator]").forEach((item) => item.classList.toggle("active", Number(item.dataset.stepIndicator) <= step));
            window.scrollTo({ top: 0, behavior: "smooth" });
        };
        document.querySelectorAll(".slot").forEach((button) => button.onclick = () => {
            document.querySelectorAll(".slot").forEach((item) => { item.classList.remove("selected"); item.setAttribute("aria-pressed", "false"); });
            button.classList.add("selected"); button.setAttribute("aria-pressed", "true");
            slot = { id: Number(button.dataset.slotId), date: button.dataset.slotDate };
            document.querySelector("[data-step='1'] [data-next]").disabled = false;
        });
        document.querySelector("[data-step='1'] [data-next]")?.addEventListener("click", () => showStep(2));
        document.querySelectorAll("[data-back]").forEach((button) => button.onclick = () => showStep(step - 1));
        form.telefone.addEventListener("input", () => { form.telefone.value = Clinic.formatPhone(form.telefone.value); });
        form.onsubmit = (event) => {
            event.preventDefault(); Clinic.clearFieldErrors(form);
            if (!form.checkValidity()) { form.reportValidity(); return; }
            document.querySelector("[data-review-professional]").textContent = root.dataset.professionalName;
            document.querySelector("[data-review-date]").textContent = Clinic.formatDateTime(slot.date);
            document.querySelector("[data-review-name]").textContent = form.nome.value.trim();
            document.querySelector("[data-review-phone]").textContent = form.telefone.value;
            document.querySelector("[data-review-email]").textContent = form.email.value.trim() || "Não informado";
            showStep(3);
        };
        document.querySelector("[data-confirm-booking]").onclick = async (event) => {
            const button = event.currentTarget, errorBox = document.querySelector("[data-booking-error]");
            errorBox.textContent = ""; Clinic.setLoading(button, true, "Enviando…");
            try {
                const patient = await Clinic.request("/api/pacientes", { method: "POST", body: JSON.stringify({ nome: form.nome.value.trim(), telefone: form.telefone.value.replace(/\D/g, ""), email: form.email.value.trim() || null }) }, false);
                const booking = await Clinic.request("/api/agendamentos", { method: "POST", body: JSON.stringify({ pacienteId: patient.id, horarioId: slot.id }) }, false);
                sessionStorage.setItem("lastBooking", JSON.stringify({ ...booking, codigoAgenda: root.dataset.code }));
                window.location.assign("/agendar/confirmacao");
            } catch (error) {
                errorBox.textContent = error.message;
                if (Object.keys(error.validationErrors || {}).length) { Clinic.showFieldErrors(form, error.validationErrors); showStep(2); }
            } finally { Clinic.setLoading(button, false); }
        };
    });
})();
