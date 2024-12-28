<template>
    <div class="calendar-and-schedule">
        <div class="calendar-container">
            <FullCalendar ref="calendarRef" :options="calendarOptions" />
        </div>
        <div class="employee-list">
            <h3>이번달의 근무자</h3>
            <ul>
                <li v-for="(name, index) in employeeNames" :key="index">
                    <span class="employee-dot" :style="{ backgroundColor: getEmployeeColor(name) }"></span>
                    {{ name }}
                </li>
            </ul>
        </div>
        <!-- 모달 -->
        <div v-if="isModalVisible" class="modal-overlay" @click="closeModal">
            <div class="modal-content" @click.stop>
                <h3>{{ modalData.title }}</h3>
                <p><strong>설명:</strong> {{ modalData.description }}</p>
                <p><strong>시작:</strong> {{ modalData.start }}</p>
                <p><strong>종료:</strong> {{ modalData.end }}</p>
                <p><strong>휴게시간:</strong> {{ modalData.breakTime }}분</p> <!-- 추가 -->
                <p><strong>총 근무시간:</strong> {{ Math.floor(modalData.totalWorkMinutes / 60) }}시간
                    {{ modalData.totalWorkMinutes % 60 }}분</p>
                <button @click="closeModal">닫기</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import FullCalendar from '@fullcalendar/vue3';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import timeGridPlugin from '@fullcalendar/timegrid';
import axios from 'axios';
import { format } from 'date-fns';
import { useUserStore } from '@/stores/userStore';

// HSL 색상에서 Hue와 Lightness를 추출하는 함수
const extractHue = (hsl) => {
    const matches = hsl.match(/hsl\((\d+), \d+%, (\d+)%\)/);
    return matches ? { h: parseInt(matches[1]), l: parseInt(matches[2]) } : { h: 0, l: 0 };
};
// 로컬 스토리지 키 설정
const COLOR_STORAGE_KEY = 'employeeColors';

const employeeColorMap = ref(JSON.parse(localStorage.getItem(COLOR_STORAGE_KEY)) || {});
const usedHues = ref(Object.values(employeeColorMap.value).map(color => extractHue(color)));

const MIN_HUE_DIFFERENCE = 30;
const MIN_LIGHTNESS_DIFFERENCE = 20;

// 이름 기반 색상 반환
const getEmployeeColor = (name) => {
    if (!employeeColorMap.value[name]) {
        let newHue, newLightness, newColor;

        do {
            // 랜덤한 Hue(0~360), Lightness(70~85)를 생성
            newHue = Math.floor(Math.random() * 360);
            newLightness = Math.floor(Math.random() * 15) + 70; // 70% ~ 85%

            // 색상 값 생성
            newColor = `hsl(${newHue}, 70%, ${newLightness}%)`;
        } while (
            usedHues.value.some(hue => Math.abs(hue.h - newHue) < MIN_HUE_DIFFERENCE &&
                Math.abs(hue.l - newLightness) < MIN_LIGHTNESS_DIFFERENCE)
        );

        // 색상 등록
        employeeColorMap.value[name] = newColor;
        usedHues.value.push({ h: newHue, l: newLightness });

        // 로컬 스토리지에 저장
        localStorage.setItem(COLOR_STORAGE_KEY, JSON.stringify(employeeColorMap.value));
    }

    return employeeColorMap.value[name];
};



// 나의 일정, 회사 일정 보기
const selectedUserId = ref(''); // 사용자 ID
const userRole = ref(''); // 사용자 역할 
const isUserView = ref(true); // 초기 상태: 내 근무 보기
const scheduleItems = ref([]);

// pinia Store에서 companyId 가져오기
const userStore = useUserStore();
const selectedCompanyId = ref(userStore.company.companyId);


