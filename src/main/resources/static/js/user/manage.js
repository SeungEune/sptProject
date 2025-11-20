(() => {
    if (window.__ACCOUNT_FORM_BOUND) return;
    window.__ACCOUNT_FORM_BOUND = true;

    const form   = document.getElementById('user-form');
    const inputs = {
        id:       document.getElementById('id-input'),
        pw:       document.getElementById('pw-input'),
        pw2:      document.getElementById('pw-input2'),
        name:     document.getElementById('name-input'),
        position: document.getElementById('position-input'),
        email:    document.getElementById('email-input'),
        phone:    document.getElementById('pn-input'),
    };
    const pw2Row    = document.getElementById('pw2-row');
    const btnEdit   = document.getElementById('update-btn');
    const btnDelete = document.getElementById('delete-btn');

    function takeSnapshot() {
        return {
            pw:       inputs.pw?.value ?? "",
            pw2:      inputs.pw2?.value ?? "",
            name:     inputs.name?.value ?? "",
            position: inputs.position?.value ?? "",
            email:    inputs.email?.value ?? "",
            phone:    inputs.phone?.value ?? "",
        };
    }
    function restoreSnapshot(s) {
        if (!s) return;
        if (inputs.pw)       inputs.pw.value       = s.pw;
        if (inputs.pw2)      inputs.pw2.value      = s.pw2;
        if (inputs.name)     inputs.name.value     = s.name;
        if (inputs.position) inputs.position.value = s.position;
        if (inputs.email)    inputs.email.value    = s.email;
        if (inputs.phone)    inputs.phone.value    = s.phone;
    }

    let editing = false;
    let snapshot = null;

    function setEditMode(on) {
        editing = on;

        if (inputs.id) { inputs.id.readOnly = true; inputs.id.disabled = true; }

        [inputs.pw, inputs.pw2, inputs.name, inputs.position, inputs.email, inputs.phone]
            .filter(Boolean)
            .forEach(el => el.readOnly = !on);

        if (pw2Row) pw2Row.hidden = !on;
        if (inputs.pw)  inputs.pw.required  = on;
        if (inputs.pw2) inputs.pw2.required = on;

        if (btnEdit)   btnEdit.textContent   = on ? '확인' : '수정';
        if (btnDelete) btnDelete.textContent = on ? '취소' : '삭제';

        btnEdit?.classList.toggle('is-confirm', on);
        btnDelete?.classList.toggle('is-cancel', on);
    }

    // 한국형 전화번호 간단 검증: 02/0XX/0XXX 모두 허용, 하이픈 선택
    const TEL_RE = /^(\d{2,4}-?\d{3,4}-?\d{4})$/;

    // (선택) 숫자만 입력했으면 보기 좋게 하이픈을 넣어주는 보정
    function normalizePhone(v) {
        const d = (v || '').replace(/\D/g, '');
        if (d.length === 11) return `${d.slice(0,3)}-${d.slice(3,7)}-${d.slice(7)}`; // 010-1234-5678
        if (d.length === 10) {
            // 02-XXXX-XXXX(서울) or 0XX-XXX-XXXX 패턴 구분
            if (d.startsWith('02')) return `02-${d.slice(2,6)}-${d.slice(6)}`;
            return `${d.slice(0,3)}-${d.slice(3,6)}-${d.slice(6)}`;
        }
        if (d.length === 9 && d.startsWith('02')) return `02-${d.slice(2,5)}-${d.slice(5)}`; // 02-XXX-XXXX
        return v; // 그 외는 그대로 둠
    }

    // 수정/확인
    btnEdit?.addEventListener('click', () => {
        if (!editing) {
            snapshot = takeSnapshot();
            setEditMode(true);
            return;
        }

        // (선택) 직책 필수면 주석 해제
        // if (inputs.position && !inputs.position.value.trim()) {
        //   alert('직책을 입력하세요.');
        //   inputs.position.focus();
        //   return;
        // }

        // 비밀번호/이메일 검증
        if (inputs.pw?.value?.length < 8) {
            alert('8자리 미만은 불가합니다.');
            inputs.pw.focus();
            return;
        }
        if (inputs.pw && inputs.pw2 && inputs.pw.value !== inputs.pw2.value) {
            alert('비밀번호와 비밀번호 확인이 일치하지 않습니다.');
            inputs.pw2.focus();
            return;
        }
        const em = inputs.email?.value?.trim() ?? '';
        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(em)) {
            alert('이메일 형식이 맞지 않습니다.');
            inputs.email?.focus();
            return;
        }

        // ✅ 전화번호 검증 + (선택) 하이픈 보정
        if (inputs.phone) {
            const raw = inputs.phone.value.trim();
            if (raw) {
                const normalized = normalizePhone(raw);
                inputs.phone.value = normalized;
                if (!TEL_RE.test(normalized)) {
                    alert('전화번호 형식이 올바르지 않습니다. 예) 010-1234-5678');
                    inputs.phone.focus();
                    return;
                }
            }
        }

        form?.submit();
    });

    // 삭제/취소 (삭제 모달은 다른 스크립트에서 처리)
    btnDelete?.addEventListener('click', () => {
        if (!editing) return;
        restoreSnapshot(snapshot);
        setEditMode(false);
    });

    setEditMode(false);
})();
