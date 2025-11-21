document.addEventListener('DOMContentLoaded', () => {

    // ===== 0) 정규식 선언 =====
    const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    // 01로 시작, 중간 3~4자리, 마지막 4자리 (하이픈은 있어도/없어도 허용)
    const TEL_RE = /^01[0-9]-?\d{3,4}-?\d{4}$/;


    // 템플릿에서 세팅해준 값 사용
    const { mode: initialMode = 'view', enter_Id: enterId = '' } = window.ENTER_PAGE || {};

    // === 1) 게스트/직원 섹션 토글 ===
    function toggleTypeSection() {
        const type  = document.querySelector('input[name="type"]:checked')?.value;
        const $guest = document.getElementById('guest-section');
        const $emp   = document.getElementById('emp-section');

        if (!$guest || !$emp) return;

        if (type === 'GUEST') {
            $guest.classList.remove('hidden');
            $emp.classList.add('hidden');
        } else {
            $guest.classList.add('hidden');
            $emp.classList.remove('hidden');
        }
    }

    // === 2) 뷰/수정 모드 토글 ===
    function setMode(mode) {
        const $form = document.querySelector('.profile-form');
        if (!$form) return;

        const $inputs = $form.querySelectorAll('input, select');
        const isView = (mode === 'view');

        $form.classList.toggle('view-mode', isView);
        $form.classList.toggle('edit-mode', !isView);

        $inputs.forEach(el => {
            if (el.type === 'hidden') return;

            // 항상 잠금(직원 고정 필드)
            if (el.classList.contains('emp-fixed')) {
                if (el.tagName === 'INPUT') {
                    el.readOnly = true;
                } else if (el.tagName === 'SELECT') {
                    el.disabled = true;
                }
                return;
            }

            // 나머지는 모드에 따라
            if (el.tagName === 'INPUT') {
                if (el.type === 'radio') {
                    // type은 수정 불가
                    el.disabled = true;
                } else {
                    el.readOnly = isView;
                }
            } else if (el.tagName === 'SELECT') {
                el.disabled = isView;
            }
        });

        const $editBtn   = document.getElementById('editBtn');
        const $saveBtn   = document.getElementById('saveBtn');
        const $cancelBtn = document.getElementById('cancelBtn');
        const $deleteBtn = document.getElementById('deleteBtn');
        const $listBtn   = document.getElementById('listBtn');

        $editBtn  && $editBtn.classList.toggle('hidden', !isView);
        $saveBtn  && $saveBtn.classList.toggle('hidden', isView);
        $cancelBtn&& $cancelBtn.classList.toggle('hidden', isView);
        $deleteBtn&& $deleteBtn.classList.toggle('hidden', !isView);
        $listBtn  && $listBtn.classList.toggle('hidden', !isView);
    }

    // === 3) 초기 바인딩 ===

    // (1) 라디오 변경 시 게스트/직원 영역 토글
    document.querySelectorAll('input[name="type"]').forEach(radio => {
        radio.addEventListener('change', toggleTypeSection);
    });
    toggleTypeSection();   // 최초 1번 호출

    // (2) 서버에서 넘겨준 mode 로 초기 모드 설정
    setMode(initialMode);

    // (3) 수정 버튼 -> edit 모드로
    const $editBtn = document.getElementById('editBtn');
    if ($editBtn) {
        $editBtn.addEventListener('click', () => setMode('edit'));
    }

    // === 4) 삭제 팝업 ===
    const $deleteBtn = document.getElementById('deleteBtn');

    if ($deleteBtn && enterId) {
        $deleteBtn.addEventListener('click', () => {
            MessageUtil.confirm(enterId + " 출입을 삭제하시겠습니까?", function (confirmed) {
                if (confirmed) {
                    document.getElementById('deleteForm')?.submit();
                }
            });
        });
    }

    // === 5) 폼 submit 시, 게스트일 때 이메일/전화번호 검증 ===
    const $form = document.querySelector('.profile-form');
    if ($form) {
        $form.addEventListener('submit', (e) => {
            const type = document.querySelector('input[name="type"]:checked')?.value;

            if (type !== 'GUEST') return;   // 직원이면 통과

            const guestEmail = document.getElementById('guest-email')?.value.trim() || '';
            const guestPhone = document.getElementById('guest-phone')?.value.trim() || '';

            if (guestEmail && !EMAIL_RE.test(guestEmail)) {
                e.preventDefault();
                MessageUtil.alert('게스트 이메일 형식이 올바르지 않습니다.\n예: example@test.com');
                document.getElementById('guest-email')?.focus();
                return;
            }

            if (guestPhone && !TEL_RE.test(guestPhone)) {
                e.preventDefault();
                MessageUtil.alert('게스트 전화번호 형식이 올바르지 않습니다.\n예: 010-1234-5678');
                document.getElementById('guest-phone')?.focus();
                return;
            }
        });
    }

});
