/**
 * update.js
 * 점심/커피 수정 화면 전용 스크립트
 */

// 전역 변수 (데이터는 HTML inline script에서 주입됨)
const selectedParticipants = new Set();
let selectedPayerId = null;

document.addEventListener('DOMContentLoaded', function() {
    // 1. 기존 데이터 로드 (참여자 목록 채우기)
    if (typeof existingParticipants !== 'undefined' && existingParticipants) {
        const participants = existingParticipants.split(',');
        participants.forEach(function(item) {
            const parts = item.split(':');
            if (parts.length === 2) {
                const userId = parts[0].trim();
                const amount = parseInt(parts[1].trim());

                const user = userList.find(u => u.userId === userId);
                if (user) addParticipant(userId, user.userName, amount);
            }
        });
    }

    // 2. 기존 결제자 ID 세팅
    if (typeof existingPayerId !== 'undefined' && existingPayerId) {
        selectedPayerId = existingPayerId;
    }

    // 3. 1/N 버튼 이벤트 연결
    const btnDutchPay = document.getElementById('btnDutchPay');
    if (btnDutchPay) {
        btnDutchPay.addEventListener('click', calculateDutchPay);
    }
});

// --- 참석자 검색 ---
function filterParticipants() {
    const query = document.getElementById('participant-search').value.toLowerCase().trim();
    const resultsDiv = document.getElementById('search-results');

    // [수정] Util.isEmpty 사용
    if (Util.isEmpty(query)) {
        resultsDiv.style.display = 'none';
        resultsDiv.innerHTML = '';
        return;
    }

    const filtered = userList.filter(user =>
        user.userName.toLowerCase().includes(query) && !selectedParticipants.has(user.userId)
    );

    if (filtered.length === 0) {
        resultsDiv.innerHTML = '<div class="p-2 text-sm text-gray-500 text-center">검색 결과가 없습니다.</div>';
        resultsDiv.style.display = 'block';
        return;
    }

    resultsDiv.innerHTML = filtered.map(user =>
        `<div onclick="addParticipant('${user.userId}', '${user.userName}')">${user.userName}</div>`
    ).join('');

    resultsDiv.style.display = 'block';
}

// --- 참석자 추가 ---
function addParticipant(userId, userName, initialAmount = '') {
    if (selectedParticipants.has(userId)) return;

    selectedParticipants.add(userId);

    const container = document.getElementById('amount-list-container');
    const placeholder = document.getElementById('amount-placeholder');
    placeholder.style.display = 'none';

    // UI 요소 생성
    const row = document.createElement('div');
    row.className = 'amount-row';
    row.id = 'participant-row-' + userId;

    // 이름 영역
    const nameSpan = document.createElement('span');
    nameSpan.className = 'name';
    nameSpan.innerText = userName;

    // 입력 그룹
    const group = document.createElement('div');
    group.className = 'amount-input-group';

    const uiAmountInput = document.createElement('input');
    uiAmountInput.type = 'number';
    uiAmountInput.placeholder = '금액입력';
    uiAmountInput.value = initialAmount;
    uiAmountInput.required = true;

    const unitSpan = document.createElement('span');
    unitSpan.innerText = '원';

    const removeBtn = document.createElement('button');
    removeBtn.type = 'button';
    removeBtn.className = 'remove-btn';
    removeBtn.innerHTML = '<i class="fas fa-times"></i>';
    removeBtn.onclick = () => removeParticipant(userId);

    group.appendChild(uiAmountInput);
    group.appendChild(unitSpan);
    group.appendChild(removeBtn);

    row.appendChild(nameSpan);
    row.appendChild(group);
    container.appendChild(row);

    // Hidden Fields 생성
    const hiddenFields = document.getElementById("hidden-fields");

    const hiddenUserInput = document.createElement("input");
    hiddenUserInput.type = "hidden";
    hiddenUserInput.name = "participantUserIds";
    hiddenUserInput.value = userId;
    hiddenUserInput.id = "hidden-user-" + userId;

    const hiddenAmountInput = document.createElement("input");
    hiddenAmountInput.type = "hidden";
    hiddenAmountInput.name = "participantAmounts";
    hiddenAmountInput.value = (initialAmount === '' ? '0' : initialAmount);
    hiddenAmountInput.id = "hidden-amount-" + userId;

    // 값 동기화 이벤트
    uiAmountInput.addEventListener("input", () => {
        hiddenAmountInput.value = uiAmountInput.value === '' ? '0' : uiAmountInput.value;
    });

    hiddenFields.appendChild(hiddenUserInput);
    hiddenFields.appendChild(hiddenAmountInput);

    // 초기화
    document.getElementById('participant-search').value = '';
    document.getElementById('search-results').style.display = 'none';
}

