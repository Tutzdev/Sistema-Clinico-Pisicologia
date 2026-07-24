(() => {
    let selectedId = null;
    document.addEventListener("DOMContentLoaded", () => {
        const body = document.querySelector("#patients-body");
        const form = document.querySelector("#patient-form");
        const modal = document.querySelector("#patient-modal");
        const deleteModal = document.querySelector("#patient-delete-modal");
        const search = document.querySelector("#patient-search");

        const normalizeRows = () => body.querySelectorAll("tr").forEach((row) => {
            row.children[1].textContent = Clinic.formatPhone(row.dataset.phone);
        });
        const bindRows = () => {
            normalizeRows();
            body.querySelectorAll("[data-edit]").forEach((button) => button.onclick = () => {
                const row = button.closest("tr");
                selectedId = row.dataset.id;
                form.id.value = selectedId;
                form.nome.value = row.dataset.name;
                form.telefone.value = Clinic.formatPhone(row.dataset.phone);
                form.email.value = row.dataset.email || "";
                document.querySelector("#patient-modal-title").textContent = "Editar paciente";
                Clinic.openModal(modal);
            });
            body.querySelectorAll("[data-delete]").forEach((button) => button.onclick = () => {
                const row = button.closest("tr");
                selectedId = row.dataset.id;
                deleteModal.querySelector("[data-delete-name]").textContent = row.dataset.name;
                Clinic.openModal(deleteModal);
            });
        };
        const refresh = async () => {
            const patients = await Clinic.request("/api/pacientes");
            body.replaceChildren(...patients.map((patient) => {
                const row = document.createElement("tr");
                Object.assign(row.dataset, { id: patient.id, name: patient.nome, phone: patient.telefone, email: patient.email || "" });
                [patient.nome, Clinic.formatPhone(patient.telefone), patient.email || "Não informado"].forEach((text) => {
                    const cell = document.createElement("td"); cell.textContent = text; row.append(cell);
                });
                const actions = document.createElement("td"); actions.className = "table-actions";
                const edit = document.createElement("button"); edit.className = "button button--secondary button--small"; edit.dataset.edit = ""; edit.textContent = "Editar";
                const remove = document.createElement("button"); remove.className = "button button--ghost-danger button--small"; remove.dataset.delete = ""; remove.textContent = "Excluir";
                actions.append(edit, remove); row.append(actions); return row;
            }));
            bindRows(); search.dispatchEvent(new Event("input"));
        };
        document.querySelector("[data-new-patient]").onclick = () => {
            selectedId = null; form.reset(); form.id.value = ""; Clinic.clearFieldErrors(form);
            document.querySelector("#patient-modal-title").textContent = "Novo paciente"; Clinic.openModal(modal);
        };
        form.telefone.addEventListener("input", () => { form.telefone.value = Clinic.formatPhone(form.telefone.value); });
        form.addEventListener("submit", async (event) => {
            event.preventDefault(); const button = form.querySelector("[type=submit]"); Clinic.setLoading(button, true, "Salvando…");
            try {
                await Clinic.request(selectedId ? `/api/pacientes/${selectedId}` : "/api/pacientes", {
                    method: selectedId ? "PUT" : "POST",
                    body: JSON.stringify({ nome: form.nome.value.trim(), telefone: form.telefone.value.replace(/\D/g, ""), email: form.email.value.trim() || null })
                });
                Clinic.closeModal(modal); await refresh(); Clinic.toast("Paciente salvo com sucesso.");
            } catch (error) { Clinic.showFieldErrors(form, error.validationErrors); Clinic.toast(error.message, "error"); }
            finally { Clinic.setLoading(button, false); }
        });
        deleteModal.querySelector("[data-confirm-delete]").onclick = async (event) => {
            Clinic.setLoading(event.currentTarget, true, "Excluindo…");
            try { await Clinic.request(`/api/pacientes/${selectedId}`, { method: "DELETE" }); Clinic.closeModal(deleteModal); await refresh(); Clinic.toast("Paciente excluído."); }
            catch (error) { Clinic.toast(error.message, "error"); }
            finally { Clinic.setLoading(event.currentTarget, false); }
        };
        search.addEventListener("input", () => {
            const term = search.value.trim().toLocaleLowerCase("pt-BR"); let count = 0;
            body.querySelectorAll("tr").forEach((row) => { const show = Object.values(row.dataset).join(" ").toLocaleLowerCase("pt-BR").includes(term); row.hidden = !show; if (show) count++; });
            document.querySelector("#patients-empty").hidden = count > 0;
            document.querySelector("[data-result-count]").textContent = `${count} resultado(s)`;
        });
        bindRows(); search.dispatchEvent(new Event("input"));
    });
})();
