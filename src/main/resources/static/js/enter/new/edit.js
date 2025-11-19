// 게스트/직원 섹션 토글 (create 화면이랑 동일)
function toggleTypeSection() {
    const type = document.querySelector('input[name="type"]:checked')?.value;
    const guest = document.getElementById('guest-section');
    const emp = document.getElementById('emp-section');

    if (type === 'GUEST') {
        guest.classList.remove('hidden');
        emp.classList.add('hidden');
    } else {
        guest.classList.add('hidden');
        emp.classList.remove('hidden');
    }
}

function setMode(mode) {
    const form   = document.querySelector('.profile-form');
    const inputs = form.querySelectorAll('input, select');

    const isView = (mode === 'view');

    form.classList.toggle('view-mode', isView);
    form.classList.toggle('edit-mode', !isView);

    inputs.forEach(el => {
        if (el.type === 'hidden') return;

        //  항상 잠겨 있어야 하는 필드
        if (el.classList.contains('emp-fixed')) {
            if (el.tagName === 'INPUT') {
                el.readOnly = true;
            } else if (el.tagName === 'SELECT') {
                el.disabled = true;
            }
            return;
        }

        //  나머지는 모드에 따라 토글
        if (el.tagName === 'INPUT') {
            if (el.type === 'radio') {
                el.disabled = true;
            } else if (el.type !== 'hidden') {
                el.readOnly = isView;
            }
        } else if (el.tagName === 'SELECT') {
            el.disabled = isView;
        }
    });

    document.getElementById('editBtn').classList.toggle('hidden', !isView);
    document.getElementById('saveBtn').classList.toggle('hidden', isView);
    document.getElementById('cancelBtn').classList.toggle('hidden', isView);
    document.getElementById('deleteBtn').classList.toggle('hidden', !isView);
    document.getElementById('listBtn').classList.toggle('hidden', !isView);
}

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('input[name="type"]').forEach(radio => {
        radio.addEventListener('change', toggleTypeSection);
    });
    toggleTypeSection();

    //  서버에서 넘겨준 mode 사용 (없으면 기본 view)
    const initialMode = /*[[${mode}]]*/ 'view';
    setMode(initialMode);

    document.getElementById('editBtn').addEventListener('click', () => {
        setMode('edit');
    });
});

//팝업

const enterId = /*[[${enter.enterId}]]*/ '';

document.getElementById('deleteBtn').addEventListener('click', function () {
    openDeleteModal(enterId);
});

function openDeleteModal(enterId) {
    Swal.fire({
        title: '출입 삭제',
        text: enterId + ' 출입을 삭제하시겠습니까?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '삭제',
        cancelButtonText: '취소',
        allowOutsideClick: false
    }).then((result) => {
        if (result.isConfirmed) {
            document.getElementById('deleteForm').submit();
        }
    });
}