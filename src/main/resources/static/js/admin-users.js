(() => {
    const dialog = document.getElementById('deleteUserDialog');
    if (!dialog) return;
    const form = document.getElementById('deleteUserForm');
    const cancel = document.getElementById('cancelUserDelete');
    const confirm = document.getElementById('confirmUserDelete');
    const confirmed = document.getElementById('deleteConfirmed');
    let opener;
    let submitting = false;
    document.querySelectorAll('[data-user-delete]').forEach(button => {
        button.addEventListener('click', () => {
            opener = button;
            submitting = false;
            confirmed.value = 'false';
            confirm.disabled = false;
            confirm.textContent = 'Xóa người dùng';
            form.action = button.dataset.deleteUrl;
            document.getElementById('deleteUserName').textContent = button.dataset.userName;
            document.getElementById('deleteUserCode').textContent = button.dataset.userCode;
            dialog.showModal();
            cancel.focus();
        });
    });
    cancel.addEventListener('click', () => dialog.close());
    dialog.addEventListener('close', () => {
        confirmed.value = 'false';
        confirm.disabled = true;
        if (opener) opener.focus();
    });
    form.addEventListener('submit', event => {
        if (!dialog.open || !opener || submitting) { event.preventDefault(); return; }
        confirmed.value = 'true';
        submitting = true;
        confirm.disabled = true;
        confirm.textContent = 'Đang xóa…';
    });
})();
