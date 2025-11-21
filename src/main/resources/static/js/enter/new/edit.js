document.addEventListener('DOMContentLoaded', () => {

    // 템플릿에서 세팅해준 값 사용

    const { mode: initialMode = 'view', enter_Id: enterId = '' } = window.ENTER_PAGE || {};

    // === 1) 게스트/직원 섹션 토글 ===
    function toggleTypeSection() {
        const type  = document.querySelector('input[name="type"]:checked')?.value;
        const guest = document.getElementById('guest-section');
        const emp   = document.getElementById('emp-section');

        if (!guest || !emp) return;

        if (type === 'GUEST') {
            guest.classList.remove('hidden');
            emp.classList.add('hidden');
        } else {
            guest.classList.add('hidden');
            emp.classList.remove('hidden');
        }
    }

    // === 2) 뷰/수정 모드 토글 ===
    function setMode(mode) {
        const form = document.querySelector('.profile-form');
        if (!form) return;

        const inputs = form.querySelectorAll('input, select');
        const isView = (mode === 'view');

        form.classList.toggle('view-mode', isView);
        form.classList.toggle('edit-mode', !isView);

        inputs.forEach(el => {
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

        const editBtn   = document.getElementById('editBtn');
        const saveBtn   = document.getElementById('saveBtn');
        const cancelBtn = document.getElementById('cancelBtn');
        const deleteBtn = document.getElementById('deleteBtn');
        const listBtn   = document.getElementById('listBtn');

        editBtn  && editBtn.classList.toggle('hidden', !isView);
        saveBtn  && saveBtn.classList.toggle('hidden', isView);
        cancelBtn&& cancelBtn.classList.toggle('hidden', isView);
        deleteBtn&& deleteBtn.classList.toggle('hidden', !isView);
        listBtn  && listBtn.classList.toggle('hidden', !isView);
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
    const editBtn = document.getElementById('editBtn');
    if (editBtn) {
        editBtn.addEventListener('click', () => setMode('edit'));
    }

    // === 4) 삭제 팝업 ===
    const deleteBtn = document.getElementById('deleteBtn');

    if (deleteBtn && enterId) {
        deleteBtn.addEventListener('click', () => {
            MessageUtil.confirm(enterId+" 정말 삭제할까요?", function(confirmed) {
                if (confirmed) {
                    // 확인 눌렀을 때 하고 싶은 일
                    document.getElementById('deleteForm').submit();
                } else {
                    // 취소 눌렀을 때 하고 싶은 일
                    console.log("삭제 취소");
                }
            });
        });
    }
});
