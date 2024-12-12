<template>
    <div class="calendar-and-schedule">
        <div class="calendar-container">
            <FullCalendar ref="calendarRef" :options="calendarOptions" />
        </div>

        <!--  모달  -->
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
import { ref } from 'vue';
import FullCalendar from '@fullcalendar/vue3'; // FullCalendar 
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
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
        title: event.title,
        description: event.extendedProps.description || '설명 없음',
        start: event.start,
        end: event.end,
    };
    isModalVisible.value = true;
};

// 모달 닫기
const closeModal = () => {
    isModalVisible.value = false;
}

// 서버에서 일정 데이터 가져오기
    const calendarOptions = ref({
        plugins: [dayGridPlugin, interactionPlugin],
        initialView: 'dayGridMonth',
        locale: 'ko', //한국어
        events: async (fetchInfo, successCallback, failureCallback) => {
            try {

                // 서버와 날짜 요청 형식을 맞추기
                const startFormatted = format(new Date(fetchInfo.start), 'yyyy-MM-dd');
                const endFormatted = format(new Date(fetchInfo.end), 'yyyy-MM-dd');

                const response = await axios.get('http://localhost:8707/api/schedules', {
                    params: {
                        userId: 'jh',
                        start: startFormatted,
                        end: endFormatted,
                    },
                });
                successCallback(response.data);
                console.log('Fetched Events:', response.data);
            } catch (error) {
                console.error('이벤트 데이터를 가져오는 중 오류 발생:', error);
                failureCallback(error);
            }
    },
    editable: true,     // 드래그로 이벤트 수정 가능
    selectable: true,   // 드래그로 영역 선택 가능
    eventColor: '#3788d8',  // 기본 이벤트 색상

    // 이벤트 렌더링: 제목만 표시
    eventContent: function (info) {
        return {
            html: `<div class="fc-event-title">${info.event.title}</div>`,
        };
    },

    // 이벤트 클릭시 모달 열기
    eventClick: function (info) {
        openModal(info.event);
    }
});

const calendarRef = ref(null);

</script>

<style>
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
</style>