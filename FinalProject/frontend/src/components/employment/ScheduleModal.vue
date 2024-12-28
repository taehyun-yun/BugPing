<!-- ScheduleModal.vue -->

<template>
  <!-- 모달이 열려 있을 때만 이 부분이 보입니다. -->
  <div v-if="isOpen" class="modal-overlay" @click="closeModal">
    <!-- 모달 내용 영역. 배경 클릭 시 모달이 닫히지 않도록 @click.stop을 사용합니다. -->
    <div class="modal-content" @click.stop>
      <!-- 모달 헤더: 제목과 닫기 버튼 -->
      <div class="modal-header">
        <h2>기본 근무 일정</h2>
        <!-- 닫기 버튼: 클릭 시 모달을 닫습니다. -->
        <button class="close-button" @click="closeModal">×</button>
      </div>

      <!-- 모달 본문: 근무 일정 입력 폼 -->
      <div class="modal-body">
        <!-- 요일 선택 섹션 -->
        <section class="weekday-section">
          <h3>요일<span class="required">*</span></h3>
          <!-- 요일 버튼 그룹: 사용자가 근무 요일을 선택할 수 있습니다. -->
          <div class="weekday-buttons">
            <button v-for="day in weekdays" :key="day.value"
              :class="['weekday-button', { active: selectedDay === day.value }]" @click="selectedDay = day.value">
              {{ day.label }}
            </button>
          </div>
        </section>

        <!-- 근무시간 입력 섹션 -->
        <section class="work-time-section">
          <h3>근무시간 <span class="required"></span></h3>
          <!-- 근무 시작과 끝 시간을 선택하는 입력 그룹 -->
          <div class="time-inputs">
            <!-- 근무 시작 시간 그룹 -->
            <div class="time-group">
              <div class="select-wrapper">
                <select v-model="startHour">
                  <option value="">선택</option>
                  <!-- 0시부터 23시까지 선택할 수 있는 옵션 -->
                  <option v-for="h in 24" :key="`start-${h}`">
                    {{ String(h - 1).padStart(2, '0') }}
                  </option>
                </select>
              </div>
              <span>:</span>
              <div class="select-wrapper">
                <select v-model="startMinute">
                  <option value="">선택</option>
                  <!-- 0분부터 59분까지 선택할 수 있는 옵션 -->
                  <option v-for="m in 60" :key="`start-${m}`">
                    {{ String(m - 1).padStart(2, '0') }}
                  </option>
                </select>
              </div>
              <span class="time-label">시작</span>
            </div>

            <!-- 근무 종료 시간 그룹 -->
            <div class="time-group">
              <div class="select-wrapper">
                <select v-model="endHour">
                  <option value="">선택</option>
                  <!-- 0시부터 23시까지 선택할 수 있는 옵션 -->
                  <option v-for="h in 24" :key="`end-hour-${h}`">
                    {{ String(h - 1).padStart(2, '0') }}
                  </option>
                </select>
              </div>
              <span>:</span>
              <div class="select-wrapper">
                <select v-model="endMinute">
                  <option value="">선택</option>
                  <!-- 0분부터 59분까지 선택할 수 있는 옵션 -->
                  <option v-for="m in 60" :key="`end-${m}`">
                    {{ String(m - 1).padStart(2, '0') }}
                  </option>
                </select>
              </div>
              <span class="time-label">끝</span>
            </div>
          </div>
        </section>

        <!-- 휴식시간 입력 섹션 -->
        <section class="break-time-section">
          <h3>휴식시간</h3>
          <!-- 휴식 시간 입력 필드: 시간과 분을 선택할 수 있습니다. -->
          <div class="break-time-input">
            <div class="select-wrapper">
              <select v-model="breakTimeHour">
                <option value="">선택</option>
                <!-- 0시간부터 23시간까지 선택할 수 있는 옵션 -->
                <option v-for="h in 24" :key="`break-hour-${h}`" :value="h - 1">
                  {{ String(h - 1).padStart(2, '0') }}
                </option>
              </select>
            </div>
            <span>시간</span>
            <div class="select-wrapper">
              <select v-model="breakTimeMinute">
                <option value="">선택</option>
                <!-- 0분부터 59분까지 선택할 수 있는 옵션 -->
                <option v-for="m in 60" :key="`break-minute-${m}`" :value="m - 1">
                  {{ String(m - 1).padStart(2, '0') }}
                </option>
              </select>
            </div>
            <span>분</span>
          </div>
        </section>
      </div>

      <!-- 모달 푸터: 취소 및 확인 버튼 -->
      <div class="modal-footer">
        <!-- 메시지 컨테이너 -->
        <div class="message-container" v-if="message" :class="messageType">
          {{ message }}
        </div>
        <!-- 버튼 그룹 -->
        <div class="button-group">
          <button class="cancel-button" @click="closeModal">취소</button>
          <button class="confirm-button" @click="handleConfirm">확인</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
