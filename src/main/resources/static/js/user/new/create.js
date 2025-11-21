document.addEventListener('DOMContentLoaded', function () {

    // 아이디 중복체크 버튼
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

    // 폼 submit 시, 중복체크 했는지 검사
    $('.profile-form').on('submit', function (e) {
        const idChecked = $('#id-checked').val() === 'true';

        if (!idChecked) {
            e.preventDefault();
            alert('아이디 중복체크를 먼저 해주세요.');
        }
    });

});
