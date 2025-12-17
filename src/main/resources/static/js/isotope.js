console.log("theme.js loaded 🚀");

document.addEventListener("DOMContentLoaded", () => {
    const container = document.querySelector(".destination-grid");
    if (!container) return;

    const iso = new Isotope(container, {
        itemSelector: '.destination-item',
        layoutMode: 'fitRows',
        transitionDuration: '0.35s'
    });



    document.querySelectorAll(".destination-filters li").forEach(filter => {
        filter.addEventListener("click", (e) => {
            e.preventDefault();

            document.querySelector(".destination-filters .filter-active")
                ?.classList.remove("filter-active");

            filter.classList.add("filter-active");

            const value = filter.getAttribute("data-filter");
            iso.arrange({ filter: value });
        });
    });
});