// --- 참석자 삭제 ---
function removeParticipant(userId) {
    selectedParticipants.delete(userId);

    const row = document.getElementById('participant-row-' + userId);
    if (row) row.remove();

    const hiddenUser = document.getElementById('hidden-user-' + userId);
    const hiddenAmount = document.getElementById('hidden-amount-' + userId);
    if (hiddenUser) hiddenUser.remove();
    if (hiddenAmount) hiddenAmount.remove();

    if (selectedParticipants.size === 0) {
        document.getElementById('amount-placeholder').style.display = 'block';
    }
}

// --- 계산자 검색 ---
function filterPayers() {
    const query = document.getElementById('payer-search').value.toLowerCase().trim();
    const resultsDiv = document.getElementById('payer-results');

    if (Util.isEmpty(query)) {
        resultsDiv.style.display = 'none';
        resultsDiv.innerHTML = '';
        return;
    }

    const filtered = userList.filter(user =>
        user.userName.toLowerCase().includes(query)
    );

    if (filtered.length === 0) {
        resultsDiv.innerHTML = '<div class="p-2 text-sm text-gray-500 text-center">검색 결과가 없습니다.</div>';
        resultsDiv.style.display = 'block';
        return;
    }

    resultsDiv.innerHTML = filtered.map(user =>
        `<div onclick="selectPayer('${user.userId}', '${user.userName}')">${user.userName}</div>`
    ).join('');

    resultsDiv.style.display = 'block';
}

function selectPayer(userId, userName) {
    selectedPayerId = userId;
    document.getElementById('payer-search').value = userName;
    document.getElementById('payerId').value = userId;
    document.getElementById('payer-results').style.display = 'none';
}

// [수정] 수정 확인
function confirmUpdate() {
    MessageUtil.confirmed(
        '내역 수정',
        function() { document.querySelector('.entry-form').submit(); },
        '수정',
        '취소',
        '내역을 수정하시겠습니까?'
    );
}

// [수정] 추가(신규등록) 확인
function confirmAdd() {
    MessageUtil.confirmed(
        '내역 등록',
        function() {
            const form = document.querySelector('.entry-form');
            form.action = '/lunch/register.do'; // action 변경
            form.submit();
        },
        '등록',
        '취소',
        '현재 내용으로 새로 등록하시겠습니까? (수정 아님)'
    );
}

// --- 1/N 계산 (금액 복사) ---
function calculateDutchPay() {
    const uiInputs = Array.from(document.querySelectorAll('.amount-input-group input[type="number"]'));

    if (uiInputs.length === 0) {
        MessageUtil.alert("참여자를 먼저 추가해주세요.");
        return;
    }

    let baseAmount = 0;
    let found = false;

    for (let input of uiInputs) {
        let val = parseInt(input.value);
        if (val > 0) {
            baseAmount = val;
            found = true;
            break;
        }
    }

    if (!found) {
        MessageUtil.alert("기준이 될 금액을 한 명의 입력칸에 적어주세요.", function() {
            if(uiInputs[0]) uiInputs[0].focus();
        });
        return;
    }

    uiInputs.forEach((uiInput) => {
        uiInput.value = baseAmount;
        uiInput.dispatchEvent(new Event('input', { bubbles: true }));
    });
}