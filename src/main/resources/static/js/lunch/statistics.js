/*
 * 파일 경로: src/main/resources/static/js/lunch/statistics.js
 * 설명: 점심/커피 통계 페이지 전용 스크립트 (Chart.js 및 UI 로직)
 */

// --- 전역 변수 및 포맷터 선언 ---
const formatCurrency = (value) => new Intl.NumberFormat('ko-KR').format(value) + '원';

// 차트 색상 상수
const CHART_COLORS = {
    blueDark: 'rgba(59, 130, 246, 0.7)',
    blueLight: 'rgba(147, 197, 253, 0.7)',
    blueBorderDark: 'rgba(59, 130, 246, 1)',
    blueBorderLight: 'rgba(147, 197, 253, 1)',
    red: 'rgba(255, 99, 132, 0.7)',
    redBorder: 'rgba(255, 99, 132, 1)'
};

// --- 날짜 UI 업데이트 함수 (HTML에서 onchange 등으로 호출됨) ---
function updateDateRange(dateStr) {
    if (!dateStr) return;

    const endDate = new Date(dateStr + "-25");
    const startDate = new Date(endDate);
    startDate.setMonth(startDate.getMonth() - 1);
    startDate.setDate(26);

    const startYear = startDate.getFullYear();
    const startMonth = startDate.getMonth() + 1;
    const startDay = startDate.getDate();

    const endMonth = endDate.getMonth() + 1;
    const endDay = endDate.getDate();

    const text = `${startYear}년 ${startMonth}월 ${startDay}일 ~ ${endMonth}월 ${endDay}일`;
    const textSpan = document.getElementById('date-range-text');
    if (textSpan) textSpan.innerText = text;
}

// --- DOMContentLoaded 이벤트: 문서 로드 후 실행 ---
document.addEventListener("DOMContentLoaded", function() {

    // 초기 날짜 텍스트 세팅
    const input = document.getElementById('searchMonthInput');
    if(input && input.value) {
        updateDateRange(input.value);
    }

    // 데이터 유효성 검사 (HTML에서 넘어온 데이터 확인)
    if (typeof summaryList === 'undefined' || typeof lunchList === 'undefined') {
        console.error("데이터가 로드되지 않았습니다.");
        return;
    }

    // Chart 1: 사용자별 부담금/결제금 ---
    if (summaryList.length > 0) {
        const ctxUser = document.getElementById('userSummaryChart');
        if (ctxUser) {
            const userLabels = summaryList.map(s => s.userName);
            const owed = summaryList.map(s => s.totalOwed);
            const paid = summaryList.map(s => s.totalPaid);

            new Chart(ctxUser.getContext('2d'), {
                type: 'bar',
                data: {
                    labels: userLabels,
                    datasets: [
                        {
                            label: '본인이 먹은 금액',
                            data: owed,
                            backgroundColor: CHART_COLORS.blueLight,
                            borderColor: CHART_COLORS.blueBorderLight,
                            borderWidth: 1
                        },
                        {
                            label: '본인이 결제한 금액',
                            data: paid,
                            backgroundColor: CHART_COLORS.blueDark,
                            borderColor: CHART_COLORS.blueBorderDark,
                            borderWidth: 1
                        }
                    ]
                },
                options: {
                    responsive: true,
                    scales: { y: { beginAtZero: true, ticks: { callback: v => formatCurrency(v) } } },
                    plugins: { tooltip: { callbacks: { label: ctx => `${ctx.dataset.label}: ${formatCurrency(ctx.raw)}` } } }
                }
            });
        }
    }

    // --- Chart 2: 일별 지출 추이 ---
    if (lunchList.length > 0) {
        const ctxDaily = document.getElementById('dailyExpenseChart');
        if (ctxDaily) {
            // 시간순 정렬 (오래된 순)
            const list = [...lunchList].reverse();
            const labels = list.map(i => i.date.substring(5));
            const amounts = list.map(i => i.totalAmount);

            new Chart(ctxDaily.getContext('2d'), {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: '지출 내역',
                        data: amounts,
                        fill: true,
                        backgroundColor: 'rgba(59, 130, 246, 0.2)',
                        borderColor: CHART_COLORS.blueBorderDark,
                        borderWidth: 2,
                        pointRadius: 4,
                        pointBackgroundColor: CHART_COLORS.blueBorderDark,
                        pointBorderColor: '#fff',
                        pointBorderWidth: 2,
                        tension: 0.1
                    }]
                },
                options: {
                    responsive: true,
                    scales: { y: { beginAtZero: true, ticks: { callback: v => formatCurrency(v) } } },
                    plugins: { tooltip: { callbacks: { label: ctx => `금액: ${formatCurrency(ctx.raw)}` } } }
                }
            });
        }
    }

    // --- Chart 3: 일자별 점심/커피 구분 ---
    if (lunchList.length > 0) {
        const ctxType = document.getElementById('dailyTypeChart');
        if (ctxType) {
            const map = new Map();
            const sorted = [...lunchList].reverse();

            for (const item of sorted) {
                const date = item.date.substring(5);
                if (!map.has(date)) map.set(date, { lunch: 0, coffee: 0 });

                if (item.type === '점심') map.get(date).lunch += item.totalAmount;
                else if (item.type === '커피') map.get(date).coffee += item.totalAmount;
            }

            const labels = Array.from(map.keys());
            const lunch = labels.map(d => map.get(d).lunch);
            const coffee = labels.map(d => map.get(d).coffee);

            new Chart(ctxType.getContext('2d'), {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [
                        { label: '점심', data: lunch, backgroundColor: CHART_COLORS.blueDark },
                        { label: '커피', data: coffee, backgroundColor: CHART_COLORS.blueLight }
                    ]
                },
                options: {
                    responsive: true,
                    scales: { y: { beginAtZero: true, ticks: { callback: v => formatCurrency(v) } } }
                }
            });
        }
    }

    // --- Chart 4: 사용자별 송금/수금 총액 ---
    if (summaryList.length > 0) {
        const ctxBalance = document.getElementById('balanceChart');
        if (ctxBalance) {
            const labels = summaryList.map(u => u.userName);
            const values = summaryList.map(u => Math.abs(u.balance));
            const bgColors = summaryList.map(u => u.balance >= 0 ? CHART_COLORS.blueDark : CHART_COLORS.red);
            const borderColors = summaryList.map(u => u.balance >= 0 ? CHART_COLORS.blueBorderDark : CHART_COLORS.redBorder);

            new Chart(ctxBalance.getContext('2d'), {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: '송금/수금 총액',
                        data: values,
                        backgroundColor: bgColors,
                        borderColor: borderColors,
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    scales: { y: { beginAtZero: true, ticks: { callback: v => formatCurrency(v) } } },
                    plugins: {
                        legend: { display: false },
                        tooltip: {
                            callbacks: {
                                label: ctx => {
                                    const balance = summaryList[ctx.dataIndex].balance;
                                    const title = balance >= 0 ? "받을 금액" : "송금할 금액";
                                    return `${title}: ${formatCurrency(ctx.raw)}`;
                                }
                            }
                        }
                    }
                }
            });
        }
    }
});