// 근무자 이름 리스트 생성
const employeeNames = computed(() => {
    const names = scheduleItems.value.map((item) => item.title);
    return [...new Set(names)];
});


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
        title: event.title,
        description: event.extendedProps?.description || '설명 없음',
        start: format(new Date(event.start), 'yyyy-MM-dd HH:mm'),
        end: event.end ? format(new Date(event.end), 'yyyy-MM-dd HH:mm') : '종료 시간이 없습니다.',
        breakTime: event.extendedProps?.breakTime || 0, // 추가: 휴게시간
        totalWorkMinutes: event.extendedProps?.totalWorkMinutes || 0, // 추가: 총 근무시간
    };
    isModalVisible.value = true;
};


// 모달 닫기
const closeModal = () => {
    isModalVisible.value = false;
};

// 구글 Calendar API
const apiKey = 'AIzaSyCcMLoDEakYxNOfXxKIE8JYVIsa8PevUr4';
const holidayCalendarId = 'ko.south_korea%23holiday@group.v.calendar.google.com';

// 버튼 텍스트 동적 설정
const buttonText = computed(() => (isUserView.value ? '회사 근무 보기' : '내 근무 보기'));

// 공통 옵션 설정
const commonOptions = {
    plugins: [dayGridPlugin, interactionPlugin, timeGridPlugin],
    initialView: 'dayGridMonth',
    locale: 'ko',
    dayCellDidMount: (info) => {
        const day = info.date.getDay(); // 0: 일요일, 6: 토요일
        if (day === 6) {
            info.el.style.backgroundColor = '#E6E6FA'; // 연한 보라색
        } else if (day === 0) {
            info.el.style.backgroundColor = '#F3E5F5'; // 연한 분홍색
        }
    },
};

const headerToolbarOptions = computed(() => {
    return userRole.value === 'ROLE_EMPLOYER'
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
});

// 공휴일과 일반 이벤트 처리 함수
const renderEventContent = (info) => {
    if (info.event.extendedProps?.isHoliday) {
        // 공휴일 이벤트
        return {
            html: `
            <div style="display: flex; align-items: center;">
                <div class="fc-event-title">${info.event.title}</div>
            </div>
            `,
        };
    } else {
        const color = getEmployeeColor(info.event.title); // 이름 기반으로 색상 가져오기
        return {
            html: `
            <div style="display: flex; align-items: center;">
                <span class="employee-dot" 
                    style="background-color: ${color}; margin-right: 5px; width: 10px; height: 10px; border-radius: 50%;">
                </span>
                <div class="fc-event-title">${info.event.title}</div>
            </div>
            `,
        };
    }
};


