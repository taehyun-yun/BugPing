<template>
    <div class="app-container">
        <MainSidebar />
        <div class="main-content">
            <MainHeader />
            <div class="calendar-and-schedule">
                <div class="calendar-container">
                    <FullCalendar ref="calendarRef" :options="calendarOptions" />
                </div>
                <div class="schedule-list">
                    <h3>일정 목록</h3>
                    <div v-for="(item, index) in scheduleItems" :key="index" class="schedule-item"
                        @click="editScheduleItem(item)">
                        <div class="schedule-color" :style="{ backgroundColor: item.color }"></div>
                        <div class="schedule-details">
                            <div class="schedule-type">{{ item.type }}</div>
                            <div class="schedule-time">{{ item.time }}</div>
                            <div class="schedule-duration">{{ item.duration }}</div>
                            <div class="schedule-creator">{{ item.creator }}</div>
                        </div>
                    </div>
                    <h3>근무 변경 요청 목록</h3>
                    <div v-for="(item, index) in changeRequests" :key="index" class="schedule-item">
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
import axios from 'axios';
import { ref, onMounted } from 'vue';
import FullCalendar from '@fullcalendar/vue3'; // FullCalendar 
import dayGridPlugin from '@fullcalendar/daygrid'; // 일별 그리드 
import interactionPlugin from '@fullcalendar/interaction'; // 상호작용  (드래그 앤 드롭, 클릭 등).
import timeGridPlugin from '@fullcalendar/timegrid'; // 시간별 
import 'bootstrap/dist/css/bootstrap.min.css'; // Bootstrap 스타일시트를 가져옵니다.
import MainSidebar from '../components/MainSidebar.vue';
import MainHeader from '../components/MainHeader.vue';

// 일정 목록 및 근무 변경 요청을 관리하는 상태 정의
const scheduleItems = ref([]);
const changeRequests = ref([]);

// 캘린더에 대한 참조 설정
const calendarRef = ref(null);

// 캘린더 옵션 객체를 정의
const calendarOptions = ref({
    plugins: [dayGridPlugin, interactionPlugin, timeGridPlugin], // 사용할 플러그인
    initialView: 'dayGridMonth', // 캘린더의 기본 뷰 설정
    themeSystem: 'bootstrap', // 부트스트랩을 테마로 사용
    editable: true, // 이벤트 드래그 및 수정 기능
    selectable: true, // 캘린더에서 날짜 선택 기능
    headerToolbar: {
        left: 'prev,next today', // 왼쪽에는 이전, 다음, 오늘 버튼 표시
        center: 'title', // 중앙에는 타이틀 표시
        right: 'dayGridMonth,timeGridWeek,timeGridDay' // 오른쪽에는 월별, 주별, 일별 버튼 표시
    },
    events: [],
    dateClick: (info) => {
        const newEventTitle = prompt('새로운 일정 입력'); // 새 이벤트 제목을 입력 받음
        if (newEventTitle) {
            const newEventStartTime = prompt('시작 시간을 입력하세요 (HH:MM 형식)', '09:00'); // 시작 시간 입력
            if (newEventStartTime) {
                const newEventEndTime = prompt('종료 시간을 입력하세요 (HH:MM 형식)', '10:00'); // 종료 시간 입력
                if (newEventEndTime) {
                    if (calendarRef.value) {
                    const calendarApi = calendarRef.value.getApi(); // FullCalendar API 인스턴스 가져오기
                    calendarApi.addEvent({
                        id: Date.now().toString(), // 고유한 ID로 이벤트를 추가
                        title: newEventTitle, // 입력한 제목으로 이벤트 생성
                        start: `${info.dateStr}T${newEventStartTime}`, // 시작 날짜와 시간
                        end: `${info.dateStr}T${newEventEndTime}`, // 종료 날짜와 시간
                        allDay: false // 특정 시간 이벤트로 설정
                    });
                } else {
                    console.error("캘린더 인스턴스 참조 못함");
                }
                }
            }
        }
    }
});

// 일정 편집 함수 (임시로 콘솔에 출력만 하도록 구현)
const editScheduleItem = (item) => {
    console.log('일정 수정:', item);
};

// 서버에서 데이터 가져오기
const fetchSchedulesAndRequests = async () => {
    try {
        const response = await axios.get('/api/scheduleRequests'); // 서버에서 일정 및 근무 변경 요청 데이터를 가져옵니다.
        console.log(response.data);
        const { schedules, changes } = response.data;

        scheduleItems.value = schedules || [];
        changeRequests.value = changes || [];

        if (calendarRef.value) {
            const calendarApi = calendarRef.value.getApi();
            schedules.forEach((schedule) => {
                calendarApi.addEvent({
                    id: schedule.id,
                    title: schedule.type,
                    start: schedule.start,
                    end: schedule.end,
                    allDay: false,
                });
            });
        } else {
            console.error("캘린더 인스턴스를 참조하지 못했습니다.");
        }
    }
    catch (error) {
        console.error('일정 데이터를 가져오는 중 오류 발생', error);
    }
};

onMounted(() => {
    fetchSchedulesAndRequests();
});
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
    /* 헤더 높이를 고려하여 조정 */
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

/* 요일의 언더라인 제거 */
:deep(.fc-col-header-cell a) {
    text-decoration: none !important;
    /* 요일 헤더에서 언더라인 제거 */
}

/* 날짜의 언더라인 제거 */
:deep(.fc-daygrid-day-number) {
    text-decoration: none !important;
    /* 날짜에서 언더라인 제거 */
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
