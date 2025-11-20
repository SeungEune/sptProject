/* * 파일 경로: src/main/resources/static/js/lunch/register.js
 * 설명: 점심/커피 등록 페이지 전용 스크립트
 */

// 전역 변수 선언 (HTML에서 넘어온 userList가 없을 경우를 대비)
const selectedParticipants = new Set();
let selectedPayerId = null;

// --- 참석자 검색 및 선택 로직 ---
function filterParticipants() {
    // userList가 정의되지 않았을 경우 에러 방지
    if (typeof userList === 'undefined') {
        console.error("userList 데이터가 로드되지 않았습니다.");
        return;
    }

    const query = document.getElementById('participant-search').value.toLowerCase().trim();
    const resultsDiv = document.getElementById('search-results');

    if (query === '') {
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
        `<div onclick="addParticipant('${user.userId}', '${user.userName}')">
            ${user.userName}
        </div>`
    ).join('');

    resultsDiv.style.display = 'block';
}

// --- addParticipant ---
function addParticipant(userId, userName) {
    if (selectedParticipants.has(userId)) return;

    selectedParticipants.add(userId);

    const container = document.getElementById('amount-list-container');
    const placeholder = document.getElementById('amount-placeholder');
    placeholder.style.display = 'none';

    // --- 새 HTML 구조(.amount-row)에 맞게 요소 생성 ---
    const amountRow = document.createElement('div');
    amountRow.className = 'amount-row';
    amountRow.id = 'participant-row-' + userId;

    // 1. 이름
    const nameSpan = document.createElement('span');
    nameSpan.className = 'name';
    nameSpan.innerText = userName;

    // 2. 금액 입력 그룹
    const amountGroup = document.createElement('div');
    amountGroup.className = 'amount-input-group';

    const uiAmountInput = document.createElement('input');
    uiAmountInput.type = 'number';
    uiAmountInput.placeholder = '금액입력';
    uiAmountInput.required = true;

    const unitSpan = document.createElement('span');
    unitSpan.innerText = '원';

    const removeBtn = document.createElement('button');
    removeBtn.type = 'button';
    removeBtn.className = 'remove-btn';
    removeBtn.innerHTML = '<i class="fas fa-times"></i>';
    removeBtn.onclick = () => removeParticipant(userId);

    amountGroup.appendChild(uiAmountInput);
    amountGroup.appendChild(unitSpan);
    amountGroup.appendChild(removeBtn);

    amountRow.appendChild(nameSpan);
    amountRow.appendChild(amountGroup);

    container.appendChild(amountRow);

    const hiddenFields = document.getElementById("hidden-fields");

    const hiddenUserInput = document.createElement("input");
    hiddenUserInput.type = "hidden";
    hiddenUserInput.name = "participantUserIds";
    hiddenUserInput.value = userId;
    hiddenUserInput.id = "hidden-user-" + userId;

    const hiddenAmountInput = document.createElement("input");
    hiddenAmountInput.type = "hidden";
    hiddenAmountInput.name = "participantAmounts";
    hiddenAmountInput.value = "0";
    hiddenAmountInput.id = "hidden-amount-" + userId;

    uiAmountInput.addEventListener("input", () => {
        hiddenAmountInput.value = uiAmountInput.value;
    });

    hiddenFields.appendChild(hiddenUserInput);
    hiddenFields.appendChild(hiddenAmountInput);

    document.getElementById('participant-search').value = '';
    document.getElementById('search-results').style.display = 'none';
}