/*
  Vue.js 3의 <script setup> 구문을 사용하여 컴포넌트 로직을 정의합니다.
  Props와 Emits를 사용하여 부모 컴포넌트와 데이터를 주고받습니다.
  ref와 watch를 사용하여 반응형 데이터를 관리합니다.
*/

import { ref, watch } from 'vue' // Vue의 반응성 API 가져오기

// 부모 컴포넌트로부터 전달받는 Props 정의
const props = defineProps({
  isOpen: {
    type: Boolean, // 모달 열림 상태는 Boolean 타입
    default: false // 기본값은 닫혀 있는 상태
  },
  schedule: {
    type: Object, // 스케줄 정보는 객체 타입
    default: () => ({}) // 기본값은 빈 객체
  }
})

console.log('Initial schedule:', props.schedule); // 🔵 초기 스케줄 정보 확인


// 부모 컴포넌트로 이벤트를 보낼 때 사용
const emit = defineEmits(['close', 'confirm']) // 'close'와 'confirm' 이벤트 선언

// 모달 내부 상태 관리
const selectedDay = ref(props.schedule.day || '') // 선택된 요일 (기본값: 빈 문자열 또는 전달된 스케줄의 요일)
const startHour = ref('') // 근무 시작 시간 (시)
const startMinute = ref('') // 근무 시작 시간 (분)
const endHour = ref('') // 근무 종료 시간 (시)
const endMinute = ref('') // 근무 종료 시간 (분)
const breakTimeHour = ref('') // 휴식 시간 (시간)
const breakTimeMinute = ref('') // 휴식 시간 (분)

const message = ref('');
const messageType = ref('');


// 스케줄 정보가 변경될 때마다 모달 내부 상태를 업데이트
watch(
  () => props.schedule,
  (newSchedule) => {
    console.log('ScheduleModal - Received schedule:', newSchedule);

    if (newSchedule) {
      selectedDay.value = newSchedule.day || '';
      const [startH, startM] = newSchedule.officialStart?.split(':') || ['', ''];
      startHour.value = startH;
      startMinute.value = startM;

      const [endH, endM] = newSchedule.officialEnd?.split(':') || ['', ''];
      endHour.value = endH;
      endMinute.value = endM;

      const breakTime = newSchedule.breakMinute ?? 0;
      breakTimeHour.value = Math.floor(breakTime / 60);
      breakTimeMinute.value = breakTime % 60;

      // 디버깅용 로그 추가
      console.log('Calculated Break Time:', {
        breakMinute: breakTime,
        breakTimeHour: breakTimeHour.value,
        breakTimeMinute: breakTimeMinute.value,
      });   
    }
  },
  { immediate: true }
);


// 모달을 닫는 함수: 부모 컴포넌트로 'close' 이벤트를 보냄
const closeModal = () => {
  emit('close') // 'close' 이벤트 전송
}

