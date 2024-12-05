<template>
    <div class="app-container">
        <MainSidebar />
        <div class="main-content">
            <MainHeader />
            <div class="calendar-and-schedule">
                <div class="calendar-container">
                    <FullCalendar :options="calendarOptions" />
                </div>
                <div class="schedule-list">
                    <div v-for="(item, index) in scheduleItems" :key="index" class="schedule-item" @click="editScheduleItem(item)">
                        <div class="schedule-color" :style="{ backgroundColor: item.color }"></div>
                        <div class="schedule-details">
                            <div class="schedule-type">{{ item.type }}</div>
                            <div class="schedule-time">{{ item.time }}</div>
                            <div class="schedule-duration">{{ item.duration }}</div>
                            <div class="schedule-creator">{{ item.creator }}</div>
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
import interactionPlugin from '@fullcalendar/interaction'; // 상호작용  (드래그 앤 드롭, 클릭 등).
import timeGridPlugin from '@fullcalendar/timegrid'; // 시간별 
import 'bootstrap/dist/css/bootstrap.min.css'; // Bootstrap 스타일시트를 가져옵니다.
import MainSidebar from '../components/MainSidebar.vue';
import MainHeader from '../components/MainHeader.vue';
import { ref, onMounted } from 'vue';
import axios from 'axios'; // 서버와 통신을 위한 axios 라이브러리 사용

// 예시 데이터 및 상태 정의
const scheduleItems = ref([]); // 오른쪽 리스트에서 표시할 일정 데이터
const calendarRef = ref(null); // FullCalendar 인스턴스를 참조하기 위한 ref

// 캘린더 옵션 객체를 정의합니다.
const calendarOptions = {
    plugins: [dayGridPlugin, interactionPlugin, timeGridPlugin], // 사용할 플러그인
    initialView: 'dayGridMonth', // 캘린더의 기본 뷰 설정 (주별 시간 그리드)
    themeSystem: 'bootstrap', // 부트스트랩을 테마 사용
    editable: true, // 이벤트 드래그 및 수정 기능
    selectable: true, // 캘린더에서 날짜를 선택 기능
    headerToolbar: {
        // 캘린더의 상단 툴바
        left: 'prev,next today', // 왼쪽에는 이전, 다음, 오늘 버튼 표시
        center: 'title', // 중앙에는 타이틀 표시
        right: 'dayGridMonth,timeGridWeek,timeGridDay' // 오른쪽에는 월별, 주별, 일별 버튼 표시
    },
    views: {
        // 각 뷰의 버튼 텍스트 설정
        dayGridMonth: { // 월별 뷰 설정
            buttonText: '월별' // 버튼에 '월별' 
        },
        timeGridWeek: { // 주별 뷰 설정
            buttonText: '주별' // 버튼에 '주별' 
        },
        timeGridDay: { // 일별 뷰 설정
            buttonText: '일별' // 버튼에 '일별'
        }
    },
    eventAdd: (info) => {
        addScheduleItem(info.event);
    },
    eventChange: (info) => {
        updateScheduleItem(info.event);
    },
    eventRemove: (info) => {
        removeScheduleItem(info.event.id);
    },
    dateClick: (info) => {
        const newEventTitle = prompt('새로운 일정 제목을 입력하세요:');
        if (newEventTitle) {
            const newEventStartTime = prompt('시작 시간을 입력하세요 (형식: HH:MM):', '09:00'); // 시작 시간 입력
            if (newEventStartTime) {
                const newEventEndTime = prompt('종료 시간을 입력하세요 (형식: HH:MM):', '10:00'); // 종료 시간 입력
                if (newEventEndTime) {
                    const newCreator = prompt('생성자를 입력하세요:', ''); // 생성자 입력
                    const calendarApi = calendarRef.value.getApi(); // FullCalendar API 인스턴스 가져오기
                    calendarApi.addEvent({
                        id: Date.now().toString(), // 고유한 ID로 이벤트를 추가
                        title: newEventTitle, // 입력한 제목으로 이벤트 생성
                        start: `${info.dateStr}T${newEventStartTime}`, // 시작 날짜와 시간
                        end: `${info.dateStr}T${newEventEndTime}`, // 종료 날짜와 시간
                        allDay: false, // 특정 시간 이벤트로 설정
                        extendedProps: {
                            creator: newCreator // 생성자를 extendedProps에 저장
                        }
                    });
                }
            }
        }
    }
};

