document.addEventListener('DOMContentLoaded', function () {

    const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;      // 기본 이메일
    // 01로 시작, 중간 3~4자리, 마지막 4자리 (하이픈은 있어도/없어도 허용)
    const TEL_RE = /^01[0-9]-?\d{3,4}-?\d{4}$/;


    // =====================
    // 1) 아이디 중복체크
    // =====================
    $('#id-check-btn').on('click', function () {
        const $idInput   = $('#user-id');
        const $chkResult = $('#id-check-result');
        const $hidden    = $('#id-checked');

        const userId = $.trim($idInput.val());

        if (!userId) {
            $chkResult.text('아이디를 입력하세요.');
            $chkResult.css('color', 'red');
            $hidden.val('false');
            return;
        }

        const url = Util.getRequestUrl(
            '/user/create/id-check.do?userId=' + encodeURIComponent(userId)
        );

        callModule.call(
            url,
            null,
            function (result) {
                const duplicated = (result === true || result === 'true');

                if (!duplicated) {
                    $chkResult.text('사용 가능한 아이디입니다.');
                    $chkResult.css('color', 'green');
                    $hidden.val('true');
                } else {
                    $chkResult.text('이미 사용 중인 아이디입니다.');
                    $chkResult.css('color', 'red');
                    $hidden.val('false');
                }
            },
            true,
            'GET'
        );
    });

    // =====================
    // 2) 폼 submit 시 검증
    // =====================
    $('.profile-form').on('submit', function (e) {

        // 1) 아이디 중복 체크 여부
        const idChecked = $('#id-checked').val() === 'true';
        if (!idChecked) {
            e.preventDefault();
            MessageUtil.alert('아이디 중복체크를 먼저 해주세요.');
            $('#user-id').focus();
            return;
        }

        // 2) 이메일 형식 검사
        const email = $.trim($('#email').val());
        if (email && !EMAIL_RE.test(email)) {
            e.preventDefault();
            MessageUtil.alert('이메일 형식이 올바르지 않습니다.\n예: example@test.com');
            $('#email').focus();
            return;
        }

        // 3) 전화번호 형식 검사
        const phone = $.trim($('#phone').val());
        if (phone && !TEL_RE.test(phone)) {
            e.preventDefault();
            MessageUtil.alert('전화번호 형식이 올바르지 않습니다.\n예: 010-1234-5678');
            $('#phone').focus();
            return;
        }

        // 4) 비밀번호 / 비밀번호 확인 검사
        const pw    = $.trim($('#password').val());
        const pwChk = $.trim($('#password-chk').val());

        // 등록은 비밀번호 필수라고 가정
        if (!pw || !pwChk) {
            e.preventDefault();
            MessageUtil.alert('비밀번호와 비밀번호 확인을 모두 입력하세요.');
            (!pw ? $('#password') : $('#password-chk')).focus();
            return;
        }

        if (pw !== pwChk) {
            e.preventDefault();
            MessageUtil.alert('비밀번호가 일치하지 않습니다.');
            $('#password-chk').focus();
            return;
        }

        // 여기까지 통과하면 submit 계속 진행 → 서버 검증
    });

});
