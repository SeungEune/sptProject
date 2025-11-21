document.addEventListener('DOMContentLoaded', () => {
    const btnDelete = document.getElementById('btn-delete');
    if (!btnDelete) return;

    btnDelete.addEventListener('click', () => {
        const userId = btnDelete.dataset.userId;
        openDeleteModal(userId);
    });

    function openDeleteModal(userId) {
        MessageUtil.confirm(userId+" 정말 삭제할까요?", function(confirmed) {
            if (confirmed) {
                // 확인 눌렀을 때 하고 싶은 일
                document.getElementById('deleteForm').submit();
            } else {
                // 취소 눌렀을 때 하고 싶은 일
                console.log("삭제 취소");
            }
        });


    }
});
