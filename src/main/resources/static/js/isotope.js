


console.log("theme.js loaded 🚀");
const iso = new Isotope('.isotope-container', {
    itemSelector: '.isotope-item',
    layoutMode: 'fitRows'
});

document.querySelectorAll('.isotope-filters li').forEach(filter => {
    filter.addEventListener('click', () => {
        document.querySelector('.filter-active')?.classList.remove('filter-active');
        filter.classList.add('filter-active');

        const value = filter.getAttribute('data-filter');
        iso.arrange({ filter: value });
    });
});


