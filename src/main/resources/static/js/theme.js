(function() {
    const toggleBtn = document.getElementById('themeToggle');
    if (!toggleBtn) return;
    const icon = toggleBtn.querySelector('i');
    const THEME_KEY = 'theme';

    function apply(theme) {
        const dark = theme === 'dark';
        document.body.classList.toggle('dark-background', dark);
        // iconița reprezintă TEMA CURENTĂ: soare pe light, lună pe dark
        if (icon) icon.className = dark ? 'bi bi-moon' : 'bi bi-sun';
        toggleBtn.setAttribute('aria-pressed', String(dark));
        toggleBtn.title = dark ? 'Dark mode' : 'Light mode';
    }

    // init: din localStorage sau preferința OS
    const stored = localStorage.getItem(THEME_KEY);
    const prefersDark = window.matchMedia?.('(prefers-color-scheme: dark)').matches;
    apply(stored ?? (prefersDark ? 'dark' : 'light'));

    // toggle + persist
    toggleBtn.addEventListener('click', () => {
        const dark = document.body.classList.contains('dark-background');
        const next = dark ? 'light' : 'dark';
        localStorage.setItem(THEME_KEY, next);
        apply(next);
    });
})();