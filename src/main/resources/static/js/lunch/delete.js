/**
 * delete.js
 * 점심/커피 삭제 화면 전용 스크립트
 */

document.addEventListener('DOMContentLoaded', function() {
    // 1. 데이터 가져오기
    const userList = window.lunchData ? window.lunchData.userList : [];
    const existingParticipants = window.lunchData ? window.lunchData.existingParticipants : '';

    const container = document.getElementById('amount-list-container');
    const placeholder = document.getElementById('amount-placeholder');
    const summaryInput = document.getElementById('participant-summary');

    // 2. 참여자 목록 렌더링
    if (existingParticipants) {
        placeholder.style.display = 'none';

        const list = existingParticipants.split(',');
        const names = [];

        list.forEach(item => {
            const parts = item.split(':');
            if (parts.length !== 2) return;

            const userId = parts[0].trim();
            const amount = parts[1].trim();

            const user = userList.find(u => u.userId === userId);
            if (!user) return;

            names.push(user.userName);

            // UI 생성 (읽기 전용)
            const row = document.createElement('div');
            row.className = 'amount-row';

            const nameSpan = document.createElement('span');
            nameSpan.className = 'name';
            nameSpan.textContent = user.userName;

            const group = document.createElement('div');
            group.className = 'amount-input-group';

            const amountInput = document.createElement('input');
            amountInput.type = 'number';
            amountInput.value = amount;
            amountInput.disabled = true; // 수정 불가

            // CSS 클래스로 스타일 제어 권장 (여기선 인라인 제거하고 CSS에 맡김)

            const unitSpan = document.createElement('span');
            unitSpan.textContent = '원';

            group.appendChild(amountInput);
            group.appendChild(unitSpan);
            // 삭제 버튼은 생성하지 않음 (읽기 전용)

            row.appendChild(nameSpan);
            row.appendChild(group);

            container.appendChild(row);
        });

        // 3. 왼쪽 요약 필드 업데이트
        if (names.length > 0) {
            if (names.length <= 2) {
                summaryInput.value = names.join(', ');
            } else {
                summaryInput.value = `${names[0]}, ${names[1]} 외 ${names.length - 2}명`;
            }
        }
    } else {
        placeholder.textContent = "참석자가 없습니다.";
        summaryInput.value = "없음";
    }
});

// --- 삭제 확인 ---
function confirmDelete() {
    Swal.fire({
        title: '내역 삭제',
        text: '정말로 이 내역을 삭제하시겠습니까?', // 문구 조금 더 명확하게
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '삭제',
        cancelButtonText: '취소',
        allowOutsideClick: false,
        buttonsStyling: true,
        customClass: {
            confirmButton: 'btn-red',
            cancelButton: 'btn-gray'
        }
    }).then((result) => {
        if (result.isConfirmed) {
            document.querySelector('.entry-form').submit();
        }
    });
}