// 확인 버튼을 클릭했을 때 실행되는 함수: 입력된 스케줄 정보를 부모 컴포넌트로 전달
const handleConfirm = () => {

  // 유효성 검사: 요일 선택 여부 확인
  if (!selectedDay.value) {
  showMessage('요일을 선택해주세요.', 'error');
  return;
}

  // 휴식 시간을 총 분으로 계산
  const totalBreakTimeMinutes = (breakTimeHour.value || 0) * 60 + (breakTimeMinute.value || 0)

  // 입력된 스케줄 데이터를 객체로 생성
  const scheduleData = {
    ...props.schedule, // 기존 데이터 유지 (temporaryId 포함) // temporaryId 포함아님 현재 코드 수정함
    // scheduleId: props.schedule.scheduleId || null, // 기존 스케줄 ID가 있으면 포함, 없으면 null
    day: selectedDay.value, // 선택된 요일
    officialStart: `${startHour.value || '00'}:${startMinute.value || '00'}`,
    officialEnd: `${endHour.value || '00'}:${endMinute.value || '00'}`,
    breakMinute: totalBreakTimeMinutes,
  }

  // 부모 컴포넌트로 'confirm' 이벤트와 스케줄 데이터를 전달
  emit('confirm', scheduleData)
  closeModal() // 모달을 닫음
}

const showMessage = (msg, type = 'error') => {
  message.value = msg;
  messageType.value = type;
  setTimeout(() => {
    message.value = '';
    messageType.value = '';
  }, 3000);
};

// 요일 목록 정의: 요일의 레이블과 값을 포함한 배열
const weekdays = [
  { label: '월', value: 1 },
  { label: '화', value: 2 },
  { label: '수', value: 3 },
  { label: '목', value: 4 },
  { label: '금', value: 5 },
  { label: '토', value: 6 },
  { label: '일', value: 7 },
]
</script>

<style scoped>
/*
  이 스타일은 ScheduleModal 컴포넌트에만 적용됩니다.
  scoped 속성을 사용하여 다른 컴포넌트에 영향을 주지 않도록 합니다.
*/

/* 모달의 배경 오버레이 스타일 */
.modal-overlay {
  position: fixed;
  /* 화면에 고정 */
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  /* 반투명 검은색 배경 */
  display: flex;
  /* 플렉스 박스 레이아웃 사용 */
  align-items: center;
  /* 수직 가운데 정렬 */
  justify-content: center;
  /* 수평 가운데 정렬 */
  z-index: 1000;
  /* 다른 요소들보다 위에 표시 */
}

/* 모달 내용 영역 스타일 */
.modal-content {
  background: white;
  /* 흰색 배경 */
  width: 90%;
  /* 너비를 화면의 90%로 설정 */
  max-width: 500px;
  /* 최대 너비는 500px */
  max-height: 90vh;
  /* 최대 높이는 화면의 90% */
  border-radius: 16px;
  /* 둥근 모서리 */
  overflow-y: auto;
  /* 세로 스크롤 가능 */
}

/* 모달 헤더 스타일 */
.modal-header {
  padding: 20px;
  /* 내부 여백 */
  display: flex;
  /* 플렉스 박스 레이아웃 사용 */
  justify-content: space-between;
  /* 양 끝으로 요소 배치 */
  align-items: center;
  /* 수직 정렬 */
  border-bottom: 1px solid #eee;
  /* 하단 테두리 */
}

/* 모달 헤더 제목 스타일 */
.modal-header h2 {
  font-size: 20px;
  /* 글자 크기 */
  font-weight: 600;
  /* 글자 두께 */
  margin: 0;
  /* 여백 없음 */
}

/* 닫기 버튼 스타일 */
.close-button {
  background: none;
  /* 배경 없음 */
  border: none;
  /* 테두리 없음 */
  font-size: 24px;
  /* 글자 크기 */
  cursor: pointer;
  /* 커서 포인터 변경 */
  padding: 0;
  /* 내부 여백 없음 */
  color: #666;
  /* 글자 색상 회색 */
}

/* 모달 본문 스타일 */
.modal-body {
  padding: 20px;
  /* 내부 여백 */
}

/* 각 섹션의 마진 설정 */
section {
  margin-bottom: 24px;
  /* 아래 여백 */
}

/* 섹션 제목 스타일 */
h3 {
  font-size: 16px;
  /* 글자 크기 */
  margin-bottom: 12px;
  /* 아래 여백 */
  font-weight: 500;
  /* 글자 두께 */
}

