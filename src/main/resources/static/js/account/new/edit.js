const userId = /*[[${user.userId}]]*/ '';

document.getElementById('btn-delete').addEventListener('click', function () {
    openDeleteModal(userId);
});

function openDeleteModal(userId) {
    Swal.fire({
        title: '계정 삭제',
        text: userId + ' 계정을 삭제하시겠습니까?',
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