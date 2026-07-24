document.addEventListener("DOMContentLoaded", () => {
    let booking;
    try { booking = JSON.parse(sessionStorage.getItem("lastBooking")); } catch (_) { booking = null; }
    const content = document.querySelector("#confirmation-content"), empty = document.querySelector("#confirmation-empty");
    if (!booking?.id) { empty.hidden = false; return; }
    content.hidden = false;
    document.querySelector("[data-confirm-patient]").textContent = booking.pacienteNome;
    document.querySelector("[data-confirm-professional]").textContent = booking.profissionalNome;
    document.querySelector("[data-confirm-date]").textContent = new Intl.DateTimeFormat("pt-BR", { dateStyle: "long", timeStyle: "short" }).format(new Date(booking.dataHora));
    document.querySelector("[data-confirm-status]").textContent = booking.status === "PENDENTE" ? "Pendente" : booking.status;
    document.querySelector("[data-new-booking]").href = `/agendar/${encodeURIComponent(booking.codigoAgenda)}`;
});
