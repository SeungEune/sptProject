(() => {
    /* =========================
     * 0) 분류=등록일일 때 날짜 구역 토글
     * ========================= */
    const sel  = document.getElementById('opt');
    const box  = document.getElementById('date-range');
    const from = document.getElementById('date-from');
    const to   = document.getElementById('date-to');

    function toggleDateRange() {
    if (!sel || !box || !from || !to) return;
    const isDate = sel.value === 'date';
    box.hidden = !isDate;
    [from, to].forEach(el => {
    el.disabled = !isDate;
    el.required =  isDate;
    if (!isDate) el.value = '';
});
}
    toggleDateRange();
    sel?.addEventListener('change', toggleDateRange);
    document.querySelector('.search-form')?.closest('form')
    ?.addEventListener('reset', () => setTimeout(toggleDateRange, 0));

    /* =========================
     * 1) 공용 모달 바인더
     *    - allowMulti: 지문 여러 개(콤마/공백) 허용
     *    - 전송 시 hidden fingerIds[]=... 형태로 만들어 List<Integer>로 받기 좋게 처리
     * ========================= */
    function bindEnterModal(opts) {
    const {
    openBtnId, modalId, formId,
    userIdInputId, fingerInputId,
    submitBtnId, helpId,
    submitLabel = '등록', allowMulti = true
} = opts;

    const openBtn   = document.getElementById(openBtnId);
    const modal     = document.getElementById(modalId);
    const form      = document.getElementById(formId);
    const submitBtn = document.getElementById(submitBtnId);
    const userIdEl  = document.getElementById(userIdInputId);
    const fingerEl  = document.getElementById(fingerInputId);
    const help      = document.getElementById(helpId);
    const closeEls  = modal?.querySelectorAll('[data-close="true"]');

    function onKeydown(e){ if(e.key === 'Escape') close(); }

    function open(prefill){
    if(!modal) return;
    modal.classList.add('is-open');
    modal.setAttribute('aria-hidden', 'false');
    if (prefill && userIdEl) userIdEl.value = prefill.userId ?? userIdEl.value;
    if (prefill && fingerEl) fingerEl.value = prefill.fingers ?? fingerEl.value;
    setTimeout(() => userIdEl?.focus(), 0);
    document.addEventListener('keydown', onKeydown);
}
    function close(){
    if(!modal) return;
    modal.classList.remove('is-open');
    modal.setAttribute('aria-hidden', 'true');
    document.removeEventListener('keydown', onKeydown);
    help && (help.textContent = '');
    // 버튼/폼 초기화
    if (submitBtn){ submitBtn.disabled = false; submitBtn.textContent = submitLabel; }
    form?.reset();
    // 혹시 만들어둔 hidden fingerIds 정리
    form?.querySelectorAll('input[name="fingerIds"]').forEach(h=>h.remove());
    // 원본 input name 복구
    fingerEl?.setAttribute('name', fingerInputId);
}

    openBtn?.addEventListener('click', () => open());
    closeEls?.forEach(el => el.addEventListener('click', close));
    modal?.addEventListener('click', (e) => {
    if (e.target.classList?.contains('modal__backdrop')) close();
});

    // 제출 검증 & 전처리
    form?.addEventListener('submit', (e) => {
    if (!userIdEl || !fingerEl) return;
    help && (help.textContent = '');

    const uid = userIdEl.value.trim();
    if (!uid) {
    e.preventDefault(); help && (help.textContent = '아이디를 입력하세요.');
    userIdEl.focus(); return;
}

    const raw = fingerEl.value.trim();
    if (!raw) {
    e.preventDefault(); help && (help.textContent = '지문을 입력하세요.');
    fingerEl.focus(); return;
}

    let numbers = [];
    if (allowMulti) {
    // 콤마/공백 구분 → 숫자 배열
    const parts = raw.split(/[\s,]+/).filter(Boolean);
    const seen = new Set();
    for (const p of parts) {
    if (!/^\d+$/.test(p)) { e.preventDefault(); help && (help.textContent = `숫자만 입력하세요: ${p}`); fingerEl.focus(); return; }
    const n = Number(p);
    if (n < 1 || n > 9999) { e.preventDefault(); help && (help.textContent = `지문 번호 범위는 1~9999 입니다: ${n}`); fingerEl.focus(); return; }
    if (seen.has(n)) { e.preventDefault(); help && (help.textContent = `중복 번호가 있습니다: ${n}`); fingerEl.focus(); return; }
    seen.add(n); numbers.push(n);
}
    if (numbers.length > 10) { e.preventDefault(); help && (help.textContent = '지문은 최대 10개까지 등록 가능합니다.'); return; }

    // hidden fingerIds[]=... 로 변환
    form.querySelectorAll('input[name="fingerIds"]').forEach(h=>h.remove());
    numbers.forEach(n=>{
    const h = document.createElement('input');
    h.type = 'hidden'; h.name = 'fingerIds'; h.value = String(n);
    form.appendChild(h);
});
    // 원본 텍스트 필드는 서버에 보내지 않도록 name 제거
    fingerEl.removeAttribute('name');
} else {
    if (!/^\d+$/.test(raw)) { e.preventDefault(); help && (help.textContent = '지문은 숫자만 입력하세요.'); fingerEl.focus(); return; }
}

    // 중복 제출 방지
    // if (submitBtn) { submitBtn.disabled = true; submitBtn.textContent = submitLabel === '등록' ? '등록 중...' : '수정 중...'; }
});

    // 외부에서 수정 모달을 열 때 값 주입 가능하게 노출(선택)
    return { openWith(prefill){ open(prefill); }, close };
}

    /* =========================
     * 2) 등록 모달 바인딩
     * ========================= */
    const createModal = bindEnterModal({
    openBtnId:       'open-enter-modal',
    modalId:         'enterModal',
    formId:          'enterCreateForm',
    userIdInputId:   'enterUserId',
    fingerInputId:   'enterFinger',
    submitBtnId:     'enterSubmitBtn',
    helpId:          'enterHelp',
    submitLabel:     '등록',
    allowMulti:      true
});

    /* =========================
     * 3) 수정 모달 바인딩
     *    - 선택된 행에서 아이디/지문을 가져와 미리 채우고 열 수도 있음
     * ========================= */
    const updateModal = bindEnterModal({
    openBtnId:       'open-enter-modal2',
    modalId:         'enterModal2',
    formId:          'enterUpdateForm',
    userIdInputId:   'enterUserId2',
    fingerInputId:   'enterFinger2',
    submitBtnId:     'enterSubmitBtn2',
    helpId:          'enterHelp2',
    submitLabel:     '수정',
    allowMulti:      true
});


    // (선택) 테이블에서 선택된 행 데이터로 수정 모달 열기 예시
    // document.getElementById('open-enter-modal2')?.addEventListener('click', () => {
    //   const selectedRow = /* 선택된 행에서 값 읽어오기 */;
    //   updateModal.openWith({ userId: 'hong', fingers: '1,2' });
    // });

    const deleteModal = bindEnterModal({
        //openBtnId:     'open-enter-modal3',   // 삭제 버튼
        modalId:       'deleteEnterModal',
        formId:        'deleteEnterForm',
        userIdInputId: 'deleteUserId',        // ★ 여기가 핵심
        submitBtnId:   'enterSubmitBtn3',
        submitLabel:   '삭제',
        allowMulti:    false
    });

// 체크된 행에서 userId 얻기(예시)
    function getSelectedUserId() {
        // 1) 체크박스 방식 예시
        const row = document.querySelector('tbody input[type="checkbox"]:checked')?.closest('tr');
        return row?.dataset.userId        // <tr data-user-id="hong">
            || row?.querySelector('[data-user-id]')?.dataset.userId
            || row?.querySelector('.col-user-id')?.textContent?.trim();
    }

// 삭제 버튼
// 2) 수동 클릭 핸들러에서만 열기
    document.getElementById('open-enter-modal3')?.addEventListener('click', (e) => {
        const uid = getSelectedUserId();
        if (!uid) {
            alert('삭제할 대상을 선택하세요.');
            return; // 여기서 끝! 모달 안 열림
        }
        // hidden 값 채우고
        document.getElementById('deleteUserId').value = uid;
        // 이제 모달 열기
        deleteModal.openWith({ userId: uid });
    });




})();
