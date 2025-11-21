document.addEventListener('DOMContentLoaded', function () {

    const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    // 01로 시작, 중간 3~4자리, 마지막 4자리 (하이픈은 있어도/없어도 허용)
    const TEL_RE = /^01[0-9]-?\d{3,4}-?\d{4}$/;


    // === 1) 폼 submit 시 검증 ===
    const $form = document.querySelector('.profile-form');
    if ($form) {
        $form.addEventListener('submit', function (e) {
            // 이메일
            const emailInput = document.getElementById('email');
            const phoneInput = document.getElementById('phone');
            const pwInput    = document.getElementById('password');
            const pwChkInput = document.getElementById('passwordChk');

            const email = emailInput ? emailInput.value.trim() : '';
            const phone = phoneInput ? phoneInput.value.trim() : '';
            const pw    = pwInput ? pwInput.value.trim() : '';
            const pwChk = pwChkInput ? pwChkInput.value.trim() : '';

            // 1) 이메일 형식 체크
            if (email && !EMAIL_RE.test(email)) {
                e.preventDefault();
                MessageUtil.alert('이메일 형식이 올바르지 않습니다.\n예: example@test.com');
                emailInput.focus();
                return;
            }

            // 2) 전화번호 형식 체크
            if (phone && !TEL_RE.test(phone)) {
                e.preventDefault();
                MessageUtil.alert('전화번호 형식이 올바르지 않습니다.\n예: 010-1234-5678');
                phoneInput.focus();
                return;
            }

            // 3) 비밀번호 / 비밀번호 확인 체크
            //    - 둘 다 비어있으면: "비번 변경 안 함" → 통과
            //    - 둘 중 하나라도 값이 있으면 같은지?
            if (pw || pwChk) {
                // 둘 중 하나만 채운 경우
                if (!pw || !pwChk) {
                    e.preventDefault();
                    MessageUtil.alert('비밀번호와 비밀번호 확인을 모두 입력하세요.');
                    ( !pw ? pwInput : pwChkInput ).focus();
                    return;
                }

                // 값이 다른 경우
                if (pw !== pwChk) {
                    e.preventDefault();
                    MessageUtil.alert('비밀번호가 일치하지 않습니다.');
                    pwChkInput.focus();
                    return;
                }

            }

            // 여기까지 통과하면 submit 계속 진행 → 서버 검증으로 넘어감
        });
    }

    // === 2) (이미 있는 경우) 삭제 버튼 confirm 예시 ===
    const btnDelete = document.getElementById('btn-delete');
    if (btnDelete) {
        btnDelete.addEventListener('click', function () {
            const userId = this.dataset.userId;

            MessageUtil.confirm(
                userId + ' 계정을 삭제하시겠습니까?',
                function (confirmed) {
                    if (confirmed) {
                        document.getElementById('deleteForm').submit();
                    }
                }
            );
        });
    }

});
