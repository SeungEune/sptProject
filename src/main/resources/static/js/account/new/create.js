document.getElementById('id-check-btn').addEventListener('click', async () => {
    const input = document.getElementById('user-id');
    const result = document.getElementById('id-check-result');
    const userId = input.value.trim();
    if (!userId) {
        result.textContent = '아이디를 입력하세요.';
        result.style.color = 'red';
        return;
    }

    const res = await fetch(`/account/create/id-check?userId=${encodeURIComponent(userId)}`);
    const text = await res.text(); // "OK" | "DUPLICATED"

    if (text === 'OK') {
        result.textContent = '사용 가능한 아이디입니다.';
        result.style.color = 'green';
        document.getElementById('id-checked').value = 'true';   // ✅ 여기!
    } else {
        result.textContent = '이미 사용 중인 아이디입니다.';
        result.style.color = 'red';
        document.getElementById('id-checked').value = 'false';  // 혹시 몰라 초기화
    }
});

document.querySelector('.profile-form').addEventListener('submit', function (e) {
    const checked = document.getElementById('id-checked').value === 'true';

    if (!checked) {
        e.preventDefault(); // ✅ 서버로 안 보내고 막기
        alert('아이디 중복체크를 먼저 해주세요.');
    }
});