document.getElementById('id-check-btn').addEventListener('click', async () => {
    const $idInput = document.getElementById('user-id');
    const $chkResult = document.getElementById('id-check-result');
    const userId = $idInput.value.trim();
    if (!userId) {
        $chkResult.textContent = '아이디를 입력하세요.';
        $chkResult.style.color = 'red';
        return;
    }

    const res = await fetch(`/account/create/id-check?userId=${encodeURIComponent(userId)}`);

    if (!res) {
        $chkResult.textContent = '사용 가능한 아이디입니다.';
        $chkResult.style.color = 'green';
        document.getElementById('id-checked').value = 'true';   // ✅ 여기!
    } else {
        $chkResult.textContent = '이미 사용 중인 아이디입니다.';
        $chkResult.style.color = 'red';
        document.getElementById('id-checked').value = 'false';  // 혹시 몰라 초기화
    }
});

document.querySelector('.profile-form').addEventListener('submit', function (e) {
    const $idChecked = document.getElementById('id-checked').value === 'true';

    if (!$idChecked) {
        e.preventDefault(); // ✅ 서버로 안 보내고 막기
        alert('아이디 중복체크를 먼저 해주세요.');
    }
});