// FullCalendar 옵션
const calendarOptions = ref({
    ...commonOptions,
    headerToolbar: headerToolbarOptions,
    customButtons: {
        toggleViewButton: {
            text: buttonText.value,
            click: () => {
                isUserView.value = !isUserView.value;
                calendarRef.value.getApi().refetchEvents(); // 일정 새로 로드
            },
        },
    },
    events: async (fetchInfo, successCallback, failureCallback) => {
        try {
            console.log("FullCalendar 요청 시 companyId:", selectedCompanyId.value); // companyid 확ㅇ인
            const startFormatted = format(new Date(fetchInfo.start), 'yyyy-MM-dd');
            const endFormatted = format(new Date(fetchInfo.end), 'yyyy-MM-dd');
            const serverResponse = await axios.get('http://localhost:8707/api/calendar', {
                params: {
                    start: startFormatted,
                    end: endFormatted,
                    viewCompanySchedule: !isUserView.value,
                    companyId: selectedCompanyId.value,
                },
            });

            console.log("서버 응답 데이터:", serverResponse.data);

            const holidaysResponse = axios.get(`https://www.googleapis.com/calendar/v3/calendars/${holidayCalendarId}/events`, {
                params: {
                    key: apiKey,
                    timeMin: fetchInfo.start.toISOString(),
                    timeMax: fetchInfo.end.toISOString(),
                    singleEvents: true,
                    orderBy: 'startTime',
                },
                withCredentials: false,
            });

            const [serverResult, holidayResult] = await Promise.all([serverResponse, holidaysResponse]);

            const { userId, role, schedules } = serverResult.data;
            selectedUserId.value = userId;
            userRole.value = role;

            scheduleItems.value = schedules.map((event) => ({
                id: event.scheduleId, // FullCalendar 이벤트 ID 설정
                title: event.title,
                start: event.start,
                end: event.end,
                editable: role == 'ROLE_EMPLOYER',   
                color: getEmployeeColor(event.title),
                extendedProps: {
                    originalScheduleId: event.scheduleId, // 원래 스케줄 ID
                    description: event.description,
                    breakTime: event.breakTime, // 추가
                    totalWorkMinutes: event.totalWorkMinutes, // 추가
                },
            }));

            const holidaySchedules = holidayResult.data.items
                .filter((event) => event.summary !== '섣달 그믐날')
                .map((event) => ({
                    title: event.summary,
                    start: event.start.date || event.start.dateTime,
                    end: event.end.date || event.end.dateTime,
                    color: '#FF9999',
                    textColor: '#8B0000',
                    editable: false,
                    extendedProps: {
                        isHoliday: true,
                    },
                }));

            const allEvents = [
                ...scheduleItems.value,
                ...holidaySchedules,
            ];
            successCallback(allEvents);
        } catch (error) {
            console.error('이벤트 데이터를 가져오는 중 오류 발생:', error);
            failureCallback(error);
        }
    },

    selectable: true,
    eventColor: '#3788d8',
    eventContent: renderEventContent,
    eventClick: (info) => openModal(info.event),


    // 드래그 앤 드롭 이벤트 추가
    eventDrop: async (info) => {
        try {

            const { event } = info;

            // UTC -> KST 변환 함수
            const convertToKoreanDate = (utcDate) => {
                const koreanOffset = 9 * 60; // UTC+9 (분 단위)
                const utcDateObject = new Date(utcDate);
                const koreanDate = new Date(utcDateObject.getTime() + koreanOffset * 60 * 1000);
                return koreanDate.toISOString(); // ISO 문자열로 반환
            };

            // 변경된 이벤트 데이터를 추출
            const updatedEvent = {
                originalScheduleId: event.extendedProps.originalScheduleId, // 기존 스케줄 ID
                originalDate: format(info.oldEvent.start, 'yyyy-MM-dd'), // 이전 날짜
                newScheduleId: event.id, // 새로운 스케줄 ID
                newDate: format(new Date(convertToKoreanDate(event.start.toISOString())), 'yyyy-MM-dd'), // 새로운 날짜 (KST)
                startTime: convertToKoreanDate(event.start.toISOString()), // 시작 시간 (KST)
                endTime: event.end ? convertToKoreanDate(event.end.toISOString()) : null, // 종료 시간 (KST)
            };

            console.log("전송 데이터:", JSON.stringify(updatedEvent, null, 2)); // 디버깅용

            // 서버로 변경 요청 전송
            await axios.post('http://localhost:8707/api/workchange', updatedEvent, {
            });

            calendarRef.value.getApi().refetchEvents(); // FullCalendar 이벤트 새로고침
            console.log('일정이 성공적으로 변경되었습니다.', updatedEvent);
        } catch (error) {
            console.error('일정 변경 중 오류 발생:', error);
            console.error('서버 응답 데이터:', error.response?.data); // 서버 응답 데이터 확인
            info.revert(); // 변경 취소
        }
    },
});

const calendarRef = ref(null);

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
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700&display=swap');

