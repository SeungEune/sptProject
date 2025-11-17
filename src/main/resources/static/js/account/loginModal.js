(() => {
    const openBtn   = document.getElementById('go-reset-pw');
    const modal     = document.getElementById('resetPwModal');
    const dialog    = modal.querySelector('.modal__dialog');
    const closeEls  = modal.querySelectorAll('[data-close="true"]');
    const form      = document.getElementById('resetPwForm');
    const submitBtn = document.getElementById('resetPwSubmit');
    const idInput   = document.getElementById('reqUserId');
    const emailInput= document.getElementById('reqEmail');
    const help      = document.getElementById('resetPwHelp');

    function openModal() {
    modal.classList.add('is-open');
    modal.setAttribute('aria-hidden', 'false');
    setTimeout(() => idInput.focus(), 0);
    document.addEventListener('keydown', onKeydown);
}
    function closeModal() {
    modal.classList.remove('is-open');
    modal.setAttribute('aria-hidden', 'true');
    document.removeEventListener('keydown', onKeydown);
    help.textContent = '';
    form.reset();
}
    function onKeydown(e) {
    if (e.key === 'Escape') closeModal();
}

    // 열기
    openBtn?.addEventListener('click', openModal);

    // 닫기(배경/×/취소)
    closeEls.forEach(el => el.addEventListener('click', closeModal));
    modal.addEventListener('click', (e) => {
    // backdrop 클릭만 닫기
    if (e.target.classList.contains('modal__backdrop')) closeModal();
});

    // 간단 클라이언트 검증
    form.addEventListener('submit', (e) => {
    help.textContent = '';
    const id = idInput.value.trim();
    const em = emailInput.value.trim();

    if (!id || !em) {
    e.preventDefault();
    help.textContent = '아이디와 이메일을 모두 입력하세요.';
    return;
}
    // 아주 단순한 이메일 포맷 체크(서버 검증은 필수)
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(em)) {
    e.preventDefault();
    help.textContent = '이메일 형식을 확인해주세요.';
    emailInput.focus();
    return;
}

    // 중복 클릭 방지
    submitBtn.disabled = true;

    location.href = "/system/user/password/reset.do";

});
})();

