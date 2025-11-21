document.addEventListener('DOMContentLoaded', function () {

    // 이메일 / 전화번호 정규식
    const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    // 01로 시작, 중간 3~4자리, 마지막 4자리 (하이픈 있어도/없어도 허용)
    const TEL_RE   = /^01[0-9]-?\d{3,4}-?\d{4}$/;

    const $form = document.querySelector('.profile-form');
    if (!$form) return;

    $form.addEventListener('submit', function (e) {

        const emailInput = document.getElementById('email');
        const phoneInput = document.getElementById('phone');
        const pwInput    = document.getElementById('password');
        const pwChkInput = document.getElementById('password-chk');

        const email = emailInput ? emailInput.value.trim() : '';
        const phone = phoneInput ? phoneInput.value.trim() : '';
        const pw    = pwInput ? pwInput.value.trim() : '';
        const pwChk = pwChkInput ? pwChkInput.value.trim() : '';

        // 1) 이메일 형식 검사
        if (email && !EMAIL_RE.test(email)) {
            e.preventDefault();
            MessageUtil.alert('이메일 형식이 올바르지 않습니다.\n예: example@test.com');
            emailInput && emailInput.focus();
            return;
        }

        // 2) 전화번호 형식 검사
        if (phone && !TEL_RE.test(phone)) {
            e.preventDefault();
            MessageUtil.alert('전화번호 형식이 올바르지 않습니다.\n예: 010-1234-5678');
            phoneInput && phoneInput.focus();
            return;
        }

        // 3) 비밀번호 / 비밀번호 확인 검사
        //    - 둘 다 비어있으면: 비번 변경 안 함 → 통과
        //    - 둘 중 하나라도 값이 있으면: 둘 다 입력 + 일치 여부 검사
        if (pw || pwChk) {
            // 하나만 입력했을 때
            if (!pw || !pwChk) {
                e.preventDefault();
                MessageUtil.alert('비밀번호와 비밀번호 확인을 모두 입력하세요.');
                (!pw && pwInput ? pwInput : pwChkInput).focus();
                return;
            }

            // 두 값이 다를 때
            if (pw !== pwChk) {
                e.preventDefault();
                MessageUtil.alert('비밀번호가 일치하지 않습니다.');
                pwChkInput && pwChkInput.focus();
                return;
            }
        }

        // 여기까지 통과하면 submit 진행 → 서버 검증으로 넘어감
    });
});
