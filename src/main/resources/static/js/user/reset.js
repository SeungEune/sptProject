(() => {
    if (window.__RESET_PW_BOUND) return;
    window.__RESET_PW_BOUND = true;

    const resetForm = document.getElementById('pw-form');
    const resetPw   = document.getElementById('new-pw-input');
    const resetPw2  = document.getElementById('new-pw-input2');
    const cancelBtn = document.getElementById('cancel-pw-btn');

    if (resetForm) {
    resetForm.addEventListener('submit', (e) => {
    // 주: 서버로 실제 제출할거면 e.preventDefault() 하지 마세요.
    // 여기선 검증 실패시에만 preventDefault 합니다.
    if (!resetPw || !resetPw2) return;

    if (resetPw.value.length < 8) {
    e.preventDefault();
    alert('8자리 미만은 불가합니다.');
    resetPw.focus();
    return;
}
    if (resetPw.value !== resetPw2.value) {
    e.preventDefault();
    alert('비밀번호와 비밀번호 확인이 일치하지 않습니다.');
    resetPw2.focus();
    return;
}
    // 통과 시: 제출 그대로 진행 (e.preventDefault 없음)
});
}

    cancelBtn?.addEventListener('click', () => {
    location.href = '/login/logout.do';
});
})();
