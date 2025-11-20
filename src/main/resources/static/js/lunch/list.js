/**
 * 날짜 범위 텍스트 업데이트 (전월 26일 ~ 당월 25일)
 */
function updateDateRange(dateStr) {
    if (!dateStr) return;

    const endDate = new Date(dateStr + "-25");
    const startDate = new Date(endDate);
    startDate.setMonth(startDate.getMonth() - 1);
    startDate.setDate(26);

    const startYear = startDate.getFullYear();
    const startMonth = startDate.getMonth() + 1;
    const startDay = startDate.getDate();

    const endMonth = endDate.getMonth() + 1;
    const endDay = endDate.getDate();

    const text = `${startYear}년 ${startMonth}월 ${startDay}일 ~ ${endMonth}월 ${endDay}일`;

    const textSpan = document.getElementById('date-range-text');
    if (textSpan) {
        textSpan.innerText = text;
    }
}

/**
 * 정산 완료 확인
 */
function confirmAdd(form) {
    Swal.fire({
        title: '정산 완료',
        text: '정산을 완료하시겠습니까?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: '예',
        cancelButtonText: '아니요',
        customClass: {
            confirmButton: 'btn-blue',
            cancelButton: 'btn-gray'
        }
    }).then((result) => {
        if (result.isConfirmed) {
            form.submit();
        }
    });
}

/**
 * 정산 취소 확인
 */
function confirmDelete(form) {
    Swal.fire({
        title: '정산 취소',
        text: '정산을 취소하시겠습니까?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '예',
        cancelButtonText: '아니요',
        customClass: {
            confirmButton: 'btn-red',
            cancelButton: 'btn-gray'
        }
    }).then((result) => {
        if (result.isConfirmed) {
            form.submit();
        }
    });
}

// --- DOM 로드 후 실행 ---
document.addEventListener("DOMContentLoaded", function() {
    // 1. 초기 날짜 텍스트 설정
    const input = document.getElementById('searchMonthInput');
    if(input && input.value) {
        updateDateRange(input.value);
    }

    // 2. DETAIL 행 금액 채우기 로직
    const headers = document.querySelectorAll('thead th[data-user-id]');
    const userIdsInOrder = Array.from(headers).map(th => th.getAttribute('data-user-id'));
    const rows = document.querySelectorAll('tbody tr[data-participants]');

    rows.forEach(row => {
        const participantString = row.getAttribute('data-participants');
        if (!participantString) return;

        const participantMap = new Map();
        participantString.split(',').forEach(pair => {
            const parts = pair.split(':');
            if (parts.length === 2) {
                const userId = parts[0].trim();
                const amount = parseInt(parts[1].trim());
                participantMap.set(userId, amount.toLocaleString());
            }
        });

        userIdsInOrder.forEach(userId => {
            const cell = row.querySelector('td.cell-user-' + userId);
            if (cell) {
                if (participantMap.has(userId)) {
                    cell.innerText = participantMap.get(userId);
                } else {
                    cell.innerText = '-';
                }
            }
        });
    });
});