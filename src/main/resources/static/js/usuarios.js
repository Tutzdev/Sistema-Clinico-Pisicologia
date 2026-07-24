(() => {
    let users = [], professionals = [], selected = null;
    document.addEventListener("DOMContentLoaded", async () => {
        if (sessionStorage.getItem("role") === "PROFISSIONAL") { window.location.replace("/admin"); return; }
        const body = document.querySelector("#users-body"), userModal = document.querySelector("#user-modal");
        const userForm = document.querySelector("#user-form"), passwordModal = document.querySelector("#password-modal");
        const statusModal = document.querySelector("#status-modal");
        const badge = (text, className) => { const span = document.createElement("span"); span.className = `badge ${className}`; span.textContent = text; return span; };
        const render = () => {
            body.replaceChildren(...users.map((user) => {
                const row = document.createElement("tr"), email = document.createElement("td"); email.textContent = user.email; row.append(email);
                const role = document.createElement("td"); role.append(badge(user.role === "ADMIN" ? "Administrador" : "Profissional", "badge--neutral")); row.append(role);
                const professional = document.createElement("td"); professional.textContent = user.profissionalNome || "—"; row.append(professional);
                const status = document.createElement("td"); status.append(badge(user.ativo ? "Ativo" : "Inativo", user.ativo ? "badge--success" : "badge--danger")); row.append(status);
                const actions = document.createElement("td"); actions.className = "table-actions";
                const password = document.createElement("button"); password.className = "button button--secondary button--small"; password.textContent = "Alterar senha"; password.onclick = () => { selected = user; document.querySelector("#password-form").reset(); Clinic.openModal(passwordModal); };
                const toggle = document.createElement("button"); toggle.className = `button button--small ${user.ativo ? "button--ghost-danger" : "button--secondary"}`; toggle.textContent = user.ativo ? "Desativar" : "Ativar"; toggle.onclick = () => { selected = user; statusModal.querySelector("[data-status-message]").textContent = `${user.ativo ? "Desativar" : "Ativar"} o acesso de ${user.email}?`; Clinic.openModal(statusModal); };
                actions.append(password, toggle); row.append(actions); return row;
            }));
            document.querySelector("#users-empty").hidden = users.length > 0;
        };
        const load = async () => {
            try { [users, professionals] = await Promise.all([Clinic.request("/api/usuarios"), Clinic.request("/api/profissionais")]); render();
                const select = userForm.profissionalId; select.querySelectorAll("option:not(:first-child)").forEach((o) => o.remove()); professionals.forEach((p) => select.add(new Option(p.nome, p.id)));
            } catch (e) { Clinic.toast(e.message, "error"); } finally { document.querySelector("#users-loading").hidden = true; }
        };
        const syncRole = () => { const isProfessional = userForm.role.value === "PROFISSIONAL"; document.querySelector("[data-professional-field]").hidden = !isProfessional; userForm.profissionalId.required = isProfessional; if (!isProfessional) userForm.profissionalId.value = ""; };
        userForm.role.onchange = syncRole;
        document.querySelector("[data-new-user]").onclick = () => { userForm.reset(); syncRole(); Clinic.clearFieldErrors(userForm); Clinic.openModal(userModal); };
        userForm.onsubmit = async (event) => { event.preventDefault(); const button = userForm.querySelector("[type=submit]"); Clinic.setLoading(button, true, "Criando…"); try { await Clinic.request("/api/usuarios", { method: "POST", body: JSON.stringify({ email: userForm.email.value.trim(), senha: userForm.senha.value, role: userForm.role.value, profissionalId: userForm.profissionalId.value ? Number(userForm.profissionalId.value) : null }) }); Clinic.closeModal(userModal); await load(); Clinic.toast("Usuário criado."); } catch (e) { Clinic.showFieldErrors(userForm, e.validationErrors); Clinic.toast(e.message, "error"); } finally { Clinic.setLoading(button, false); } };
        document.querySelector("#password-form").onsubmit = async (event) => { event.preventDefault(); const form = event.currentTarget, button = form.querySelector("[type=submit]"); Clinic.setLoading(button, true, "Alterando…"); try { await Clinic.request(`/api/usuarios/${selected.id}/senha`, { method: "PATCH", body: JSON.stringify({ novaSenha: form.novaSenha.value }) }); Clinic.closeModal(passwordModal); form.reset(); Clinic.toast("Senha alterada."); } catch (e) { Clinic.showFieldErrors(form, e.validationErrors); Clinic.toast(e.message, "error"); } finally { Clinic.setLoading(button, false); } };
        statusModal.querySelector("[data-confirm-status]").onclick = async (event) => { const button = event.currentTarget; Clinic.setLoading(button, true, "Confirmando…"); try { await Clinic.request(`/api/usuarios/${selected.id}/${selected.ativo ? "desativacao" : "ativacao"}`, { method: "PATCH" }); Clinic.closeModal(statusModal); await load(); Clinic.toast(`Usuário ${selected.ativo ? "desativado" : "ativado"}.`); } catch (e) { Clinic.toast(e.message, "error"); } finally { Clinic.setLoading(button, false); } };
        await load();
    });
})();
