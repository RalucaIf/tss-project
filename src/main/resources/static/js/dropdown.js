(function () {
    const item   = document.querySelector('#header .account-item');
    const toggle = item?.querySelector('.account-toggle');

    // Deschide/închide meniul la click pe "Cont"
    toggle?.addEventListener('click', function (e) {
        e.preventDefault();
        e.stopPropagation();
        item.classList.toggle('open');
    });

    // Închide când dai click în afara meniului
    document.addEventListener('click', function (e) {
        if (!item) return;
        if (!item.contains(e.target)) item.classList.remove('open');
    });

    // Închide la ESC
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') item?.classList.remove('open');
    });
})();