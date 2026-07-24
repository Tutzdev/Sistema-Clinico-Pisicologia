(() => {
    let appointments = [];
    let slots = [];
    let pendingAction = null;

    const statusLabels = {
        PENDENTE: "Pendente",
        CONFIRMADO: "Confirmado",
        CANCELADO: "Cancelado",
        CONCLUIDO: "Concluído"
    };

    const statusClass = (status) => ({
        PENDENTE: "badge--warning",
        CONFIRMADO: "badge--success",
        CANCELADO: "badge--danger",
        CONCLUIDO: "badge--neutral"
    })[status] || "badge--neutral";

    document.addEventListener("DOMContentLoaded", async () => {
        if (sessionStorage.getItem("role") !== "PROFISSIONAL") {
            window.location.replace("/admin");
            return;
        }

        const appointmentBody = document.querySelector("#professional-appointments");
        const actionModal = document.querySelector("#professional-action-modal");
        const slotForm = document.querySelector("#professional-slot-form");
        const dateFilter = document.querySelector("#appointment-date");
        const statusFilter = document.querySelector("#appointment-status");

        const isSameDay = (value, date = new Date()) => {
            const candidate = new Date(value);
            return candidate.getFullYear() === date.getFullYear()
                && candidate.getMonth() === date.getMonth()
                && candidate.getDate() === date.getDate();
        };

        const updateMetrics = () => {
            const now = new Date();
            document.querySelector("[data-today-count]").textContent =
                appointments.filter((item) => isSameDay(item.dataHora, now) && item.status !== "CANCELADO").length;
            document.querySelector("[data-pending-count]").textContent =
                appointments.filter((item) => item.status === "PENDENTE").length;
            document.querySelector("[data-upcoming-count]").textContent =
                appointments.filter((item) => new Date(item.dataHora) >= now && item.status !== "CANCELADO").length;
        };

        const actionButton = (label, action, item, style = "button--secondary") => {
            const button = document.createElement("button");
            button.className = `button button--small ${style}`;
            button.textContent = label;
            button.addEventListener("click", () => openAction(action, item));
            return button;
        };

        const renderAppointments = () => {
            const selectedDate = dateFilter.value;
            const selectedStatus = statusFilter.value;
            const filtered = appointments.filter((item) =>
                (!selectedDate || item.dataHora.slice(0, 10) === selectedDate)
                && (!selectedStatus || item.status === selectedStatus)
            );

            appointmentBody.replaceChildren(...filtered.map((item) => {
                const row = document.createElement("tr");
                const date = document.createElement("td");
                date.textContent = Clinic.formatDateTime(item.dataHora);
                const patient = document.createElement("td");
                patient.textContent = item.pacienteNome;
                const contact = document.createElement("td");
                contact.textContent = [Clinic.formatPhone(item.pacienteTelefone), item.pacienteEmail]
                    .filter(Boolean).join(" · ") || "Não informado";
                const status = document.createElement("td");
                const badge = document.createElement("span");
                badge.className = `badge ${statusClass(item.status)}`;
                badge.textContent = statusLabels[item.status] || item.status;
                status.append(badge);
                const actions = document.createElement("td");
                actions.className = "table-actions";
                if (item.status === "PENDENTE") {
                    actions.append(
                        actionButton("Confirmar", "confirmacao", item, "button--primary"),
                        actionButton("Cancelar", "cancelamento", item, "button--ghost-danger")
                    );
                } else if (item.status === "CONFIRMADO") {
                    actions.append(
                        actionButton("Concluir", "conclusao", item, "button--primary"),
                        actionButton("Cancelar", "cancelamento", item, "button--ghost-danger")
                    );
                }
                row.append(date, patient, contact, status, actions);
                return row;
            }));
            document.querySelector("[data-appointments-empty]").hidden = filtered.length > 0;
            updateMetrics();
        };

        const renderSlots = () => {
            const container = document.querySelector("[data-professional-slots]");
            container.replaceChildren(...slots.map((slot) => {
                const item = document.createElement("div");
                item.className = "slot-list-item";
                const text = document.createElement("span");
                text.textContent = Clinic.formatDateTime(slot.dataHora);
                const remove = document.createElement("button");
                remove.className = "button button--ghost-danger button--small";
                remove.textContent = "Excluir";
                remove.addEventListener("click", () => openAction("excluir-horario", slot));
                item.append(text, remove);
                return item;
            }));
            document.querySelector("[data-slots-empty]").hidden = slots.length > 0;
        };

        const openAction = (action, item) => {
            const descriptions = {
                confirmacao: ["Confirmar agendamento?", `O atendimento de ${item.pacienteNome} será confirmado.`],
                cancelamento: ["Cancelar agendamento?", `O horário de ${item.pacienteNome} será liberado novamente.`],
                conclusao: ["Concluir atendimento?", `O atendimento de ${item.pacienteNome} será marcado como concluído.`],
                "excluir-horario": ["Excluir horário?", `${Clinic.formatDateTime(item.dataHora)} deixará de aparecer para pacientes.`]
            };
            pendingAction = { action, item };
            const [title, message] = descriptions[action];
            actionModal.querySelector("[data-action-title]").textContent = title;
            actionModal.querySelector("[data-action-message]").textContent = message;
            const confirm = actionModal.querySelector("[data-confirm-action]");
            confirm.className = `button ${action.includes("cancel") || action.includes("excluir") ? "button--danger" : "button--primary"}`;
            Clinic.openModal(actionModal);
        };

        const loadAppointments = async () => {
            document.querySelector("[data-appointments-loading]").hidden = false;
            try {
                appointments = await Clinic.request("/api/profissional/me/agendamentos");
                renderAppointments();
            } catch (error) {
                Clinic.toast(error.message, "error");
            } finally {
                document.querySelector("[data-appointments-loading]").hidden = true;
            }
        };

        const loadSlots = async () => {
            document.querySelector("[data-slots-loading]").hidden = false;
            try {
                slots = await Clinic.request("/api/profissional/me/horarios");
                renderSlots();
            } catch (error) {
                Clinic.toast(error.message, "error");
            } finally {
                document.querySelector("[data-slots-loading]").hidden = true;
            }
        };

        dateFilter.addEventListener("change", renderAppointments);
        statusFilter.addEventListener("change", renderAppointments);
        const localNow = new Date(Date.now() - new Date().getTimezoneOffset() * 60000);
        slotForm.dataHora.min = localNow.toISOString().slice(0, 16);
        slotForm.addEventListener("submit", async (event) => {
            event.preventDefault();
            const button = slotForm.querySelector("[type=submit]");
            Clinic.setLoading(button, true, "Adicionando…");
            try {
                await Clinic.request("/api/profissional/me/horarios", {
                    method: "POST",
                    body: JSON.stringify({ dataHora: slotForm.dataHora.value })
                });
                slotForm.reset();
                await loadSlots();
                Clinic.toast("Horário disponibilizado.");
            } catch (error) {
                Clinic.showFieldErrors(slotForm, error.validationErrors);
                Clinic.toast(error.message, "error");
            } finally {
                Clinic.setLoading(button, false);
            }
        });

        actionModal.querySelector("[data-confirm-action]").addEventListener("click", async (event) => {
            const button = event.currentTarget;
            Clinic.setLoading(button, true, "Processando…");
            try {
                if (pendingAction.action === "excluir-horario") {
                    await Clinic.request(
                        `/api/profissional/me/horarios/${pendingAction.item.id}`,
                        { method: "DELETE" }
                    );
                    await loadSlots();
                } else {
                    await Clinic.request(
                        `/api/profissional/me/agendamentos/${pendingAction.item.id}/${pendingAction.action}`,
                        { method: "PATCH" }
                    );
                    await loadAppointments();
                    await loadSlots();
                }
                Clinic.closeModal(actionModal);
                Clinic.toast("Agenda atualizada.");
            } catch (error) {
                Clinic.toast(error.message, "error");
            } finally {
                Clinic.setLoading(button, false);
            }
        });

        await Promise.all([loadAppointments(), loadSlots()]);
    });
})();
