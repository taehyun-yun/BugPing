<template>
    <div v-if="isOpen" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>기본 근무 일정</h2>
          <button class="close-button" @click="closeModal">×</button>
        </div>
  
        <div class="modal-body">
          <section class="weekday-section">
            <h3>요일</h3>
            <div class="weekday-buttons">
              <button v-for="day in weekdays" :key="day.value"
                :class="['weekday-button', { active: selectedDay === day.value }]" @click="selectedDay = day.value">
                {{ day.label }}
              </button>
            </div>
          </section>
  
          <!-- <section class="work-type-section">
            <h3>유형</h3>
            <div class="select-wrapper">
              <select v-model="workType">
                <option value="근무">근무</option>
                <option value="휴가">휴가</option>
                <option value="외근">외근</option>
              </select>
            </div>
          </section> -->
  
          <section class="work-time-section">
            <h3>근무시간 <span class="required">*</span></h3>
            <div class="time-inputs">
              <div class="time-group">
                <div class="select-wrapper">
                  <select v-model="startHour">
                    <option value="">선택</option>
                    <option v-for="h in 24" :key="`start-${h}`">
                      {{ String(h - 1).padStart(2, '0') }}
                    </option>
                  </select>
                </div>
                <span>:</span>
                <div class="select-wrapper">
                  <select v-model="startMinute">
                    <option value="">선택</option>
                    <option v-for="m in 60" :key="`start-${m}`">
                      {{ String(m - 1).padStart(2, '0') }}
                    </option>
                  </select>
                </div>
                <span class="time-label">시작</span>
              </div>
  
              <div class="time-group">
                <div class="select-wrapper">
                  <select v-model="endHour">
                    <option value="">선택</option>
                    <option v-for="h in 24" :key="`end-${h}`">
                      {{ String(h - 1).padStart(2, '0') }}
                    </option>
                  </select>
                </div>
                <span>:</span>
                <div class="select-wrapper">
                  <select v-model="endMinute">
                    <option value="">선택</option>
                    <option v-for="m in 60" :key="`end-${m}`">
                      {{ String(m - 1).padStart(2, '0') }}
                    </option>
                  </select>
                </div>
                <span class="time-label">끝</span>
              </div>
            </div>
          </section>
  
          <section class="break-time-section">
            <h3>휴식시간</h3>
            <div class="break-time-input">
              <div class="select-wrapper">
                <select v-model="breakHour">
                  <option value="">선택</option>
                  <option v-for="m in 60" :key="`break-${m}`">
                    {{ String(m - 1).padStart(2, '0') }}
                  </option>
                </select>
              </div>
              <span>시간</span>
              <div class="select-wrapper">
                <select v-model="breakMinute">
                  <option value="">선택</option>
                  <option v-for="m in 60" :key="`break-${m}`">
                    {{ String(m - 1).padStart(2, '0') }}
                  </option>
                </select>
              </div>
              <span>분</span>
            </div>
          </section>
  
          <!-- <section class="workplace-section">
            <h3>근무지</h3>
            <div class="select-wrapper full-width">
              <select v-model="workplace">
                <option value="">선택하세요</option>
                <option value="본사">본사</option>
                <option value="지사">지사</option>
              </select>
            </div>
          </section>
  
          <section class="memo-section">
            <h3>메모</h3>
            <div class="memo-input">
              <textarea v-model="memo" placeholder="입력하세요" maxlength="500"></textarea>
              <div class="memo-count">{{ memo.length }} / 500</div>
            </div>
          </section> -->
        </div>
  
        <div class="modal-footer">
          <button class="cancel-button" @click="closeModal">취소</button>
          <button class="confirm-button" @click="handleConfirm">확인</button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  
  const props = defineProps({
    isOpen: {
      type: Boolean,
      default: false
    }
  })
  
  const emit = defineEmits(['close', 'confirm'])
  
  const weekdays = [
    { label: '월', value: 'MON' },
    { label: '화', value: 'TUE' },
    { label: '수', value: 'WED' },
    { label: '목', value: 'THU' },
    { label: '금', value: 'FRI' },
    { label: '토', value: 'SAT' },
    { label: '일', value: 'SUN' }
  ]
  
  const selectedDay = ref('')
  const workType = ref('근무')
  const startHour = ref('')
  const startMinute = ref('')
  const endHour = ref('')
  const endMinute = ref('')
  const breakHour = ref('')
  const breakMinute = ref('')
  const workplace = ref('')
  const memo = ref('')
  
  const closeModal = () => {
    emit('close')
  }
  
  const handleConfirm = () => {
    emit('confirm', {
      day: selectedDay.value,
      workType: workType.value,
      startTime: `${startHour.value}:${startMinute.value}`,
      endTime: `${endHour.value}:${endMinute.value}`,
      breakTime: `${breakHour.value}:${breakMinute.value}`,
      workplace: workplace.value,
      memo: memo.value
    })
    closeModal()
  }
  </script>
  
  <style scoped>
  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
  }
  
  .modal-content {
    background: white;
    width: 90%;
    max-width: 500px;
    max-height: 90vh;
    border-radius: 16px;
    overflow-y: auto;
  }
  
  .modal-header {
    padding: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #eee;
  }
  
  .modal-header h2 {
    font-size: 20px;
    font-weight: 600;
    margin: 0;
  }
  
  .close-button {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
    padding: 0;
    color: #666;
  }
  
  .modal-body {
    padding: 20px;
  }
  
  section {
    margin-bottom: 24px;
  }
  
  h3 {
    font-size: 16px;
    margin-bottom: 12px;
    font-weight: 500;
  }
  
  .required {
    color: #ff4444;
  }
  
  .weekday-buttons {
    display: flex;
    gap: 8px;
  }
  
  .weekday-button {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    border: 1px solid #ddd;
    background: white;
    cursor: pointer;
    transition: all 0.2s;
  }
  
  .weekday-button.active {
    background: #2196f3;
    color: white;
    border-color: #2196f3;
  }
  
  .select-wrapper {
    position: relative;
    background: white;
    border: 1px solid #ddd;
    border-radius: 8px;
    overflow: hidden;
  }
  
  .select-wrapper select {
    width: 100%;
    padding: 10px;
    border: none;
    background: transparent;
    appearance: none;
    outline: none;
    font-size: 14px;
  }
  
  .full-width {
    width: 100%;
  }
  
  .time-inputs {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }
  
  .time-group {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  
  .time-group .select-wrapper {
    width: 80px;
  }
  
  .time-label {
    margin-left: 8px;
    color: #666;
  }
  
  .break-time-input {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  
  .break-time-input .select-wrapper {
    width: 80px;
  }
  
  .memo-input {
    position: relative;
  }
  
  textarea {
    width: 100%;
    height: 100px;
    padding: 12px;
    border: 1px solid #ddd;
    border-radius: 8px;
    resize: none;
    font-size: 14px;
  }
  
  .memo-count {
    position: absolute;
    right: 12px;
    bottom: 12px;
    font-size: 12px;
    color: #666;
  }
  
  .modal-footer {
    padding: 20px;
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    border-top: 1px solid #eee;
  }
  
  .cancel-button,
  .confirm-button {
    padding: 8px 24px;
    border-radius: 8px;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s;
  }
  
  .cancel-button {
    background: #f5f5f5;
    border: 1px solid #ddd;
  }
  
  .confirm-button {
    background: #2196f3;
    color: white;
    border: none;
  }
  
  .cancel-button:hover {
    background: #eee;
  }
  
  .confirm-button:hover {
    background: #1976d2;
  }
  
  @media (max-width: 480px) {
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
  }
  </style>