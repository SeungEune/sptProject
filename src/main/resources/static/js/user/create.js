(() => {
    // ===== 요소 =====
    const form   = document.getElementById('create-user-form');
    const btn    = document.getElementById('create-btn');

    const idEl   = document.getElementById('create-id-input');
    const pwEl   = document.getElementById('create-pw-input');
    const pw2Row = document.getElementById('create-pw2-row');
    const pw2El  = document.getElementById('create-pw-input2');
    const nameEl = document.getElementById('create-name-input');
    const posEl  = document.getElementById('create-position-input');
    const mailEl = document.getElementById('create-email-input');
    const telEl  = document.getElementById('create-pn-input');

    // ===== 생성 페이지는 직접 입력해야 하므로 읽기전용/숨김 해제 =====
    [pwEl, pw2El, nameEl, mailEl, telEl].forEach(el => el?.removeAttribute('readonly'));
    pw2Row?.removeAttribute('hidden');

    // ===== 유틸 =====
    const trim = v => (v ?? '').trim();

    function invalid(msg, el) {
    alert(msg);
    el?.focus();
    throw new Error('invalid'); // 아래 로직 중단용
}

    // 간단한 유효성 규칙들
    const ID_RE    = /^[a-z0-9_]{4,20}$/;          // 소문자/숫자/밑줄 4~20
    const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/; // 기본 이메일 형태
    const TEL_RE   = /^(\d{2,4}-?\d{3,4}-?\d{4})$/; // 010-1234-5678 또는 01012345678

    // 비번 입력 시작하면 확인창 보이기 유도(이미 보이도록 했지만 안전망)
    pwEl?.addEventListener('input', () => {
    if (pwEl.value && pw2Row?.hasAttribute('hidden')) pw2Row.removeAttribute('hidden');
});

    // 엔터 제출 지원
    form?.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
    // textarea가 없고, 버튼과 동일 동작
    e.preventDefault();
    btn?.click();
}
});

    // ===== 등록 버튼 =====
    btn?.addEventListener('click', () => {
    try {
    const userId   = trim(idEl?.value);
    const userPw   = pwEl?.value ?? '';
    const userPw2  = pw2El?.value ?? '';
    const userName = trim(nameEl?.value);
    const userPos  = trim(posEl?.value);
    const userMail = trim(mailEl?.value);
    const userTel  = trim(telEl?.value);

    // 값 되돌려쓰기(좌우 공백 제거된 상태로)
    if (idEl)   idEl.value   = userId;
    if (nameEl) nameEl.value = userName;
    if (posEl)  posEl.value  = userPos;
    if (mailEl) mailEl.value = userMail;
    if (telEl)  telEl.value  = userTel;

    // ==== 검증 ====
    if (!userId)               invalid('아이디를 입력하세요.', idEl);
    if (!ID_RE.test(userId))   invalid('아이디는 소문자/숫자/밑줄 4~20자입니다.', idEl);

    if (!userPw)               invalid('비밀번호를 입력하세요.', pwEl);
    if (userPw.length < 8)     invalid('비밀번호는 8자 이상이어야 합니다.', pwEl);
    if (!userPw2)              invalid('비밀번호 확인을 입력하세요.', pw2El);
    if (userPw !== userPw2)    invalid('비밀번호와 비밀번호 확인이 일치하지 않습니다.', pw2El);

    if (!userName)             invalid('이름을 입력하세요.', nameEl);

    if (userMail && !EMAIL_RE.test(userMail))
    invalid('이메일 형식이 올바르지 않습니다.', mailEl);

    if (userTel && !TEL_RE.test(userTel))
    invalid('전화번호 형식이 올바르지 않습니다. 예) 010-1234-5678', telEl);

    // ==== 통과 → 제출 ====
    btn.disabled = true;
    //btn.textContent = '등록 중...';
    form?.submit();
} catch (_) {
    // invalid()에서 던진 에러는 여기서 무시
}
});
})();