// --- removeParticipant ---
function removeParticipant(userId) {
    selectedParticipants.delete(userId);

    const rowToRemove = document.getElementById('participant-row-' + userId);
    if (rowToRemove) rowToRemove.remove();

    const hiddenUser = document.getElementById('hidden-user-' + userId);
    const hiddenAmount = document.getElementById('hidden-amount-' + userId);
    if (hiddenUser) hiddenUser.remove();
    if (hiddenAmount) hiddenAmount.remove();

    const container = document.getElementById('amount-list-container');
    const placeholder = document.getElementById('amount-placeholder');

    // placeholder가 있는 상태에서 자식이 1개(placeholder 본인)만 남으면 다시 보여줌
    // 혹은 container에 실제 입력 row가 없는지 체크
    const rows = container.querySelectorAll('.amount-row');
    if (rows.length === 0) {
        placeholder.style.display = 'block';
    }
}

// --- 계산자 검색 ---
function filterPayers() {
    if (typeof userList === 'undefined') return;

    const query = document.getElementById('payer-search').value.toLowerCase().trim();
    const resultsDiv = document.getElementById('payer-results');

    if (query === '') {
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
        `<div onclick="selectPayer('${user.userId}', '${user.userName}')">
            ${user.userName}
        </div>`
    ).join('');

    resultsDiv.style.display = 'block';
}

// --- 계산자 선택 ---
function selectPayer(userId, userName) {
    selectedPayerId = userId;
    document.getElementById('payerId').value = userId;
    document.getElementById('payer-search').value = userName;
    document.getElementById('payer-results').style.display = 'none';
}

// 확인 모달
function confirmAdd() {
    const form = document.querySelector('.entry-form');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }
    MessageUtil.confirmed(
        '내역 등록',                              // 메인 메시지 (Title)
        function() { form.submit(); },           // 확인 시 실행할 콜백
        '등록',                                  // 확인 버튼 텍스트
        '취소',                                  // 취소 버튼 텍스트
        '현재 내용으로 새로 등록하시겠습니까?'        // 서브 메시지 (HTML)
    );
}
// [수정] 1/N 계산 함수 (한 명 금액 -> 전체 복사)
function calculateDutchPay() {
    // 1. 현재 화면에 있는 모든 금액 입력칸(UI Input) 가져오기
    const uiInputs = Array.from(document.querySelectorAll('.amount-input-group input[type="number"]'));

    // 2. 기준이 될 금액 찾기 (가장 먼저 입력된 금액을 찾음)
    let baseAmount = 0;
    let found = false;

    for (let input of uiInputs) {
        let val = parseInt(input.value);
        if (!isNaN(val) && val > 0) {
            baseAmount = val;
            found = true;
            break; // 첫 번째로 발견된 유효한 금액을 기준 금액으로 사용
        }
    }

    // 4. 모든 칸에 같은 금액 채우기
    uiInputs.forEach((uiInput) => {
        uiInput.value = baseAmount;
        uiInput.dispatchEvent(new Event('input', { bubbles: true }));
    });
}
// --- DOMContentLoaded 이벤트: 문서 로드 후 실행 ---
document.addEventListener('DOMContentLoaded', function() {
    // 사이드바 토글 로직
    const closeMenu = document.getElementById('close-menu');
    const sidebar = document.getElementById('sidebar');
    const overlay = document.getElementById('overlay');
    const btnDutchPay = document.getElementById('btnDutchPay');
    if (btnDutchPay) {
        btnDutchPay.addEventListener('click', calculateDutchPay);
    }

    // 드롭다운 자동 숨김 처리
    document.addEventListener('click', function(event) {
        const participantSearchInput = document.getElementById('participant-search');
        const payerSearchInput = document.getElementById('payer-search');

        if (participantSearchInput) {
            const participantContainer = participantSearchInput.closest('.form-group');
            if (!participantContainer.contains(event.target)) {
                const resDiv = document.getElementById('search-results');
                if(resDiv) resDiv.style.display = 'none';
            }
        }

        if (payerSearchInput) {
            const payerContainer = payerSearchInput.closest('.form-group');
            if (!payerContainer.contains(event.target)) {
                const resDiv = document.getElementById('payer-results');
                if(resDiv) resDiv.style.display = 'none';
            }
        }
    });
});