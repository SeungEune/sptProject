// 전역 변수
const selectedParticipants = new Set();
let selectedPayerId = null;

const MESSAGES = {
    storeName: '가게 이름을 입력해주세요.',
    payerId: '계산자(결제자)를 선택해주세요.',
    participants: '참석자를 최소 1명 이상 추가해주세요.',
    amount: '참석자 금액은 0원 이상이어야 합니다.'
};

document.addEventListener('DOMContentLoaded', function() {
    // 1. 서버 리다이렉트 에러 처리
    const urlParams = new URLSearchParams(window.location.search);
    const error = urlParams.get('error');

    if (error && MESSAGES[error]) {
        MessageUtil.alert(MESSAGES[error]);
    }

    // 2. 1/N 버튼 이벤트
    const btnDutchPay = document.getElementById('btnDutchPay');
    if (btnDutchPay) {
        btnDutchPay.addEventListener('click', calculateDutchPay);
    }

    // 3. 참석자 검색 이벤트
    const participantSearchInput = document.getElementById('participant-search');
    if (participantSearchInput) {
        participantSearchInput.addEventListener('click', filterParticipants);
        participantSearchInput.addEventListener('focus', filterParticipants);
    }

    // 4. 계산자 검색 이벤트
    const payerSearchInput = document.getElementById('payer-search');
    if (payerSearchInput) {
        payerSearchInput.addEventListener('click', filterPayers);
        payerSearchInput.addEventListener('focus', filterPayers);

        // 계산자 이름 수정 시 ID 초기화
        payerSearchInput.addEventListener('input', function() {
            document.getElementById('payerId').value = '';
            selectedPayerId = null;
            filterPayers();
        });
    }

    // 5. 드롭다운 닫기 이벤트
    document.addEventListener('click', function(event) {
        const pInput = document.getElementById('participant-search');
        const payInput = document.getElementById('payer-search');

        if (pInput) {
            const container = pInput.closest('.form-group');
            if (!container.contains(event.target)) {
                const resDiv = document.getElementById('search-results');
                if(resDiv) resDiv.style.display = 'none';
            }
        }

        if (payInput) {
            const container = payInput.closest('.form-group');
            if (!container.contains(event.target)) {
                const resDiv = document.getElementById('payer-results');
                if(resDiv) resDiv.style.display = 'none';
            }
        }
    });
});

// --- 참석자 검색 및 필터링 ---
function filterParticipants() {
    if (typeof userList === 'undefined') {
        console.error("userList 데이터가 로드되지 않았습니다.");
        return;
    }

    const query = document.getElementById('participant-search').value.toLowerCase().trim();
    const resultsDiv = document.getElementById('search-results');

    const filtered = userList.filter(user =>
        (Util.isEmpty(query) || user.userName.toLowerCase().includes(query)) &&
        !selectedParticipants.has(user.userId)
    );

    if (filtered.length === 0) {
        resultsDiv.innerHTML = '<div class="p-2 text-sm text-gray-500 text-center">검색 결과가 없습니다.</div>';
        resultsDiv.style.display = 'block';
        return;
    }

    resultsDiv.innerHTML = filtered.map(user =>
        `<div onclick="addParticipant('${user.userId}', '${user.userName}', event)">
            ${user.userName}
        </div>`
    ).join('');

    resultsDiv.style.display = 'block';
}

// --- 참석자 추가 ---
function addParticipant(userId, userName, event) {
    if (event) event.stopPropagation();
    if (selectedParticipants.has(userId)) return;

    selectedParticipants.add(userId);

    const container = document.getElementById('amount-list-container');
    const placeholder = document.getElementById('amount-placeholder');
    placeholder.style.display = 'none';

    const amountRow = document.createElement('div');
    amountRow.className = 'amount-row';
    amountRow.id = 'participant-row-' + userId;

    const nameSpan = document.createElement('span');
    nameSpan.className = 'name';
    nameSpan.innerText = userName;

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

    const searchInput = document.getElementById('participant-search');
    searchInput.value = '';
    if(event) searchInput.focus();
    if(event) filterParticipants();
    else document.getElementById('search-results').style.display = 'none';
}

// --- 참석자 삭제 ---
function removeParticipant(userId) {
    selectedParticipants.delete(userId);

    const rowToRemove = document.getElementById('participant-row-' + userId);
    if (rowToRemove) rowToRemove.remove();

    const hiddenUser = document.getElementById('hidden-user-' + userId);
    const hiddenAmount = document.getElementById('hidden-amount-' + userId);
    if (hiddenUser) hiddenUser.remove();
    if (hiddenAmount) hiddenAmount.remove();

    if (selectedParticipants.size === 0) {
        document.getElementById('amount-placeholder').style.display = 'block';
    }

    if(document.getElementById('search-results').style.display === 'block'){
        filterParticipants();
    }
}

// --- 계산자 검색 및 필터링 ---
function filterPayers() {
    if (typeof userList === 'undefined') return;

    const query = document.getElementById('payer-search').value.toLowerCase().trim();
    const resultsDiv = document.getElementById('payer-results');

    const filtered = userList.filter(user =>
        (Util.isEmpty(query) || user.userName.toLowerCase().includes(query))
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

function selectPayer(userId, userName) {
    selectedPayerId = userId;
    document.getElementById('payerId').value = userId;
    document.getElementById('payer-search').value = userName;
    document.getElementById('payer-results').style.display = 'none';
}

// --- 1/N 계산 ---
function calculateDutchPay() {
    const uiInputs = Array.from(document.querySelectorAll('.amount-input-group input[type="number"]'));
    if (uiInputs.length === 0) {
        MessageUtil.alert("참여자를 먼저 추가해주세요.");
        return;
    }
    let baseAmount = 0;
    let found = false;

    for (let input of uiInputs) {
        let val = Util.toFloat(input.value);
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

// 공통 유효성 검사 함수
function validateForm() {
    const form = document.querySelector('.entry-form');
    // HTML 기본 검사 (가게명 required 제거했으므로 날짜 등 체크)
    if (!form.checkValidity()) {
        form.reportValidity();
        return false;
    }

    // 1. 가게명 체크
    const storeName = document.getElementById('store-name').value.trim();
    if (!storeName) {
        MessageUtil.alert(MESSAGES.storeName);
        return false;
    }

    // 2. 참석자 수 체크
    if (selectedParticipants.size === 0) {
        MessageUtil.alert(MESSAGES.participants);
        return false;
    }

    // 금액 체크
    const amountInputs = document.querySelectorAll('.amount-input-group input[type="number"]');
    for (const input of amountInputs) {
        if (!input.value || parseInt(input.value) <= 0) {
            MessageUtil.alert(MESSAGES.amount);
            return false;
        }
    }

    // 계산자 체크
    const payerValue = document.getElementById('payerId').value;
    if (!payerValue) {
        MessageUtil.alert(MESSAGES.payerId);
        return false;
    }

    return true;
}

// 확인 모달
function confirmAdd() {
    // 유효성 검사 실행 (실패 시 중단)
    if (!validateForm()) return;

    const form = document.querySelector('.entry-form');
    MessageUtil.confirmed(
        '내역 등록',
        function() { form.submit(); },
        '등록',
        '취소',
        '현재 내용으로 새로 등록하시겠습니까?'
    );
}