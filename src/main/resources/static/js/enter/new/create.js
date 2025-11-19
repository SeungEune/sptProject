function toggleTypeSection() {
    const type = document.querySelector('input[name="type"]:checked')?.value;
    const guest = document.getElementById('guest-section');
    const emp = document.getElementById('emp-section');

    if (type === 'GUEST') {
        guest.classList.remove('hidden');
        emp.classList.add('hidden');
    } else {
        guest.classList.add('hidden');
        emp.classList.remove('hidden');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('input[name="type"]').forEach(radio => {
        radio.addEventListener('change', toggleTypeSection);
    });
    toggleTypeSection(); // 처음 로딩 시에도 맞춰주기
});