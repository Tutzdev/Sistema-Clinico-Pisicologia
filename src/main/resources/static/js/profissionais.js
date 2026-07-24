(() => {
    let items = [], selectedId = null;
    document.addEventListener("DOMContentLoaded", async () => {
        if (sessionStorage.getItem("role") === "PROFISSIONAL") { window.location.replace("/admin"); return; }
        const body = document.querySelector("#professionals-body"), form = document.querySelector("#professional-form");
        const modal = document.querySelector("#professional-modal"), deleteModal = document.querySelector("#professional-delete-modal");
        const render = () => {
            const term = document.querySelector("#professional-search").value.toLowerCase();
            const filtered = items.filter((p) => `${p.nome} ${p.email} ${p.codigoAgenda}`.toLowerCase().includes(term));
            body.replaceChildren(...filtered.map((p) => {
                const row = document.createElement("tr");
                [p.nome, p.email, p.codigoAgenda].forEach((value) => { const td = document.createElement("td"); td.textContent = value; row.append(td); });
                const actions = document.createElement("td"); actions.className = "table-actions";
                [["Copiar link", "copy"], ["Editar", "edit"], ["Excluir", "delete"]].forEach(([label, action]) => { const b = document.createElement("button"); b.className = `button button--small ${action === "delete" ? "button--ghost-danger" : "button--secondary"}`; b.textContent = label; b.onclick = () => act(action, p); actions.append(b); });
                row.append(actions); return row;
            }));
            document.querySelector("#professionals-empty").hidden = filtered.length > 0;
        };
        const load = async () => {
            try { items = await Clinic.request("/api/profissionais"); render(); }
            catch (e) { Clinic.toast(e.message, "error"); }
            finally { document.querySelector("#professionals-loading").hidden = true; }
        };
        const act = async (action, p) => {
            if (action === "copy") {
                try { await navigator.clipboard.writeText(`${location.origin}/agendar/${p.codigoAgenda}`); Clinic.toast("Link copiado."); }
                catch (_) { Clinic.toast("Não foi possível copiar o link.", "error"); }
            } else if (action === "edit") {
                selectedId = p.id; form.id.value = p.id; form.nome.value = p.nome; form.email.value = p.email; form.codigoAgenda.value = p.codigoAgenda; document.querySelector("#professional-title").textContent = "Editar profissional"; Clinic.openModal(modal);
            } else { selectedId = p.id; deleteModal.querySelector("[data-delete-name]").textContent = p.nome; Clinic.openModal(deleteModal); }
        };
        document.querySelector("[data-new]").onclick = () => { selectedId = null; form.reset(); Clinic.clearFieldErrors(form); document.querySelector("#professional-title").textContent = "Novo profissional"; Clinic.openModal(modal); };
        document.querySelector("#professional-search").oninput = render;
        form.onsubmit = async (event) => {
            event.preventDefault(); const button = form.querySelector("[type=submit]"); Clinic.setLoading(button, true, "Salvando…");
            try { await Clinic.request(selectedId ? `/api/profissionais/${selectedId}` : "/api/profissionais", { method: selectedId ? "PUT" : "POST", body: JSON.stringify({ nome: form.nome.value.trim(), email: form.email.value.trim(), codigoAgenda: form.codigoAgenda.value.trim() }) }); Clinic.closeModal(modal); await load(); Clinic.toast("Profissional salvo."); }
            catch (e) { Clinic.showFieldErrors(form, e.validationErrors); Clinic.toast(e.message, "error"); } finally { Clinic.setLoading(button, false); }
        };
        deleteModal.querySelector("[data-confirm-delete]").onclick = async (event) => { Clinic.setLoading(event.currentTarget, true, "Excluindo…"); try { await Clinic.request(`/api/profissionais/${selectedId}`, { method: "DELETE" }); Clinic.closeModal(deleteModal); await load(); Clinic.toast("Profissional excluído."); } catch (e) { Clinic.toast(e.message, "error"); } finally { Clinic.setLoading(event.currentTarget, false); } };
        await load();
    });
})();
