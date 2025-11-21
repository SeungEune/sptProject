(function (window, $) {
    // 1) 페이지 이동 함수 (전역으로 노출)
    //    => paginationModule 에서 onclick="goAccountPage('2')" 이렇게 쓰고 있으니까
    //       window에 붙여줘야 한다.

    // 기존에 있던 페이지 인풋 제거하고 새 페이지 인풋 만들어서 요청
    function goAccountPage(pageNo) {
        const $form = document.getElementById('search-form');
        if (!$form) return;

        const $old = $form.querySelector('input[name="page"]');
        if ($old) $old.remove();

        const $new = document.createElement('input');
        $new.type  = 'hidden';
        $new.name  = 'page';
        $new.value = pageNo;
        $form.appendChild($new);

        $form.submit();
    }
    window.goAccountPage = goAccountPage;  // 전역 등록

    // 2) 페이지네이션 렌더링

    $(function () {
        // Thymeleaf 쪽에서 미리 넣어준 전역 객체
        const cfg = window.ACCOUNT_PAGING || {
            totalCount: 0,
            page: 1,
            totalPages: 1
        };

        const totalCount = cfg.totalCount;
        const page       = cfg.page;
        const totalPages = cfg.totalPages;

        const paging = {
            totalCount : totalCount,
            pageNo     : page,
            startPageNo: 1,
            endPageNo  : totalPages,
            prevPageNo : (page > 1) ? page - 1 : 1,
            nextPageNo : (page < totalPages) ? page + 1 : totalPages,
            finalPageNo: totalPages
        };

        const $pagingArea = $('#paging-area');

        if (!$pagingArea.length) return;

        if (paging.totalCount === 0) {
            setEmptyPagination($pagingArea);
        } else {
            setPagination(paging, $pagingArea, 'goAccountPage');
        }
    });

})(window, jQuery);
