/**
 * update.js
 * 점심/커피 수정 화면 전용 스크립트
 */

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
    const urlParams = new URLSearchParams(window.location.search);
    const error = urlParams.get('error');

    if (error && MESSAGES[error]) {
        MessageUtil.alert(MESSAGES[error]);
    }

    // 1. 기존 데이터 로드 (참여자 목록 채우기)
    if (typeof existingParticipants !== 'undefined' && existingParticipants) {
        const participants = existingParticipants.split(',');
        participants.forEach(function(item) {
            const parts = item.split(':');
            if (parts.length === 2) {
                const userId = parts[0].trim();
                const amount = parseInt(parts[1].trim());

                const user = userList.find(u => u.userId === userId);
                // [주의] 초기 로드 시에는 event 객체가 없으므로 null 전달
                if (user) addParticipant(userId, user.userName, null, amount);
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

    // 4. [추가] 검색창 이벤트 (클릭/포커스 시 전체 목록 노출)
    const participantSearchInput = document.getElementById('participant-search');
    if (participantSearchInput) {
        participantSearchInput.addEventListener('click', filterParticipants);
        participantSearchInput.addEventListener('focus', filterParticipants);
    }

    const payerSearchInput = document.getElementById('payer-search');
    if (payerSearchInput) {
        payerSearchInput.addEventListener('click', filterPayers);
        payerSearchInput.addEventListener('focus', filterPayers);

        // 사용자가 이름을 지우거나 타이핑을 시작하면, 기존 선택된 ID를 즉시 비워버립니다.
        payerSearchInput.addEventListener('input', function() {
            document.getElementById('payerId').value = ''; // 숨겨진 ID 초기화
            selectedPayerId = null; // 전역 변수 초기화
            filterPayers(); // 검색 목록 갱신
        });
    }

    // 5. [추가] 드롭다운 자동 숨김 (영역 밖 클릭 시)
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
    if (typeof userList === 'undefined') return;

    const query = document.getElementById('participant-search').value.toLowerCase().trim();
    const resultsDiv = document.getElementById('search-results');

    // [수정] 검색어가 없어도 전체 목록 표시
    const filtered = userList.filter(user =>
        (Util.isEmpty(query) || user.userName.toLowerCase().includes(query)) &&
        !selectedParticipants.has(user.userId)
    );

    if (filtered.length === 0) {
        resultsDiv.innerHTML = '<div class="p-2 text-sm text-gray-500 text-center">검색 결과가 없습니다.</div>';
        resultsDiv.style.display = 'block';
        return;
    }

    // [수정] onclick에 event 전달
    resultsDiv.innerHTML = filtered.map(user =>
        `<div onclick="addParticipant('${user.userId}', '${user.userName}', event)">
            ${user.userName}
        </div>`
    ).join('');

    resultsDiv.style.display = 'block';
}

// --- 참석자 추가 ---
// [수정] event 파라미터 추가 및 초기 금액(initialAmount) 지원
function addParticipant(userId, userName, event, initialAmount = '') {
    // 이벤트가 있을 때만 전파 중단 (초기 로딩 시엔 event가 null임)
    if (event) {
        event.stopPropagation();
    }

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

    // 초기화 및 목록 갱신
    const searchInput = document.getElementById('participant-search');
    searchInput.value = '';
    // 초기 로딩이 아닐 때만 포커스 (화면 진입 시 자동 포커스 방지)
    if(event) searchInput.focus();

    // 드롭다운 닫지 않고 내용 갱신 (검색 흐름 유지)
    if(event) filterParticipants();
    else document.getElementById('search-results').style.display = 'none';
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

    // 삭제 시 검색 목록 갱신 (선택했던 사람 다시 나오게)
    if(document.getElementById('search-results').style.display === 'block'){
        filterParticipants();
    }
}

// --- 계산자 검색 및 필터링 ---
function filterPayers() {
    if (typeof userList === 'undefined') return;

    const query = document.getElementById('payer-search').value.toLowerCase().trim();
    const resultsDiv = document.getElementById('payer-results');

    // [수정] 검색어가 없어도 전체 목록 표시
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
    document.getElementById('payer-search').value = userName;
    document.getElementById('payerId').value = userId;
    document.getElementById('payer-results').style.display = 'none';
}

// 공통 유효성 검사 함수
function validateForm() {
    const storeName = document.getElementById('store-name').value.trim();
    if (!storeName) {
        MessageUtil.alert(MESSAGES.storeName);
        return false;
    }

    if (selectedParticipants.size === 0) {
        MessageUtil.alert(MESSAGES.participants);
        return false;
    }
    const amountInputs = document.querySelectorAll('.amount-input-group input[type="number"]');
    for (const input of amountInputs) {
        // 값이 비어있거나 0 이하인 경우 체크
        if (!input.value || parseInt(input.value) <= 0) {
            MessageUtil.alert(MESSAGES.amount); // "참석자 금액은 0원 이상이어야 합니다."
            return false;
        }
    }

    const payerValue = document.getElementById('payerId').value;
    if (!payerValue) {
        MessageUtil.alert(MESSAGES.payerId);
        return false;
    }

    return true;
}

// [수정] 수정 확인
function confirmUpdate() {
    if (!validateForm()) return; // 유효성 검사 실패 시 중단

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
    if (!validateForm()) return; // 유효성 검사 실패 시 중단

    MessageUtil.confirmed(
        '내역 등록',
        function() {
            const form = document.querySelector('.entry-form');
            form.action = '/lunch/register.do'; // action 변경 (수정화면에서 신규등록)
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