/* 전체 캘린더 스타일 */
.calendar-and-schedule {
    font-family: 'Noto Sans KR', sans-serif;
    --main-color: #4a90e2;
    --secondary-color: #f3f4f6;
    --text-color: #333;
    --border-color: #e2e8f0;
    --button-padding: 12px;
    /* 버튼 공통 스타일 변수 */
    --button-border-radius: 6px;
    /* 버튼 둥근 테두리 변수 */
    display: flex;
    gap: 20px;
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
    background-color: #f8f9fa;
    border-radius: 12px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.calendar-container {
    flex: 1;
    background-color: #ffffff;
    border-radius: 12px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    overflow: hidden;
}

/* FullCalendar 스타일 오버라이드 */
.fc {
    --fc-border-color: var(--border-color);
    --fc-button-text-color: #ffffff;
    --fc-button-bg-color: var(--main-color);
    --fc-button-border-color: var(--main-color);
    --fc-button-hover-bg-color: #3a7cbd;
    --fc-button-hover-border-color: #3a7cbd;
    --fc-button-active-bg-color: #2c5d8f;
    --fc-button-active-border-color: #2c5d8f;
    font-family: 'Noto Sans KR', sans-serif;
}


.fc .fc-button-primary {
    text-transform: uppercase;
    font-weight: 500;
    padding: 8px 16px;
    border-radius: 6px;
    transition: all 0.3s ease;
}

.fc .fc-button-primary:not(:disabled):active,
.fc .fc-button-primary:not(:disabled).fc-button-active {
    background-color: #2c5d8f;
    border-color: #2c5d8f;
}

.fc .fc-toolbar-title {
    font-size: 1.5em;
    font-weight: 700;
    color: var(--text-color);
}

/* 헤더 스타일 */
.fc .fc-toolbar.fc-header-toolbar {
    margin-bottom: 1.5em;
    padding: 10px;
    background-color: var(--secondary-color);
    border-radius: 8px;
}

/* 날짜 셀 스타일 */
.fc .fc-daygrid-day {
    transition: background-color 0.3s ease;
}

.fc .fc-daygrid-day:hover {
    background-color: #e6f2ff;
}

.fc .fc-daygrid-day-number {
    font-weight: 500;
    color: var(--text-color);
    padding: 8px;
}

/* 이벤트 스타일 */
.fc-event {
    border: none;
    border-radius: 4px;
    padding: 2px 4px;
    font-size: 0.85em;
    transition: transform 0.2s ease;
}

.fc-event:hover {
    transform: translateY(-2px);
}

.fc-event-title {
    font-weight: 500;
}

/* 토요일, 일요일 스타일 */
.fc-day-sat {
    color: #4a90e2;
}

.fc-day-sun {
    color: #e25a5a;
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
    padding: 30px;
    border-radius: 12px;
    width: 400px;
    max-width: 90%;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.modal-content h3 {
    margin-top: 0;
    font-size: 1.8rem;
    color: var(--main-color);
    font-weight: 700;
}

.modal-content p {
    margin: 15px 0;
    line-height: 1.6;
    font-weight: 400;
}

.modal-content button {
    display: block;
    width: 100%;
    padding: var(--button-padding);
    background-color: var(--main-color);
    color: #fff;
    border: none;
    border-radius: var(--button-border-radius);
    font-size: 1rem;
    font-weight: 500;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.modal-content button:hover {
    background-color: #3a7cbd;
}

/* 모달 트랜지션 */
.modal-enter-active,
.modal-leave-active {
    transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
    opacity: 0;
}

.employee-list {
    width: 250px;
    padding: 20px;
    background-color: #ffffff;
    border-radius: 12px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.employee-list h3 {
    font-size: 1.2rem;
    font-weight: 700;
    margin-bottom: 15px;
    color: var(--main-color);
    text-align: center;
}

.employee-list ul {
    list-style-type: none;
    padding: 0;
}

.employee-list li {
    display: flex;
    align-items: center;
    padding: 8px 0;
    font-weight: 400;
    border-bottom: 1px solid var(--border-color);
}

.employee-list li:last-child {
    border-bottom: none;
}

.employee-dot {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    margin-right: 5px;
}

/* 달력 구분선 스타일 수정 */
.fc-theme-standard td,
.fc-theme-standard th {
    border: 1px solid #ddd;
    /* 더 진한 색상으로 변경 */
}

.fc .fc-scrollgrid {
    border: 1px
}
</style>