// 서버에서 이벤트 데이터를 가져오는 함수
const fetchEventsFromServer = async () => {
    try {
        const response = await axios.get('YOUR_SERVER_API_ENDPOINT'); // 서버 API 호출
        const events = response.data; // 서버에서 받아온 이벤트 데이터

        // 받아온 이벤트 데이터를 캘린더와 리스트에 추가
        const calendarApi = calendarRef.value.getApi(); // FullCalendar API 인스턴스 가져오기
        events.forEach(event => {
            calendarApi.addEvent({
                id: event.id,
                title: event.title,
                start: event.start,
                end: event.end,
                allDay: event.allDay,
                extendedProps: {
                    creator: event.creator
                }
            });
            addScheduleItem(event);
        });
    } catch (error) {
        console.error('이벤트 데이터를 가져오는 중 오류가 발생했습니다:', error);
    }
};

// 컴포넌트가 마운트될 때 서버에서 이벤트 데이터를 가져옴
onMounted(() => {
    fetchEventsFromServer();
});

// 이벤트를 오른쪽 리스트에 추가하는 함수
const addScheduleItem = (event) => {
    scheduleItems.value.push({
        id: event.id,
        type: event.title,
        time: event.allDay ? '하루 종일' : `${new Date(event.start).getHours()}:${String(new Date(event.start).getMinutes()).padStart(2, '0')} - ${event.end ? new Date(event.end).getHours() + ':' + String(new Date(event.end).getMinutes()).padStart(2, '0') : '미정'}`,
        duration: calculateDuration(event.start, event.end), // 시작 시간과 종료 시간을 이용하여 소요 시간 계산
        creator: event.extendedProps?.creator || '알 수 없음', // 입력된 생성자가 없으면 '알 수 없음'으로 기본값 설정
        color: '#006666' // 기본 색상 설정
    });
};

// 이벤트를 오른쪽 리스트에 업데이트하는 함수
const updateScheduleItem = (event) => {
    const item = scheduleItems.value.find(item => item.id === event.id);
    if (item) {
        item.type = event.title;
        item.time = event.allDay ? '하루 종일' : `${new Date(event.start).getHours()}:${String(new Date(event.start).getMinutes()).padStart(2, '0')} - ${event.end ? new Date(event.end).getHours() + ':' + String(new Date(event.end).getMinutes()).padStart(2, '0') : '미정'}`;
        item.duration = calculateDuration(event.start, event.end); // 소요 시간 업데이트
        item.creator = event.extendedProps?.creator || '알 수 없음';
    }
};

// 시작 시간과 종료 시간의 차이를 계산하는 함수
const calculateDuration = (start, end) => {
    if (!start || !end) return '미정'; // 종료 시간이 없는 경우 미정으로 표시

    const startTime = new Date(start);
    const endTime = new Date(end);
    const diffMs = endTime - startTime; // 밀리초 단위 차이 계산

    const diffHours = Math.floor(diffMs / (1000 * 60 * 60)); // 시간 계산
    const diffMinutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60)); // 분 계산

    return `${diffHours}시간 ${diffMinutes}분`;
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
    text-decoration: none !important; /* 요일 헤더에서 언더라인 제거 */
}

/* 날짜의 언더라인 제거 */
:deep(.fc-daygrid-day-number) {
    text-decoration: none !important; /* 날짜에서 언더라인 제거 */
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