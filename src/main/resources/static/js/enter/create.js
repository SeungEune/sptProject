document.addEventListener('DOMContentLoaded', () => {

    // ===== 이메일 / 전화번호 정규식 =====
    const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;      // 예: example@test.com
    // 01로 시작, 중간 3~4자리, 마지막 4자리 (하이픈은 있어도/없어도 허용)
    const TEL_RE = /^01[0-9]-?\d{3,4}-?\d{4}$/;


    // ===== 1) 게스트/직원 섹션 토글 =====
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

    document.querySelectorAll('input[name="type"]').forEach(radio => {
        radio.addEventListener('change', toggleTypeSection);
    });
    toggleTypeSection(); // 처음 로딩 시에도 맞춰주기

    // ===== 2) 폼 submit 시, 게스트일 때 이메일/전화번호 형식 검증 =====
    const $form = document.querySelector('.profile-form');
    if ($form) {
        $form.addEventListener('submit', (e) => {
            const type = document.querySelector('input[name="type"]:checked')?.value;

            // 직원(EMP)일 때는 패스
            if (type !== 'GUEST') return;

            const guestEmail = document.getElementById('guest-email')?.value.trim() || '';
            const guestPhone = document.getElementById('guest-phone')?.value.trim() || '';

            // 이메일 형식 체크 (값이 있을 때만)
            if (guestEmail && !EMAIL_RE.test(guestEmail)) {
                e.preventDefault();
                MessageUtil.alert('게스트 이메일 형식이 올바르지 않습니다.\n예: example@test.com');
                document.getElementById('guest-email')?.focus();
                return;
            }

            // 전화번호 형식 체크 (값이 있을 때만)
            if (guestPhone && !TEL_RE.test(guestPhone)) {
                e.preventDefault();
                MessageUtil.alert('게스트 전화번호 형식이 올바르지 않습니다.\n예: 010-1234-5678');
                document.getElementById('guest-phone')?.focus();
                return;
            }
        });
    }

});
