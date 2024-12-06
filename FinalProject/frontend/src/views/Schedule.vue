<template>
    <div class="app-container">
        <div class="main-content">
            <div class="calendar-and-schedule">
                <div class="calendar-container">
                    <FullCalendar :options="calendarOptions" />
                </div>
                <div class="schedule-list">
                    <h3>일정 목록</h3>
                    <div v-for="(item, index) in scheduleItems" :key="index" class="schedule-item" @click="editScheduleItem(item)">
                        <div class="schedule-color" :style="{ backgroundColor: item.color }"></div>
                        <div class="schedule-details">
                            <div class="schedule-type">{{ item.type }}</div>
                            <div class="schedule-time">{{ item.time }}</div>
                            <div class="schedule-duration">{{ item.duration }}</div>
                            <div class="schedule-creator">{{ item.creator }}</div>
                        </div>
                    </div>
                    <h3>근무 요청 목록</h3>
                    <div v-for="(item, index) in workRequests" :key="index" class="schedule-item">
                        <div class="schedule-color" :style="{ backgroundColor: item.color }"></div>
                        <div class="schedule-details">
                            <div class="schedule-type">{{ item.type }}</div>
                            <div class="schedule-time">{{ item.time }}</div>
                            <div class="schedule-creator">{{ item.requestor }}</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import FullCalendar from '@fullcalendar/vue3'; // FullCalendar 
import dayGridPlugin from '@fullcalendar/daygrid'; // 일별 그리드 
import interactionPlugin from '@fullcalendar/interaction'; // 상호작용  (드래그 안드 듣, 클릭 등).
import timeGridPlugin from '@fullcalendar/timegrid'; // 시간별 
import 'bootstrap/dist/css/bootstrap.min.css'; // Bootstrap 스타일시트를 가져옵니다.
import { ref, onMounted } from 'vue';

// 예시 데이터 및 상태 정의
const scheduleItems = ref([
    {
        id: '1',
        type: '중요한 일정',
        time: '2024-12-15 10:00 - 12:00',
        duration: '2시간 0분',
        creator: '진식',
        color: '#006666'
    },
    {
        id: '2',
        type: '한요중 일정',
        time: '2024-12-17 15:00 - 17:00',
        duration: '2시간 0분',
        creator: '문화',
        color: '#6666ff'
    }
]);

const workRequests = ref([
    {
        id: '3',
        type: '야간 근무 요청',
        time: '2024-12-18 22:00 - 06:00',
        requestor: '이민진',
        color: '#ff9900'
    },
    {
        id: '4',
        type: '오후 근무 요청',
        time: '2024-12-20 13:00 - 18:00',
        requestor: '황영우',
        color: '#ff6600'
    }
]);

// 카드러 옵션 객체를 정의합니다.
const calendarOptions = {
    plugins: [dayGridPlugin, interactionPlugin, timeGridPlugin], // 사용할 플러그인
    initialView: 'dayGridMonth', // 카드러의 기본 분 설정 (주별 시간 그리드)
    themeSystem: 'bootstrap', // 부트스트러프를 테마 사용
    editable: true, // 이벤트 드래그 및 수정 기능
    selectable: true, // 카드러에서 날짜를 선택 기능
    headerToolbar: {
        // 카드러의 상단 툴바
        left: 'prev,next today', // 왼쪽에는 이전, 다음, 오늘 버튼 표시
        center: 'title', // 중앙에는 타이틀 표시
        right: 'dayGridMonth,timeGridWeek,timeGridDay' // 오른쪽에는 월별, 주별, 일별 버튼 표시
    },
    views: {
        // 각 분의 버튼 텍스트 설정
        dayGridMonth: { // 월별 분 설정
            buttonText: '월별' // 버튼에 '월별' 
        },
        timeGridWeek: { // 주별 분 설정
            buttonText: '주별' // 버튼에 '주별' 
        },
        timeGridDay: { // 일별 분 설정
            buttonText: '일별' // 버튼에 '일별'
        }
    },
    dateClick: (info) => {
        // 날짜를 누르면 이벤트 추가
        const newEventTitle = prompt('새로운 일정 입력'); // 새 이벤트 제목을 입력 받음
        if (newEventTitle) {
            const newEventStartTime = prompt('시작 시간을 입력하세요 (HH:MM 형식)', '09:00'); // 시작 시간 입력
            if (newEventStartTime) {
                const newEventEndTime = prompt('종료 시간을 입력하세요 (HH:MM 형식)', '10:00'); // 종료 시간 입력
                if (newEventEndTime) {
                    const calendarApi = calendarRef.value.getApi(); // FullCalendar API 인스턴스 가져오기
                    calendarApi.addEvent({
                        id: Date.now().toString(), // 고유한 ID로 이벤트를 추가
                        title: newEventTitle, // 입력한 제목으로 이벤트 생성
                        start: `${info.dateStr}T${newEventStartTime}`, // 시작 날짜와 시간
                        end: `${info.dateStr}T${newEventEndTime}`, // 종료 날짜와 시간
                        allDay: false // 특정 시간 이벤트로 설정
                    });
                }
            }
        }
    }
};
</script>

<style scoped>
.app-container {
    display: flex;
    height: 100vh;
    overflow: hidden;
}

.main-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 20px;
    overflow-y: auto;
}

.calendar-and-schedule {
    display: flex;
    gap: 20px;
    height: calc(100vh - 100px);
    /* 허더 높이를 고려하여 조정 */
}

.calendar-container {
    flex: 2;
    min-width: 0;
    /* flexbox 내에서 축소되지 않도록 설정 */
}

.schedule-list {
    flex: 1;
    overflow-y: auto;
    padding: 10px;
    background-color: #f5f5f5;
    border-radius: 8px;
}

.schedule-item {
    display: flex;
    align-items: center;
    padding: 10px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    margin-bottom: 10px;
}

.schedule-color {
    width: 16px;
    height: 16px;
    border-radius: 50%;
    margin-right: 15px;
}

.schedule-details {
    display: flex;
    flex-direction: column;
    flex: 1;
}

.schedule-type {
    font-weight: bold;
    font-size: 14px;
    color: #333;
}

.schedule-time,
.schedule-duration,
.schedule-creator {
    font-size: 12px;
    color: #555;
    margin-top: 2px;
}

/* FullCalendar 스타일 오버라이드 */
:deep(.fc) {
    height: 100%;
}

:deep(.fc-header-toolbar) {
    margin-bottom: 0.5em !important;
}

:deep(.fc-view-harness) {
    height: calc(100% - 50px) !important;
    /* 툴바 높이를 고려하여 조정 */
}
/* 기존 스타일은 그대로 유지하며 필요한 부분만 추가 */

/* 요일의 어드라인 제거 */
:deep(.fc-col-header-cell a) {
    text-decoration: none !important; /* 요일 헤더에서 어드라인 제거 */
}

/* 날짜의 어드라인 제거 */
:deep(.fc-daygrid-day-number) {
    text-decoration: none !important; /* 날짜에서 어드라인 제거 */
}

/* 반응형 디자인 */
@media (max-width: 768px) {
    .calendar-and-schedule {
        flex-direction: column;
    }

    .calendar-container,
    .schedule-list {
        flex: none;
        width: 100%;
        height: 50vh;
    }
}
</style>
