(function (window, $) {

    // ───────────────────────────────
    // 1) 페이지 이동 함수 (전역으로 노출)
    //    => paginationModule 에서 onclick="goAccountPage('2')" 이렇게 쓰고 있으니까
    //       window에 붙여줘야 한다.
    // ───────────────────────────────
    function goAccountPage(pageNo) {
        const form = document.getElementById('search-form');
        if (!form) return;

        const old = form.querySelector('input[name="page"]');
        if (old) old.remove();

        const hidden = document.createElement('input');
        hidden.type  = 'hidden';
        hidden.name  = 'page';
        hidden.value = pageNo;
        form.appendChild(hidden);

        form.submit();
    }
    window.goAccountPage = goAccountPage;  // ★ 전역 등록


    // ───────────────────────────────
    // 2) 페이지네이션 렌더링
    // ───────────────────────────────
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
