document.addEventListener("DOMContentLoaded", () => {
    const dateTime = document.querySelector("#dataHora");
    if (dateTime) {
        const now = new Date(Date.now() - new Date().getTimezoneOffset() * 60000);
        dateTime.min = now.toISOString().slice(0, 16);
    }
    const professional = document.querySelector("#filter-professional");
    const date = document.querySelector("#filter-date");
    const rows = [...document.querySelectorAll("tbody tr")];
    const filter = () => {
        let visible = 0;
        rows.forEach((row) => {
            const show = (!professional.value || row.dataset.professional === professional.value)
                && (!date.value || row.dataset.date === date.value);
            row.hidden = !show;
            if (show) visible += 1;
        });
        const empty = document.querySelector("#filtered-empty");
        if (empty) empty.hidden = visible > 0 || rows.length === 0;
    };
    professional?.addEventListener("change", filter);
    date?.addEventListener("change", filter);
    const modal = document.querySelector("#delete-modal");
    document.querySelectorAll("[data-delete-id]").forEach((button) => button.addEventListener("click", () => {
        modal.querySelector("[data-delete-summary]").textContent = button.dataset.deleteLabel;
        modal.querySelector("[data-delete-form]").action = `/admin/agenda/${button.dataset.deleteId}/excluir`;
        Clinic.openModal(modal);
    }));
});
