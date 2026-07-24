(() => {
    let step = 1;
    let professional = null;
    let slot = null;

    document.addEventListener("DOMContentLoaded", () => {
        const form = document.querySelector("#home-booking-form");
        const sections = document.querySelectorAll("[data-step]");

        const showStep = (nextStep) => {
            step = nextStep;
            sections.forEach((section) => {
                section.hidden = Number(section.dataset.step) !== step;
            });
            document.querySelectorAll("[data-step-indicator]").forEach((item) => {
                item.classList.toggle(
                    "active",
                    Number(item.dataset.stepIndicator) <= step
                );
            });
            document.querySelector("#agendamento").scrollIntoView({
                behavior: "smooth",
                block: "start"
            });
        };

        const selectSlot = (button, value) => {
            document.querySelectorAll("[data-slots] .slot").forEach((item) => {
                item.classList.remove("selected");
                item.setAttribute("aria-pressed", "false");
            });
            button.classList.add("selected");
            button.setAttribute("aria-pressed", "true");
            slot = value;
            document.querySelector("[data-step='2'] [data-next]").disabled = false;
        };

        const renderSlots = (slots) => {
            const container = document.querySelector("[data-slots]");
            container.replaceChildren(...slots.map((item) => {
                const button = document.createElement("button");
                button.type = "button";
                button.className = "slot";
                button.textContent = Clinic.formatDateTime(item.dataHora);
                button.setAttribute("aria-pressed", "false");
                button.addEventListener("click", () => selectSlot(button, {
                    id: item.id,
                    date: item.dataHora
                }));
                return button;
            }));
            document.querySelector("[data-slots-empty]").hidden = slots.length > 0;
        };

        const loadSlots = async () => {
            const loading = document.querySelector("[data-slots-loading]");
            loading.hidden = false;
            document.querySelector("[data-slots]").replaceChildren();
            document.querySelector("[data-slots-empty]").hidden = true;
            try {
                const slots = await Clinic.request(
                    `/api/public/profissionais/${professional.id}/horarios`,
                    {},
                    false
                );
                renderSlots(slots);
            } catch (error) {
                Clinic.toast(error.message, "error");
                document.querySelector("[data-slots-empty]").hidden = false;
            } finally {
                loading.hidden = true;
            }
        };

        document.querySelectorAll("[data-professional-id]").forEach((button) => {
            button.addEventListener("click", () => {
                document.querySelectorAll("[data-professional-id]").forEach((item) => {
                    item.classList.remove("selected");
                    item.setAttribute("aria-pressed", "false");
                });
                button.classList.add("selected");
                button.setAttribute("aria-pressed", "true");
                professional = {
                    id: Number(button.dataset.professionalId),
                    name: button.dataset.professionalName,
                    code: button.dataset.professionalCode
                };
                document.querySelector("[data-step='1'] [data-next]").disabled = false;
            });
        });

        document.querySelector("[data-step='1'] [data-next]")?.addEventListener("click", async () => {
            slot = null;
            document.querySelector("[data-step='2'] [data-next]").disabled = true;
            document.querySelector("[data-selected-professional]").textContent = professional.name;
            showStep(2);
            await loadSlots();
        });
        document.querySelector("[data-step='2'] [data-next]").addEventListener("click", () => showStep(3));
        document.querySelectorAll("[data-back]").forEach((button) => {
            button.addEventListener("click", () => showStep(step - 1));
        });

        form.telefone.addEventListener("input", () => {
            form.telefone.value = Clinic.formatPhone(form.telefone.value);
        });
        form.addEventListener("submit", (event) => {
            event.preventDefault();
            Clinic.clearFieldErrors(form);
            if (!form.checkValidity()) {
                form.reportValidity();
                return;
            }
            document.querySelector("[data-review-professional]").textContent = professional.name;
            document.querySelector("[data-review-date]").textContent = Clinic.formatDateTime(slot.date);
            document.querySelector("[data-review-name]").textContent = form.nome.value.trim();
            document.querySelector("[data-review-phone]").textContent = form.telefone.value;
            document.querySelector("[data-review-email]").textContent = form.email.value.trim() || "Não informado";
            showStep(4);
        });

        document.querySelector("[data-confirm-booking]").addEventListener("click", async (event) => {
            const button = event.currentTarget;
            const errorBox = document.querySelector("[data-booking-error]");
            errorBox.textContent = "";
            Clinic.setLoading(button, true, "Enviando…");
            try {
                const patient = await Clinic.request("/api/pacientes", {
                    method: "POST",
                    body: JSON.stringify({
                        nome: form.nome.value.trim(),
                        telefone: form.telefone.value.replace(/\D/g, ""),
                        email: form.email.value.trim() || null
                    })
                }, false);
                const booking = await Clinic.request("/api/agendamentos", {
                    method: "POST",
                    body: JSON.stringify({
                        pacienteId: patient.id,
                        horarioId: slot.id
                    })
                }, false);
                sessionStorage.setItem("lastBooking", JSON.stringify({
                    ...booking,
                    codigoAgenda: professional.code
                }));
                window.location.assign("/agendar/confirmacao");
            } catch (error) {
                errorBox.textContent = error.message;
                if (Object.keys(error.validationErrors || {}).length) {
                    Clinic.showFieldErrors(form, error.validationErrors);
                    showStep(3);
                }
            } finally {
                Clinic.setLoading(button, false);
            }
        });
    });
})();
