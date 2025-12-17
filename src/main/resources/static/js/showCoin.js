// Funcție pentru popup bani
function showCoinPopup(amount, targetElement) {
    const popup = document.createElement('div');
    popup.classList.add('coin-popup');
    popup.innerHTML = `+${amount} <i class="fa-regular fa-money-bill-1"></i>`;

    // poziționare relativă la mesajul user-ului
    const rect = targetElement.getBoundingClientRect();
    popup.style.position = 'absolute';
    popup.style.left = rect.right + 10 + 'px'; // la dreapta mesajului
    popup.style.top = rect.top + 'px';
    popup.style.zIndex = 9999;

    document.body.appendChild(popup);

    popup.addEventListener('animationend', () => {
        popup.remove();
    });
}
function showPointsAnimation(amount) {
    const anim = document.createElement('div');
    anim.className = 'points-float';
    anim.innerHTML = `
        +${amount} <i class="fa-regular fa-money-bill-1"></i>
    `;

    document.body.appendChild(anim);

    setTimeout(() => {
        anim.remove();
    }, 2000);
}
function updateNavbarPoints(points) {
    const el = document.getElementById('navbar-points');
    if (el) {
        el.innerHTML = `${points} <i class="fa-regular fa-money-bill-1"></i>`;
    }
}

