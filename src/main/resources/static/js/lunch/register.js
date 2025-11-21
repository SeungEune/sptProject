// 전역 변수 선언 (HTML에서 넘어온 userList가 없을 경우를 대비)
const selectedParticipants = new Set();
let selectedPayerId = null;


// --- 참석자 검색 및 필터링 ---
function filterParticipants() {
    if (typeof userList === 'undefined') {
        console.error("userList 데이터가 로드되지 않았습니다.");
        return;
    }

    const query = document.getElementById('participant-search').value.toLowerCase().trim();
    const resultsDiv = document.getElementById('search-results');

    // 검색어가 없어도 전체 목록 표시
    const filtered = userList.filter(user =>
        (Util.isEmpty(query) || user.userName.toLowerCase().includes(query)) &&
        !selectedParticipants.has(user.userId)
    );

    if (filtered.length === 0) {
        resultsDiv.innerHTML = '<div class="p-2 text-sm text-gray-500 text-center">검색 결과가 없습니다.</div>';
        resultsDiv.style.display = 'block';
        return;
    }

    // [중요] onclick에 event 객체 전달 (event.stopPropagation() 사용을 위해)
    resultsDiv.innerHTML = filtered.map(user =>
        `<div onclick="addParticipant('${user.userId}', '${user.userName}', event)">
            ${user.userName}
        </div>`
    ).join('');

    resultsDiv.style.display = 'block';
}

// --- 참석자 추가 ---
function addParticipant(userId, userName, event) {
    if (event) {
        event.stopPropagation();
    }

    if (selectedParticipants.has(userId)) return;

    selectedParticipants.add(userId);

    const container = document.getElementById('amount-list-container');
    const placeholder = document.getElementById('amount-placeholder');
    placeholder.style.display = 'none';

    // --- Row 생성 ---
    const amountRow = document.createElement('div');
    amountRow.className = 'amount-row';
    amountRow.id = 'participant-row-' + userId;

    // 이름
    const nameSpan = document.createElement('span');
    nameSpan.className = 'name';
    nameSpan.innerText = userName;

    // 금액 입력 그룹
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
    hiddenAmountInput.value = "0";
    hiddenAmountInput.id = "hidden-amount-" + userId;

    uiAmountInput.addEventListener("input", () => {
        hiddenAmountInput.value = uiAmountInput.value;
    });

    hiddenFields.appendChild(hiddenUserInput);
    hiddenFields.appendChild(hiddenAmountInput);

    // 입력창 초기화 및 포커스 유지 (연속 입력)
    const searchInput = document.getElementById('participant-search');
    searchInput.value = '';
    searchInput.focus();

    // 목록 갱신 (닫히지 않음)
    filterParticipants();
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

    const container = document.getElementById('amount-list-container');
    const placeholder = document.getElementById('amount-placeholder');

    const rows = container.querySelectorAll('.amount-row');
    if (rows.length === 0) {
        placeholder.style.display = 'block';
    }

    // 삭제 시에도 목록 갱신 (혹시 검색창에 검색어가 있다면 반영)
    if(document.getElementById('search-results').style.display === 'block'){
        filterParticipants();
    }
}


// ============================================================
// 2. 계산자 (Payer) 관련 로직 (수정됨)
// ============================================================

// --- 계산자 검색 및 필터링 ---
function filterPayers() {
    if (typeof userList === 'undefined') return;

    const query = document.getElementById('payer-search').value.toLowerCase().trim();
    const resultsDiv = document.getElementById('payer-results');

    // [수정] 검색어 없어도 전체 목록 표시 (빈 값 체크 제거)

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

// --- 계산자 선택 ---
function selectPayer(userId, userName) {
    selectedPayerId = userId;
    document.getElementById('payerId').value = userId;
    document.getElementById('payer-search').value = userName;
    // 계산자는 1명이므로 선택 후 닫는 것이 자연스러움
    document.getElementById('payer-results').style.display = 'none';
}


// ============================================================
// 3. 공통 유틸 및 이벤트
// ============================================================

// 1/N 계산 함수
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

// 확인 모달
function confirmAdd() {
    const form = document.querySelector('.entry-form');
    // 1. 가게명, 날짜 등 기본 HTML 태그 검사
    // (이 단계에서 storeName이 비어있으면 브라우저 말풍선이 뜨고 멈춥니다)
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const storeName = document.getElementById('store-name').value.trim();
    if (!storeName) {
        MessageUtil.alert('가게 이름을 입력해주세요.');
        return;
    }

    // 3. 참석자 수동 검사
    // 전역 변수 selectedParticipants의 개수를 확인합니다.
    if (selectedParticipants.size === 0) {
        MessageUtil.alert('참석자를 최소 1명 이상 추가해주세요.');
        return;
    }

    // 2. 계산자 수동 검사
    const payerValue = document.getElementById('payerId').value;
    if (!payerValue) {
        // 서버로 가기 전에 막고 알림창 띄움
        MessageUtil.alert('계산자(결제자)를 선택해주세요.');
        return;
    }

    // 4. 모든 검사 통과 시 서버 전송 확인창 표시
    MessageUtil.confirmed(
        '내역 등록',
        function() { form.submit(); },
        '등록',
        '취소',
        '현재 내용으로 새로 등록하시겠습니까?'
    );
}

// --- DOM 로드 후 실행 ---
// --- DOM 로드 후 실행 ---
document.addEventListener('DOMContentLoaded', function() {
    // URL 파라미터에서 error 값 확인
    const urlParams = new URLSearchParams(window.location.search);
    const error = urlParams.get('error');

    if (error) {
        if (error === 'storeName') {
            MessageUtil.alert('가게 이름을 입력해주세요.');
        }
        else if (error === 'payerId') {
            MessageUtil.alert('계산자(결제자)를 선택해주세요.');
        }
        else if (error === 'participants') {
            MessageUtil.alert('참석자를 최소 1명 이상 추가해주세요.');
        }

    }


    // --- 기존 로직 유지 ---
    const btnDutchPay = document.getElementById('btnDutchPay');
    if (btnDutchPay) {
        btnDutchPay.addEventListener('click', calculateDutchPay);
    }

    // [참석자] 입력창 이벤트: 클릭/포커스 시 전체 목록 노출
    const participantSearchInput = document.getElementById('participant-search');
    if (participantSearchInput) {
        participantSearchInput.addEventListener('click', filterParticipants);
        participantSearchInput.addEventListener('focus', filterParticipants);
    }

    // [계산자] 입력창 이벤트: 클릭/포커스 시 전체 목록 노출
    const payerSearchInput = document.getElementById('payer-search');
    if (payerSearchInput) {
        payerSearchInput.addEventListener('click', filterPayers);
        payerSearchInput.addEventListener('focus', filterPayers);
    }

    // 드롭다운 자동 숨김 (영역 밖 클릭 시)
    document.addEventListener('click', function(event) {
        const pInput = document.getElementById('participant-search');
        const payInput = document.getElementById('payer-search');

        if (pInput) {
            const container = pInput.closest('.form-group');
            // 클릭된 요소가 컨테이너 내부가 아니면 닫기
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