/* 필수 입력 항목 표시 스타일 */
.required {
  color: #ff4444;
  /* 글자 색상 빨간색 */
}

/* 요일 버튼 그룹 스타일 */
.weekday-buttons {
  display: flex;
  /* 플렉스 박스 레이아웃 사용 */
  gap: 8px;
  /* 버튼 간 간격 */
}

/* 요일 버튼 기본 스타일 */
.weekday-button {
  width: 40px;
  /* 너비 */
  height: 40px;
  /* 높이 */
  border-radius: 50%;
  /* 원형 */
  border: 1px solid #ddd;
  /* 테두리 */
  background: white;
  /* 배경 흰색 */
  cursor: pointer;
  /* 커서 포인터 변경 */
  transition: all 0.2s;
  /* 모든 속성에 대해 0.2초 동안 전환 효과 */
}

/* 활성화된 요일 버튼 스타일 */
.weekday-button.active {
  background: #2196f3;
  /* 배경색 파란색 */
  color: white;
  /* 글자 색상 흰색 */
  border-color: #2196f3;
  /* 테두리 색상 파란색 */
}

/* 선택 박스 래퍼 스타일 */
.select-wrapper {
  position: relative;
  /* 자식 요소의 위치 기준 */
  background: white;
  /* 흰색 배경 */
  border: 1px solid #ddd;
  /* 테두리 */
  border-radius: 8px;
  /* 둥근 모서리 */
  overflow: hidden;
  /* 넘치는 내용 숨김 */
}

/* 셀렉트 박스 스타일 */
.select-wrapper select {
  width: 100%;
  /* 너비 100% */
  padding: 10px;
  /* 내부 여백 */
  border: none;
  /* 테두리 없음 */
  background: transparent;
  /* 투명 배경 */
  appearance: none;
  /* 기본 셀렉트 스타일 제거 */
  outline: none;
  /* 외곽선 없음 */
  font-size: 14px;
  /* 글자 크기 */
}

/* 전체 너비를 사용하는 셀렉트 래퍼 */
.full-width {
  width: 100%;
  /* 너비 100% */
}

/* 근무시간 입력 그룹 스타일 */
.time-inputs {
  display: flex;
  /* 플렉스 박스 레이아웃 사용 */
  flex-direction: column;
  /* 세로 방향으로 배치 */
  gap: 12px;
  /* 요소 간 간격 */
}

/* 개별 시간 그룹 스타일 */
.time-group {
  display: flex;
  /* 플렉스 박스 레이아웃 사용 */
  align-items: center;
  /* 수직 정렬 */
  gap: 8px;
  /* 요소 간 간격 */
}

/* 시간 그룹 내 셀렉트 래퍼의 너비 설정 */
.time-group .select-wrapper {
  width: 80px;
  /* 너비 80px */
}

/* 시간 레이블 스타일 */
.time-label {
  margin-left: 8px;
  /* 왼쪽 여백 */
  color: #666;
  /* 글자 색상 회색 */
}

/* 휴식시간 입력 그룹 스타일 */
.break-time-input {
  display: flex;
  /* 플렉스 박스 레이아웃 사용 */
  align-items: center;
  /* 수직 정렬 */
  gap: 8px;
  /* 요소 간 간격 */
}

/* 휴식시간 입력 그룹 내 셀렉트 래퍼의 너비 설정 */
.break-time-input .select-wrapper {
  width: 80px;
  /* 너비 80px */
}

/* 메모 입력 스타일 (현재는 주석 처리됨) */
.memo-input {
  position: relative;
  /* 자식 요소의 위치 기준 */
}

textarea {
  width: 100%;
  /* 너비 100% */
  height: 100px;
  /* 높이 100px */
  padding: 12px;
  /* 내부 여백 */
  border: 1px solid #ddd;
  /* 테두리 */
  border-radius: 8px;
  /* 둥근 모서리 */
  resize: none;
  /* 크기 조절 불가 */
  font-size: 14px;
  /* 글자 크기 */
}

