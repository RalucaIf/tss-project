console.log("theme.js loaded 🚀");

// Funcție generică pentru Isotope + filtre
function initIsotope(containerSelector, itemSelector, filterSelector) {
    const container = document.querySelector(containerSelector);
    if (!container) return;

    const iso = new Isotope(container, {
        itemSelector: itemSelector,
        layoutMode: 'fitRows',
        transitionDuration: '0.6s'
    });

    document.querySelectorAll(filterSelector).forEach(filter => {
        filter.addEventListener('click', (e) => {
            e.preventDefault(); // previne deschiderea vreunei pagini

            document.querySelector(filterSelector + '.filter-active')?.classList.remove('filter-active');
            filter.classList.add('filter-active');

            const value = filter.getAttribute('data-filter');
            iso.arrange({ filter: value });
        });
    });
}

// Initializează pentru tururi
initIsotope('.isotope-container', '.isotope-item', '.filter-pills button');

// Initializează pentru destinații
initIsotope('.destination-container', '.destination-item', '.destination-filters button');
