(function (window, $) {

    // 페이지 클릭 시 호출될 함수 (전역으로 노출)
    window.goEnterPage = function (pageNo) {
        // 현재 URL 기준으로 page 파라미터만 교체
        const url    = new URL(window.location.href);
        const params = url.searchParams;

        params.set('page', pageNo);  // page=… 세팅
        url.search = params.toString();

        window.location.href = url.toString();
    };

    // DOM 준비되면 페이징 그리기
    $(function () {
        const info = window.ENTER_PAGING || {
            totalCount : 0,
            page       : 1,
            totalPages : 1
        };

        const paging = {
            totalCount : info.totalCount,
            pageNo     : info.page,
            startPageNo: 1,
            endPageNo  : info.totalPages,
            prevPageNo : (info.page > 1) ? info.page - 1 : 1,
            nextPageNo : (info.page < info.totalPages) ? info.page + 1 : info.totalPages,
            finalPageNo: info.totalPages
        };

        const $pagingArea = $('#paging-area');

        if (paging.totalCount === 0) {
            setEmptyPagination($pagingArea);        // 공통 모듈
        } else {
            setPagination(paging, $pagingArea, 'goEnterPage'); // 공통 모듈
        }
    });

})(window, jQuery);
