/* File: src/main/resources/static/js/isotope.js */

document.addEventListener("DOMContentLoaded", () => {
    const container = document.querySelector(".destination-grid");
    const filters = document.querySelector(".destination-filters");

    if (!container || !filters || typeof Isotope === "undefined") return;

    const wrapper =
        container.closest(".destinations-isotope") ||
        container.closest("[data-default-filter]") ||
        container.parentElement;

    const defaultFilter = (wrapper && wrapper.getAttribute("data-default-filter")) || "*";
    const layoutMode = (wrapper && wrapper.getAttribute("data-layout")) || "fitRows";

    const iso = new Isotope(container, {
        itemSelector: ".destination-item",
        layoutMode,
        transitionDuration: "350ms",

        // ✅ efect de apariție/dispariție la filtrare (UN SINGUR efect)
        hiddenStyle: { opacity: 0, transform: "scale(0.96)" },
        visibleStyle: { opacity: 1, transform: "scale(1)" },

        // (opțional) apar pe rând, discret
        stagger: 20,
    });

    // Inițializare
    iso.arrange({ filter: defaultFilter });

    filters.querySelectorAll("li").forEach((btn) => {
        btn.addEventListener("click", (e) => {
            e.preventDefault();

            filters.querySelector(".filter-active")?.classList.remove("filter-active");
            btn.classList.add("filter-active");

            const value = btn.getAttribute("data-filter") || "*";
            iso.arrange({ filter: value });
        });
    });
});
