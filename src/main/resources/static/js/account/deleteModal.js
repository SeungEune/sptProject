(() => {
    // 중복 바인딩 가드 (스크립트가 두 번 로드돼도 1회만 바인딩)
    if (window.__DELETE_MODAL_BOUND) return;
    window.__DELETE_MODAL_BOUND = true;

    const openBtn   = document.getElementById('delete-btn');
    const modal     = document.getElementById('deleteModal');
    const form      = document.getElementById('deleteForm');
    const hiddenId  = document.getElementById('deleteAccountId');
    const submitBtn = document.getElementById('deleteSubmitBtn');
    const idInput   = document.getElementById('id-input');
    const msgP      = modal?.querySelector('p');
    const closeEls  = modal?.querySelectorAll('[data-close="true"]');

    function onKeydown(e){ if (e.key === 'Escape') close(); }

    function open() {
    if (!modal) return;
    const uid = (idInput?.value || '').trim();

    // === 옵션 A: 아이디가 없으면 모달도 열지 않음(현재 동작)
    if (!uid) { alert('삭제할 아이디가 없습니다. 먼저 사용자 아이디를 확인하세요.'); return; }

    // === 옵션 B: 아이디가 없어도 모달은 열고, 안내만 표시(원하면 위 A 주석 해제/이 줄 주석)
    // if (!uid && msgP) msgP.textContent = '삭제할 아이디가 없습니다. 아이디를 먼저 선택/확인하세요.';
    // hiddenId.value = uid;  // 비어 있어도 같이 초기화

    modal.classList.add('is-open');
    modal.setAttribute('aria-hidden', 'false');
    document.addEventListener('keydown', onKeydown);
}

    function close() {
    if (!modal) return;
    modal.classList.remove('is-open');
    modal.setAttribute('aria-hidden', 'true');
    document.removeEventListener('keydown', onKeydown);
    if (submitBtn) { submitBtn.disabled = false; submitBtn.textContent = '확인'; }
    form?.reset();
}

    openBtn?.addEventListener('click', open);
    closeEls?.forEach(el => el.addEventListener('click', close));
    modal?.addEventListener('click', (e) => {
    if (e.target.classList?.contains('modal__backdrop')) close();
});

    form?.addEventListener('submit', (e) => {
    const uid = (hiddenId?.value || '').trim();
    if (!uid) {
    e.preventDefault();
    alert('삭제할 아이디가 비어 있습니다.');
    return;
}
    if (submitBtn) {
    submitBtn.disabled = true;
    //submitBtn.textContent = '삭제 중...';
}
});
})();

