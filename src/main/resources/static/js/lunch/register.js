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
    // 1. 유효성 검사 (빈칸 체크)
    const form = document.querySelector('.entry-form');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    // mainLayout 등에 이미 SweetAlert2가 로드되어 있어야 함
    Swal.fire({
        title: '내역 등록',
        text: "현재 내용으로 새로 등록하시겠습니까?",
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: '등록',
        cancelButtonText: '취소',
        buttonsStyling: false,
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

// --- DOMContentLoaded 이벤트: 문서 로드 후 실행 ---
document.addEventListener('DOMContentLoaded', function() {
    // 사이드바 토글 로직
    const menuToggle = document.getElementById('menu-toggle');
    const closeMenu = document.getElementById('close-menu');
    const sidebar = document.getElementById('sidebar');
    const overlay = document.getElementById('overlay');

    if (menuToggle) {
        function openSidebar() {
            if (sidebar) sidebar.classList.add('active');
            if (overlay) overlay.classList.add('active');
            document.body.style.overflow = 'hidden';
        }

        function closeSidebarFunc() {
            if (sidebar) sidebar.classList.remove('active');
            if (overlay) overlay.classList.remove('active');
            document.body.style.overflow = '';
        }

        menuToggle.addEventListener('click', openSidebar);
        if (closeMenu) closeMenu.addEventListener('click', closeSidebarFunc);
        if (overlay) overlay.addEventListener('click', closeSidebarFunc);
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