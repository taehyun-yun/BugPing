<template>
    <div class="calendar-and-schedule">
        <div class="calendar-container">
            <FullCalendar ref="calendarRef" :options="calendarOptions" />
        </div>

        <!-- 모달 -->
        <div v-if="isModalVisible" class="modal-overlay" @click="closeModal">
            <div class="modal-content" @click.stop>
                <h3>{{ modalData.title }}</h3>
                <p><strong>설명:</strong> {{ modalData.description }}</p>
                <p><strong>시작:</strong> {{ modalData.start }}</p>
                <p><strong>종료:</strong> {{ modalData.end }}</p>
                <button @click="closeModal">닫기</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import FullCalendar from '@fullcalendar/vue3';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import timeGridPlugin from '@fullcalendar/timegrid';
import axios from 'axios';
import { format } from 'date-fns';

// 모달 관련 데이터
const isModalVisible = ref(false);
const modalData = ref({
    title: '',
    description: '',
    start: '',
    end: '',
});

// 모달 열기
const openModal = (event) => {
    modalData.value = {
        title: event.title || '제목 없음',
        description: event.extendedProps?.description || '설명 없음',
        start: format(new Date(event.start), 'yyyy-MM-dd HH:mm'),
        end: event.end ? format(new Date(event.end), 'yyyy-MM-dd HH:mm') : '종료 시간이 없습니다.',
    };
    isModalVisible.value = true;
};


// 모달 닫기
const closeModal = () => {
    isModalVisible.value = false;
};

// 나의 일정, 회사 일정 보기
const selectedUserId = ref(''); // 사용자 ID
const selectedCompanyId = ref(''); // 회사 ID
const userRole = ref(""); // 사용자 역할 
const isUserView = ref(true); // 초기 상태: 내 근무 보기

const apiKey = 'AIzaSyCcMLoDEakYxNOfXxKIE8JYVIsa8PevUr4';
const holidayCalendarId = 'ko.south_korea%23holiday@group.v.calendar.google.com';
// 버튼 텍스트 동적 설정
const buttonText = computed(() => (isUserView.value ? '회사 근무 보기' : '내 근무 보기'));

// FullCalendar 옵션
const calendarOptions = ref({
    plugins: [dayGridPlugin, interactionPlugin, timeGridPlugin],
    initialView: 'dayGridMonth',
    locale: 'ko', // 한국어
    headerToolbar: computed(() => {
        // role이 employer인 경우 toggleViewButton을 숨김
        return userRole.value === 'employer'
            ? {
                left: 'prev,next today',
                center: 'title',
                right: 'dayGridMonth,timeGridWeek,timeGridDay',
            }
            : {
                left: 'prev,next today',
                center: 'title',
                right: 'dayGridMonth,timeGridWeek,timeGridDay,toggleViewButton',
            };
    }),
    dayCellDidMount: (info) => {
        const day = info.date.getDay(); // 0: 일요일, 6: 토요일
        if (day === 6) {
            info.el.style.backgroundColor = '#E6E6FA'; // 연한 보라색
        } else if (day === 0) {
            info.el.style.backgroundColor = '#F3E5F5'; // 연한 분홍색
        }
    },

    customButtons: {
        toggleViewButton: {
            text: buttonText.value, // 동적으로 텍스트 설정
            click: () => {
                isUserView.value = !isUserView.value;
                if (isUserView.value) {
                    selectedCompanyId.value = '';
                    selectedUserId.value = '';
                } else {
                    selectedUserId.value = '';
                    selectedCompanyId.value = '';
                }
                calendarRef.value.getApi().refetchEvents(); // 일정 새로 로드
            },
        },
    },
    events: async (fetchInfo, successCallback, failureCallback) => {
        try {
            // 서버와 날짜 요청 형식을 맞추기
            const startFormatted = format(new Date(fetchInfo.start), 'yyyy-MM-dd');
            const endFormatted = format(new Date(fetchInfo.end), 'yyyy-MM-dd');

            const params = {
                start: startFormatted,
                end: endFormatted,
            };

            if (selectedCompanyId.value) {
                params.companyId = selectedCompanyId.value;
            }

            const [serverResponse, holidaysResponse] = await Promise.all([
                axios.get('http://localhost:8707/api/calendar', { params }),
                axios.get(`https://www.googleapis.com/calendar/v3/calendars/${holidayCalendarId}/events`, {
                    params: {
                        key: apiKey,
                        timeMin: fetchInfo.start.toISOString(),
                        timeMax: fetchInfo.end.toISOString(),
                        singleEvents: true,
                        orderBy: 'startTime',
                    },
                }),
            ]);

            // 서버 데이터 처리
            const { userId, role, companyId, schedules } = serverResponse.data;
            selectedUserId.value = userId; // userId 설정
            userRole.value = role; // role 설정
            selectedCompanyId.value = companyId; // companyId 설정

            const existingSchedules = schedules.map((event) => ({
                title: event.title,
                start: event.start,
                end: event.end,
                color: event.color || '#3788d8',
                extendedProps: {
                description: event.description, // 서버에서 받은 description
                },
            }));

            // 공휴일 데이터 처리
            const holidaySchedules = holidaysResponse.data.items
            .filter((event) => event.summary !== '섣달 그믐날')     
            .map((event) => ({
                title: event.summary,
                start: event.start.date || event.start.dateTime,
                end: event.end.date || event.end.dateTime,
                color: '#FF0000',
                editable: false, //드래그 앤 드롭 비활성화
            }));

            // 기존 일정 + 공휴일 
            successCallback([...existingSchedules, ...holidaySchedules]);
        } catch (error) {
            console.error('이벤트 데이터를 가져오는 중 오류 발생:', error);
            failureCallback(error);
        }
    },

    editable: true,     // 드래그로 이벤트 수정 가능
    selectable: true,   // 드래그로 영역 선택 가능
    eventColor: '#3788d8',  // 기본 이벤트 색상

    // 제목만 표시
    eventContent: (info) => {
        return {
            html: `<div class="fc-event-title">${info.event.title}</div>`,
        };
    },

    // 이벤트 클릭시 모달 열기
    eventClick: (info) => openModal(info.event),
});

const calendarRef = ref(null);

// 버튼 텍스트 변경 시 FullCalendar 업데이트
watch(buttonText, () => {
    const calendarApi = calendarRef.value.getApi();
    calendarApi.setOption('customButtons', {
        toggleViewButton: {
            text: buttonText.value,
            click: calendarApi.getOption('customButtons').toggleViewButton.click,
        },
    });
});
</script>

<style scoped>
/* 달력 전체 스타일 */
.calendar-and-schedule {
    display: flex;
    flex-direction: column;
    align-items: center;
}

/* 달력 컨테이너 */
.calendar-container {
    width: 100%;
    max-width: 900px;
    margin: 0 auto;
}

/* FullCalendar 이벤트 스타일 */
.fc-event-title {
    font-weight: bold;
    font-size: 1rem;
    color: #333;
}

/* 모달 스타일 */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.modal-content {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    width: 400px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.modal-content h3 {
    margin-top: 0;
    font-size: 1.5rem;
}

.modal-content p {
    margin: 10px 0;
}

.modal-content button {
    display: block;
    margin: 20px auto 0;
    padding: 10px 20px;
    background-color: #3788d8;
    color: #fff;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

.modal-content button:hover {
    background-color: #2c6fb2;
}

.fc-daygrid-day.fc-saturday {
    background-color: #E6E6FA !important;
    /* 토요일 색상 */
}

.fc-daygrid-day.fc-sunday {
    background-color: #F3E5F5 !important;
    /* 일요일 색상 */
}
</style>
