<!-- ContractModal.vue -->
<template>
    <div v-if="isOpen" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2 class="title">
            계약 정보 수정
            <button class="help-button">?</button>
          </h2>
        </div>
  
        <div class="modal-body">
          <section class="members-section">
            <h3>편집 대상 구성원</h3>
            <div class="member-item">
              <div class="profile-image">
                <img src="@/assets/AdminContractImg/placeholder.png" alt="프로필 이미지" />
              </div>
              <span class="member-name">{{ contract?.work?.user?.name || '이름 없음' }}</span>
            </div>
          </section>
  
          <section class="contract-details">
            <div class="form-group">
              <label for="hourlyWage">시급</label>
              <input id="hourlyWage" v-model="editedContract.hourlyWage" type="number" min="0" step="100" />
            </div>
            <div class="form-group">
              <label for="contractStart">계약 시작일</label>
              <input id="contractStart" v-model="editedContract.contractStart" type="date" />
            </div>
            <div class="form-group">
              <label for="contractEnd">계약 종료일</label>
              <input id="contractEnd" v-model="editedContract.contractEnd" type="date" />
            </div>
          </section>
  
          <button class="add-button" @click="addSchedule">
            <span class="plus-icon">+</span>
            추가
          </button>
  
          <section v-if="contract?.schedules?.length">
            <div v-for="schedule in contract.schedules" :key="schedule.id" class="schedule-section">
              <div class="schedule-header day-box">
                <span class="day">{{ getDayName(schedule.day) }}</span>
              </div>
              <div class="schedule-actions">
                <button @click="editSchedule(schedule)" class="action-button edit-button">
                  <span class="icon">✏️</span>
                </button>
                <button @click="deleteSchedule(schedule)" class="action-button delete-button">
                  <span class="icon">🗑️</span>
                </button>
              </div>
              <div class="schedule-details">
                <div class="time-slot">
                  <span class="time-icon">🕐</span>
                  {{ schedule.officialStart }} - {{ schedule.officialEnd }}
                </div>
                <div class="break-time">
                  <span class="break-icon">☕</span>
                  {{ formatDuration(schedule.breakMinute) }}
                </div>
              </div>
            </div>
          </section>
  
          <section class="weekdays-section" v-if="!contract?.schedules?.length">
            <div class="weekdays-header">
              월, 화, 수, 목, 금, 토, 일
              <span class="status">일정 없음</span>
            </div>
          </section>
  
          <!-- 메시지 표시 -->
          <div v-if="message" :class="messageType" style="margin-top:20px;">
            {{ message }}
          </div>
        </div>
  
        <div class="modal-footer">
          <button class="cancel-button" @click="closeModal">취소</button>
          <button class="save-button" @click="saveContract">저장</button>
        </div>
      </div>
      <!-- ScheduleModal 추가 -->
      <ScheduleModal :is-open="showScheduleModal" :schedule="currentSchedule" @close="closeScheduleModal"
      @confirm="saveSchedule"/>
    </div>
  </template>
  
  <script setup>
  import { ref, defineProps, defineEmits, watch } from 'vue'
  import { useContractsStore } from '@/stores/contracts' // Pinia 스토어 import
  import ScheduleModal from '@/components/employment/ScheduleModal.vue'
  
  // Props 정의
  const props = defineProps({
    isOpen: {
      type: Boolean,
      default: false
    },
    contract: {
      type: [Object, null],
      required: false,
    }
  })
  
  // Emits 정의
  const emit = defineEmits(['close', 'save'])
  
  // Pinia 스토어 사용
  const contractsStore = useContractsStore()
  
  // 수정된 계약 데이터
  const editedContract = ref({
    hourlyWage: 0,
    contractStart: '',
    contractEnd: ''
  })
  
  // 메시지 상태
  const message = ref('')
  const messageType = ref('') // 'success' 또는 'error'
  
  // ScheduleModal 상태 관리
  const showScheduleModal = ref(false)
  const currentSchedule = ref({})
  
  // 스케줄 추가 함수
  const addSchedule = () => {
    currentSchedule.value = {
      day: '',
      officialStart: '',
      officialEnd: '',
      breakMinute: 0
    }
    showScheduleModal.value = true
  }
  
  // 스케줄 수정 함수
  const editSchedule = (schedule) => {
    currentSchedule.value = { ...schedule }
    showScheduleModal.value = true
    console.log("editSchedule schedule:", JSON.stringify(schedule, null, 2));
  }

  // 스케줄 저장 함수
  const saveSchedule = async (schedule) => {
    console.log("saveSchedule schedule:", JSON.stringify(schedule, null, 2));
    if (props.contract) {
      if (schedule.scheduleId) {
        // 기존 스케줄 수정
        try {
          await contractsStore.editSchedule(props.contract.contractId, schedule.scheduleId, schedule)
          message.value = '스케줄이 성공적으로 수정되었습니다.'
          messageType.value = 'success'
          emit('save', schedule); // 부모 컴포넌트로 수정된 스케줄 전달
        } catch (error) {
          message.value = '스케줄 수정에 실패했습니다. 다시 시도해주세요.'
          messageType.value = 'error'

          console.log("newSchedule:", JSON.stringify(newSchedule, null, 2));
          emit('save', newSchedule); // 부모 컴포넌트로 새 스케줄 전달
        }
      } else {
        // 새 스케줄 추가
        try {
          await contractsStore.addSchedule(props.contract.contractId, schedule)
          message.value = '스케줄이 성공적으로 추가되었습니다.'
          messageType.value = 'success'
          props.contract.schedules.push(newSchedule); // 새 스케줄 추가
        } catch (error) {
          message.value = '스케줄 추가에 실패했습니다. 다시 시도해주세요.'
          messageType.value = 'error'
        }
      }
      closeScheduleModal()
    }
  }
  
  // 스케줄 삭제 함수
  const deleteSchedule = async (schedule) => {
    if (props.contract && schedule.scheduleId) {
      try {
        await contractsStore.deleteSchedule(props.contract.contractId, schedule.scheduleId)
        message.value = '스케줄이 성공적으로 삭제되었습니다.'
        messageType.value = 'success'
      } catch (error) {
        message.value = '스케줄 삭제에 실패했습니다. 다시 시도해주세요.'
        messageType.value = 'error'
      }
    }
  }
  
  // 스케줄 모달 닫기 함수
  const closeScheduleModal = () => {
    showScheduleModal.value = false
  }
  
  // 계약 데이터 감시 및 편집 데이터 초기화
  watch(() => props.contract, (newContract) => {
    if (newContract) {
      // LocalDateTime -> YYYY-MM-DD 변환
      const startDate = newContract.contractStart ? newContract.contractStart.split('T')[0] : ''
      const endDate = newContract.contractEnd ? newContract.contractEnd.split('T')[0] : ''
  
      editedContract.value = {
        hourlyWage: newContract.hourlyWage,
        contractStart: startDate,
        contractEnd: endDate,
      }
    } else {
      editedContract.value = {
        hourlyWage: 0,
        contractStart: '',
        contractEnd: '',
      }
    }
  }, { immediate: true })
  
  // 모달 닫기 함수
  const closeModal = () => {
    emit('close')
  }
  
  // 요일 정보 가져오기 - getDayName 함수 (객체(Object) 기반 매핑)
  // 객체는 키를 통해 값을 접근하지만, 배열은 인덱스를 통해 값을 접근 
  // 배열의 인덱스는 0부터 시작하는 반면, 객체의 키는 1부터 시작
  const getDayName = (day) => {
  const dayMapNumber = {
    1: '월',
    2: '화',
    3: '수',
    4: '목',
    5: '금',
    6: '토',
    7: '일',
  };
  return dayMapNumber[day] || '요일 정보 없음';
};

  
  // formatDuration 함수 정의
  const formatDuration = (minutes) => {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return `${hours}시간 ${mins}분`;
  };
  
  // 계약 저장 함수
  const saveContract = async () => {
    const baseUrl = import.meta.env.VITE_API_URL
    console.log('baseUrl:', baseUrl) // baseUrl이 undefined나 빈 문자열이 아닌지 확인
  
    console.log('props.contract:', props.contract)
    if (props.contract && props.contract.contractId) {
      const updatedContract = {
        ...props.contract,
        ...editedContract.value,
        // LocalDateTime 형식에 맞게 'T00:00:00' 추가
        contractStart: editedContract.value.contractStart ? `${editedContract.value.contractStart}T00:00:00` : null,
        contractEnd: editedContract.value.contractEnd ? `${editedContract.value.contractEnd}T00:00:00` : null,
      };

  
      try {
        await contractsStore.updateContract(props.contract.contractId, updatedContract)
        emit('save', updatedContract); // 부모로 이벤트 전달
        console.log('계약 업데이트 성공')
        message.value = '계약 정보가 성공적으로 업데이트되었습니다.'
        messageType.value = 'success'
      } catch (error) {
        console.error('계약 업데이트 실패:', error)
        message.value = '계약 업데이트에 실패했습니다. 다시 시도해주세요.'
        messageType.value = 'error'
      }
    } else {
      message.value = '유효한 계약 ID가 없습니다.'
      messageType.value = 'error'
    }
  
    // closeModal()을 바로 호출하지 않고 메시지를 표시한 후 닫기
    setTimeout(() => {
      closeModal()
      message.value = ''
      messageType.value = ''
    }, 2000) // 2초 후 모달 닫기
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
    border-radius: 16px;
    width: 90%;
    max-width: 500px;
    max-height: 80%;
    /* 모달의 최대 높이를 화면의 80%로 제한 */
    overflow-y: auto;
    /* 스크롤바 */
    padding: 24px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  }
  
  .modal-header {
    margin-bottom: 24px;
  }
  
  .title {
    font-size: 20px;
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 8px;
  }
  
  .help-button {
    width: 24px;
    height: 24px;
    border-radius: 50%;
    border: 1px solid #e2e8f0;
    background: none;
    color: #718096;
    cursor: pointer;
  }
  
  .members-section {
    margin-bottom: 20px;
  }
  
  .members-section h3 {
    font-size: 16px;
    color: #4a5568;
    margin-bottom: 12px;
  }
  
  .member-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px;
    background: #f7fafc;
    border-radius: 8px;
  }
  
  .profile-image {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    overflow: hidden;
    background: #e2e8f0;
  }
  
  .profile-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .member-name {
    font-size: 16px;
    color: #2d3748;
  }
  
  .contract-details {
    background: #f7fafc;
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 20px;
  }
  
  .form-group {
    margin-bottom: 16px;
  }
  
  .form-group:last-child {
    margin-bottom: 0;
  }
  
  .form-group label {
    display: block;
    margin-bottom: 8px;
    font-weight: 500;
    color: #4a5568;
  }
  
  .form-group input {
    width: 100%;
    padding: 8px 12px;
    border: 1px solid #e2e8f0;
    border-radius: 4px;
    font-size: 14px;
    background-color: white;
  }
  
  .form-group input:focus {
    outline: none;
    border-color: #3182ce;
    box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.5);
  }
  
  .add-button {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 16px;
    border: 2px solid #e2e8f0;
    border-radius: 8px;
    background: none;
    color: #4a5568;
    cursor: pointer;
    margin-bottom: 20px;
    width: 100%;
    justify-content: center;
    transition: all 0.2s;
  }
  
  .add-button:hover {
    background: #f7fafc;
  }
  
  .plus-icon {
    color: #3182ce;
    font-size: 18px;
  }
  
  .schedule-section {
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 20px;
    position: relative;
  }
  
  .schedule-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 16px;
    color: #4a5568;
  }
  
  .schedule-actions {
    position: absolute;
    top: 8px;
    right: 8px;
    display: none;
  }
  
  .schedule-section:hover .schedule-actions {
    display: flex;
  }
  
  .action-button {
    background: none;
    border: none;
    cursor: pointer;
    padding: 4px;
    margin-left: 4px;
  }
  
  .action-button:hover {
    background-color: rgba(0, 0, 0, 0.1);
    border-radius: 50%;
  }
  
  .icon {
    font-size: 16px;
  }
  
  .schedule-details {
    display: grid;
    gap: 12px;
    color: #718096;
  }
  
  .weekdays-section {
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 24px;
  }
  
  .weekdays-header {
    color: #4a5568;
    display: flex;
    justify-content: space-between;
  }
  
  .status {
    color: #718096;
  }
  
  .modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
  
  .cancel-button,
  .save-button {
    padding: 8px 24px;
    border-radius: 8px;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s;
  }
  
  .cancel-button {
    border: 1px solid #e2e8f0;
    background: #f7fafc;
    color: #4a5568;
  }
  
  .save-button {
    border: none;
    background: #3182ce;
    color: white;
  }
  
  .cancel-button:hover {
    background: #edf2f7;
  }
  
  .save-button:hover {
    background: #2c5282;
  }
  
  .success {
    color: green;
  }
  
  .error {
    color: red;
  }
  </style>
  