/* 메모 입력 시 글자 수 표시 스타일 */
.memo-count {
  position: absolute;
  /* 부모를 기준으로 위치 */
  right: 12px;
  /* 오른쪽에서 12px 위치 */
  bottom: 12px;
  /* 아래에서 12px 위치 */
  font-size: 12px;
  /* 글자 크기 */
  color: #666;
  /* 글자 색상 회색 */
}

/* 모달 푸터 스타일 */
.modal-footer {
  padding: 20px;
  /* 내부 여백 */
  display: flex;

  /* 플렉스 박스 레이아웃 사용 */
  /* justify-content: flex-end; */


  justify-content: space-between; /* 메시지는 왼쪽, 버튼은 오른쪽 */
  align-items: center; /* 수직 가운데 정렬 */

  /* 오른쪽으로 요소 정렬 */
  gap: 12px;
  /* 요소 간 간격 */
  border-top: 1px solid #eee;
  /* 상단 테두리 */
}

.modal-footer {
  padding: 20px;
  display: flex;
  align-items: center; /* 수직 가운데 정렬 */
  justify-content: space-between; /* 메시지는 왼쪽, 버튼은 오른쪽 */
  gap: 12px;
  border-top: 1px solid #eee;
  position: relative; /* 버튼 위치 고정을 위한 설정 */
  min-height: 40px; /* footer 자체의 최소 높이 설정 */
}

/* 메시지 컨테이너 스타일 */
.message-container {
  font-size: 14px;
  flex: 1; /* 버튼과 균형 유지 */
  min-height: 18px; /* 메시지가 없어도 일정한 높이 유지 */
  visibility: hidden; /* 메시지가 없을 때 공간만 차지 */
  display: block; /* block으로 강제 고정 */
}

.message-container.error {
  visibility: visible; /* 에러 메시지가 있을 때 보이도록 설정 */
  color: red;
  font-weight: bold;
}

.message-container.success {
  visibility: visible; /* 성공 메시지가 있을 때 보이도록 설정 */
  color: green;
  font-weight: bold;
}

/* 버튼 그룹 스타일 */
.button-group {
  display: flex;
  gap: 12px;
  align-items: center;
  justify-content: flex-end; /* 항상 오른쪽 정렬 */
  flex-shrink: 0; /* 메시지와의 균형 유지 */
  position: absolute; /* 버튼 위치 고정을 위해 사용 */
  right: 20px; /* 모달 오른쪽으로부터 20px */
}



/* 취소 및 확인 버튼 기본 스타일 */
.cancel-button,
.confirm-button {
  padding: 8px 24px;
  /* 내부 여백 */
  border-radius: 8px;
  /* 둥근 모서리 */
  font-size: 14px;
  /* 글자 크기 */
  cursor: pointer;
  /* 커서 포인터 변경 */
  transition: all 0.2s;
  /* 모든 속성에 대해 0.2초 동안 전환 효과 */
}

/* 취소 버튼 스타일 */
.cancel-button {
  background: #f5f5f5;
  /* 배경색 연한 회색 */
  border: 1px solid #ddd;
  /* 테두리 */
}

/* 확인 버튼 스타일 */
.confirm-button {
  background: #2196f3;
  /* 배경색 파란색 */
  color: white;
  /* 글자 색상 흰색 */
  border: none;
  /* 테두리 없음 */
}

/* 취소 버튼 호버 시 스타일 */
.cancel-button:hover {
  background: #eee;
  /* 호버 시 배경색 약간 진한 회색 */
}

/* 확인 버튼 호버 시 스타일 */
.confirm-button:hover {
  background: #1976d2;
  /* 호버 시 배경색 더 어두운 파란색 */
}

/* 반응형 스타일: 화면 너비가 480px 이하일 때 적용 */
/* @media (max-width: 480px) {
  .modal-content {
    width: 100%;
    height: 100%;
    max-height: 100vh;
    border-radius: 0;
  }

  .weekday-buttons {
    flex-wrap: wrap;
    justify-content: center;
  }

} */
</style>
