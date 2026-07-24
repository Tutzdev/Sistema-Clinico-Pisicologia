document.addEventListener("DOMContentLoaded", () => {
    const hour = new Date().getHours();
    const greeting = hour < 12 ? "Bom dia" : hour < 18 ? "Boa tarde" : "Boa noite";
    const target = document.querySelector("[data-greeting]");
    if (target) target.textContent = greeting;
});
