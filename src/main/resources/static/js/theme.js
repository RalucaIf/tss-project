const toggleButton = document.getElementById('theme-toggle');
const body = document.body;

// Setăm tema inițială, de exemplu light
if (!localStorage.getItem('theme')) {
    localStorage.setItem('theme', 'light-background');
}
body.classList.add(localStorage.getItem('theme'));

toggleButton.addEventListener('click', () => {
    if (body.classList.contains('light-background')) {
        body.classList.replace('light-background', 'dark-background');
        localStorage.setItem('theme', 'dark-background');
    } else {
        body.classList.replace('dark-background', 'light-background');
        localStorage.setItem('theme', 'light-background');
    }
});

document.querySelectorAll('.dropdown > a').forEach(el => {
    el.addEventListener('click', function(e) {
        e.preventDefault();
        const subMenu = this.nextElementSibling;
        if (subMenu) {
            subMenu.style.display = subMenu.style.display === 'block' ? 'none' : 'block';
        }
    });
});