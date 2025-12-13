(function () {
    const avatarImgs = document.querySelectorAll('.avatar-option');
    const saveBtn = document.getElementById('saveAvatarBtn');
    const form = document.getElementById('avatarForm');
    const avatarValue = document.getElementById('avatarValue');

    // select vizual + radio hidden
    avatarImgs.forEach(img => {
        img.addEventListener('click', function () {
            avatarImgs.forEach(i => i.classList.remove('selected'));
            this.classList.add('selected');
            this.previousElementSibling.checked = true; /* WHY: radio-ul din label trebuie să aibă value */
        });
    });

    // la Save -> persistă în DB; redirectul aduce avatarul nou din model (${user.avatar})
    saveBtn?.addEventListener('click', function () {
        const selected = document.querySelector('input[name="avatar"]:checked');
        if (!selected) { alert('Please select an avatar!'); return; }

        avatarValue.value = selected.value;  /* WHY: controllerul primește parametrul "avatar" */
        form.submit();                       /* WHY: @PostMapping("/profile/avatar") salvează + redirect */
    });
})();

document.addEventListener('DOMContentLoaded', function () {
    const img = document.querySelector('.right-user img.js-avatar-trigger');
    if (!img) return;
    img.addEventListener('click', function (ev) {
        // Nu deschide dropdown-ul, ci avatar modalul
        ev.preventDefault();
        ev.stopPropagation();
        const modalEl = document.getElementById('avatarModal');
        if (!modalEl) return;
        const modal = new bootstrap.Modal(modalEl);
        modal.show();
    });
});