<template>
  <div v-if="isOpen" class="modal-overlay" @click.self="closeModal">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h2 class="title">
          계약 정보
          <button class="help-button">?</button>
        </h2>
      </div>

      <div class="modal-body">
        <section class="members-section">
          <h3>편집 대상 구성원</h3>
          <div class="member-item" @click="handleMemberClick">
            <div class="profile-image">
              <template v-if="selectedEmployee?.name">
                <div class="avatar">{{ selectedEmployee.name.charAt(0) }}</div>
              </template>
              <template v-else-if="contract?.work?.user?.name">
                <div class="avatar">{{ contract.work.user.name.charAt(0) }}</div>
              </template>
              <template v-else>
                <img src="@/assets/AdminContractImg/placeholder.png" alt="기본 이미지" />
              </template>
            </div>
            <span class="member-name">
              {{ selectedEmployee?.name || contract?.work?.user?.name || '이름 없음' }}
            </span>
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
          <div v-for="schedule in contract.schedules" :key="schedule.scheduleId" class="schedule-section">
            <div class="schedule-header day-box">
              <span class="day">{{ getDayName(schedule.day) }}</span>
            </div>
            <div class="schedule-actions">
              <button @click="editSchedule(schedule)" class="action-button edit-button">
                <span class="icon">✏️</span>
              </button>
              <button @click="handleDeleteSchedule(schedule)" class="action-button delete-button">
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

        <div v-if="message" :class="messageType" style="margin-top:20px;">
          {{ message }}
        </div>
      </div>

      <div class="modal-footer">
        <button class="cancel-button" @click="closeModal">취소</button>
        <button class="save-button" @click="saveContract">저장</button>
      </div>
    </div>

    <ScheduleModal :is-open="showScheduleModal" :schedule="currentSchedule" @close="closeScheduleModal" @confirm="handleScheduleConfirm" />

    <UserModal :is-open="showUserModal" @close="closeUserModal" @save="handleUserSelection" />
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch } from 'vue';
import { useContractsStore } from '@/stores/contracts';
import ScheduleModal from '@/components/employment/ScheduleModal.vue';
import UserModal from '@/components/employment/UserModal.vue';

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false,
  },
  contract: {
    type: Object,
    default: () => ({
      schedules: [],
      work: {
        user: null, // 🔵 추가된 기본값
        workId: null, // 🔵 추가된 기본값
      },
    }),
  },
});

const emit = defineEmits(['close', 'save']);

const contractsStore = useContractsStore();

const editedContract = ref({});
const addedSchedules = ref([]);
const editedSchedules = ref([]);
const deletedSchedules = ref([]);
const message = ref('');
const messageType = ref('');
const showScheduleModal = ref(false);
const currentSchedule = ref({});
const showUserModal = ref(false);
const selectedEmployee = ref(null);
const selectedWorkId = ref(null); // 선택된 workId를 저장

watch(
  () => props.contract,
  (newContract) => {
    if (newContract) {
      const { contractStart, contractEnd, hourlyWage, work } = newContract;
      editedContract.value = {
        contractStart: contractStart?.split('T')[0] || '',
        contractEnd: contractEnd?.split('T')[0] || '',
        hourlyWage: hourlyWage || 0,
      };
      selectedEmployee.value = work?.user || null; // 🔵 work.user 설정
      selectedWorkId.value = work?.workId || null; // 🔵 workId 설정
    }
  },
  { immediate: true },
);


const handleMemberClick = () => {
  if (!props.contract?.work?.user?.name) {
    openUserModal();
  }
};

const openUserModal = () => {
  showUserModal.value = true;
};


const handleUserSelection = ({ employee, workId }) => {
  selectedEmployee.value = employee;
  selectedWorkId.value = workId; // WorkID 저장

  // 📌 콘솔 로그 추가: 선택된 사용자와 workId 확인
  console.log('User Selected:', employee);
  console.log('Work ID Selected:', workId);

  message.value = `${employee.name}이(가) 선택되었습니다.`;
  messageType.value = 'success';
  clearMessage();
};





const closeUserModal = () => {
  showUserModal.value = false;
};

const addSchedule = () => {
  currentSchedule.value = {
    id: null,
    day: '',
    officialStart: '',
    officialEnd: '',
    breakMinute: 0,
  };
  showScheduleModal.value = true;
};

const editSchedule = (schedule) => {
  currentSchedule.value = { ...schedule };
  showScheduleModal.value = true;
};

const handleDeleteSchedule = (schedule) => {
  if (!schedule.scheduleId) {
    addedSchedules.value = addedSchedules.value.filter((s) => s.id !== schedule.id);
  } else {
    deletedSchedules.value.push(schedule);
    props.contract.schedules = props.contract.schedules.filter((s) => s.scheduleId !== schedule.scheduleId);
  }
  message.value = '스케줄이 삭제되었습니다.';
  messageType.value = 'success';
  clearMessage();
};

const handleScheduleConfirm = (schedule) => {
  if (schedule.scheduleId) {
    const index = props.contract.schedules.findIndex((s) => s.scheduleId === schedule.scheduleId);
    if (index !== -1) {
      props.contract.schedules[index] = schedule;
      editedSchedules.value.push(schedule);
      message.value = '스케줄이 수정되었습니다.';
      messageType.value = 'success';
    }
  } else {
    const newSchedule = { ...schedule, id: Date.now() };
    props.contract.schedules.push(newSchedule);
    addedSchedules.value.push(newSchedule);
    message.value = '스케줄이 추가되었습니다.';
    messageType.value = 'success';
  }
  showScheduleModal.value = false;
  clearMessage();
};

const clearMessage = () => {
  setTimeout(() => {
    message.value = '';
    messageType.value = '';
  }, 2000);
};

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

const formatDuration = (minutes) => {
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  return `${hours}시간 ${mins}분`;
};


const saveContract = async () => {
  try {
    // 📌 콘솔 로그 추가: 현재 selectedEmployee와 selectedWorkId 확인
    console.log('Selected Employee:', selectedEmployee.value);
    console.log('Selected Work ID:', selectedWorkId.value);

    // 📌 유효성 검사: 구성원이 선택되었는지 확인
    if (!selectedEmployee.value) {
      throw new Error('구성원이 선택되지 않았습니다.');
    }

    const contractData = {
      ...props.contract,
      ...editedContract.value,
      work: {
        user: {
          userId: selectedEmployee.value.userId,
          name: selectedEmployee.value.name,
        },
        workId: selectedWorkId.value,
      },
      contractStart: editedContract.value.contractStart ? `${editedContract.value.contractStart}T00:00:00` : null,
      contractEnd: editedContract.value.contractEnd ? `${editedContract.value.contractEnd}T00:00:00` : null,
    };

    // 📌 콘솔 로그 추가: 구성된 contractData 확인
    console.log('Contract Data to Save:', contractData);

    let newContract;
    if (props.contract?.contractId) {
      // 기존 계약 업데이트
      await contractsStore.updateContract(props.contract.contractId, contractData);
      console.log('Contract updated successfully in store.');
    } else {
      // 새 계약 생성
      newContract = await contractsStore.addContract(contractData);

      // 📌 콘솔 로그 추가: addContract의 반환값 확인
      console.log('Added Contract:', newContract);

      if (newContract?.contractId) {
        emit('save', newContract); // 생성된 계약 데이터를 부모 컴포넌트로 전달
      } else {
        throw new Error('새 계약 생성 실패: contractId가 반환되지 않았습니다.');
      }
    }

    message.value = '계약이 성공적으로 저장되었습니다.';
    messageType.value = 'success';
    closeModal();
  } catch (error) {
    console.error('계약 저장 실패:', error);
    message.value = '계약 저장에 실패했습니다.';
    messageType.value = 'error';
  } finally {
    clearMessage();
  }
};






const closeModal = () => {
  emit('close');
};
const closeScheduleModal = () => {
  showScheduleModal.value = false;
};


</script>

<style scoped>
.message {
  margin-top: 20px;
  font-size: 14px;
}

.message.success {
  color: green;
}

.message.error {
  color: red;
}

/* 
  모달의 배경 오버레이 스타일
  화면 전체를 덮고 반투명한 검은색 배경을 가집니다.
*/
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

/* 
  모달 내용 영역 스타일
  흰색 배경, 둥근 모서리, 그림자 효과 등을 가집니다.
*/
.modal-content {
  background: white;
  /* 흰색 배경 */
  border-radius: 16px;
  /* 둥근 모서리 */
  width: 90%;
  /* 너비를 화면의 90%로 설정 */
  max-width: 500px;
  /* 최대 너비는 500px */
  max-height: 80%;
  /* 최대 높이는 화면의 80% */
  overflow-y: auto;
  /* 세로 스크롤 가능 */
  padding: 24px;
  /* 내부 여백 */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  /* 그림자 효과 */
}

/* 모달 헤더 스타일 */
.modal-header {
  margin-bottom: 24px;
  /* 아래 여백 */
}

/* 모달 제목 스타일 */
.title {
  font-size: 20px;
  /* 글자 크기 */
  font-weight: 600;
  /* 글자 두께 */
  display: flex;
  /* 플렉스 박스 레이아웃 사용 */
  align-items: center;
  /* 수직 정렬 */
  gap: 8px;
  /* 요소 간 간격 */
}

/* 도움말 버튼 스타일 */
.help-button {
  width: 24px;
  /* 너비 */
  height: 24px;
  /* 높이 */
  border-radius: 50%;
  /* 원형 */
  border: 1px solid #e2e8f0;
  /* 테두리 */
  background: none;
  /* 배경 없음 */
  color: #718096;
  /* 글자 색상 회색 */
  cursor: pointer;
  /* 커서 포인터 변경 */
}

/* 구성원 섹션 스타일 */
.members-section {
  margin-bottom: 20px;
  /* 아래 여백 */
}

/* 구성원 섹션 제목 스타일 */
.members-section h3 {
  font-size: 16px;
  /* 글자 크기 */
  color: #4a5568;
  /* 글자 색상 회색 */
  margin-bottom: 12px;
  /* 아래 여백 */
}

/* 구성원 아이템 스타일 */
.member-item {
  display: flex;
  /* 플렉스 박스 레이아웃 사용 */
  align-items: center;
  /* 수직 정렬 */
  gap: 12px;
  /* 요소 간 간격 */
  padding: 12px;
  /* 내부 여백 */
  background: #f7fafc;
  /* 배경색 연한 회색 */
  border-radius: 8px;
  /* 둥근 모서리 */
  cursor: pointer;
  /* 커서 포인터 변경 */
  transition: background 0.2s;
  /* 배경색 전환 효과 */
}

.member-item:hover {
  background: #edf2f7;
  /* 호버 시 배경색 약간 진한 회색 */
}

/* 프로필 이미지 스타일 */
.profile-image {
  width: 40px;
  /* 너비 */
  height: 40px;
  /* 높이 */
  border-radius: 50%;
  /* 원형 */
  overflow: hidden;
  /* 넘치는 내용 숨김 */
  background: #e2e8f0;
  /* 배경색 회색 */
}

/* 프로필 이미지 안의 이미지 스타일 */
.profile-image img {
  width: 100%;
  /* 이미지 너비를 부모에 맞춤 */
  height: 100%;
  /* 이미지 높이를 부모에 맞춤 */
  object-fit: cover;
  /* 이미지 비율 유지하며 채움 */
}

.avatar {
  width: 40px;
  /* 원형 아이콘 크기 */
  height: 40px;
  background-color: #e2e8f0;
  /* 연한 회색 배경 */
  color: #4a5568;
  /* 글자 색상 */
  border-radius: 50%;
  /* 원형 처리 */
  display: flex;
  /* 플렉스 박스 활성화 */
  justify-content: center;
  /* 수평 가운데 정렬 */
  align-items: center;
  /* 수직 가운데 정렬 */
  font-size: 16px;
  /* 글자 크기 */
  font-weight: bold;
  /* 글자 굵기 */
  text-transform: uppercase;
  /* 대문자로 표시 */
}

/* 
.disabled {
  pointer-events: none; 
  opacity: 0.5; 
} 
*/

/* 구성원 이름 스타일 */
.member-name {
  font-size: 16px;
  /* 글자 크기 */
  color: #2d3748;
  /* 글자 색상 어두운 회색 */
}

/* 계약 세부 정보 섹션 스타일 */
.contract-details {
  background: #f7fafc;
  /* 배경색 연한 회색 */
  border-radius: 8px;
  /* 둥근 모서리 */
  padding: 16px;
  /* 내부 여백 */
  margin-bottom: 20px;
  /* 아래 여백 */
}

/* 폼 그룹 스타일 */
.form-group {
  margin-bottom: 16px;
  /* 아래 여백 */
}

/* 마지막 폼 그룹의 아래 여백을 없앰 */
.form-group:last-child {
  margin-bottom: 0;
}

/* 폼 그룹 내 레이블 스타일 */
.form-group label {
  display: block;
  /* 블록 요소로 표시 */
  margin-bottom: 8px;
  /* 아래 여백 */
  font-weight: 500;
  /* 글자 두께 */
  color: #4a5568;
  /* 글자 색상 회색 */
}

/* 폼 그룹 내 입력 필드 스타일 */
.form-group input {
  width: 100%;
  /* 너비를 부모에 맞춤 */
  padding: 8px 12px;
  /* 내부 여백 */
  border: 1px solid #e2e8f0;
  /* 테두리 */
  border-radius: 4px;
  /* 둥근 모서리 */
  font-size: 14px;
  /* 글자 크기 */
  background-color: white;
  /* 배경색 흰색 */
}

/* 입력 필드 포커스 시 스타일 */
.form-group input:focus {
  outline: none;
  /* 외곽선 없음 */
  border-color: #3182ce;
  /* 테두리 색상 파란색 */
  box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.5);
  /* 그림자 효과 */
}

/* 스케줄 추가 버튼 스타일 */
.add-button {
  display: flex;
  /* 플렉스 박스 레이아웃 사용 */
  align-items: center;
  /* 수직 정렬 */
  gap: 8px;
  /* 요소 간 간격 */
  padding: 8px 16px;
  /* 내부 여백 */
  border: 2px solid #e2e8f0;
  /* 테두리 */
  border-radius: 8px;
  /* 둥근 모서리 */
  background: none;
  /* 배경 없음 */
  color: #4a5568;
  /* 글자 색상 회색 */
  cursor: pointer;
  /* 커서 포인터 변경 */
  margin-bottom: 20px;
  /* 아래 여백 */
  width: 100%;
  /* 너비 100% */
  justify-content: center;
  /* 요소를 가운데로 정렬 */
  transition: all 0.2s;
  /* 모든 속성에 대해 0.2초 동안 전환 효과 */
}

/* 스케줄 추가 버튼 호버 시 스타일 */
.add-button:hover {
  background: #f7fafc;
  /* 호버 시 배경색 연한 회색 */
}

/* 플러스 아이콘 스타일 */
.plus-icon {
  color: #3182ce;
  /* 아이콘 색상 파란색 */
  font-size: 18px;
  /* 아이콘 크기 */
}

/* 스케줄 섹션 스타일 */
.schedule-section {
  border: 1px solid #e2e8f0;
  /* 테두리 */
  border-radius: 8px;
  /* 둥근 모서리 */
  padding: 16px;
  /* 내부 여백 */
  margin-bottom: 20px;
  /* 아래 여백 */
  position: relative;
  /* 상대 위치 지정 */
}

/* 스케줄 헤더 스타일 */
.schedule-header {
  display: flex;
  /* 플렉스 박스 레이아웃 사용 */
  align-items: center;
  /* 수직 정렬 */
  gap: 8px;
  /* 요소 간 간격 */
  margin-bottom: 16px;
  /* 아래 여백 */
  color: #4a5568;
  /* 글자 색상 회색 */
}

/* 스케줄 액션 버튼 컨테이너 스타일 */
.schedule-actions {
  position: absolute;
  /* 절대 위치 지정 */
  top: 8px;
  /* 위에서 8px 위치 */
  right: 8px;
  /* 오른쪽에서 8px 위치 */
  display: none;
  /* 기본적으로 숨김 */
}

/* 스케줄 섹션 호버 시 액션 버튼 표시 */
.schedule-section:hover .schedule-actions {
  display: flex;
  /* 호버 시 액션 버튼을 보이게 함 */
}

/* 액션 버튼 스타일 */
.action-button {
  background: none;
  /* 배경 없음 */
  border: none;
  /* 테두리 없음 */
  cursor: pointer;
  /* 커서 포인터 변경 */
  padding: 4px;
  /* 내부 여백 */
  margin-left: 4px;
  /* 왼쪽 여백 */
}

/* 액션 버튼 호버 시 스타일 */
.action-button:hover {
  background-color: rgba(0, 0, 0, 0.1);
  /* 배경색 반투명 검은색 */
  border-radius: 50%;
  /* 원형 */
}

/* 아이콘 스타일 */
.icon {
  font-size: 16px;
  /* 아이콘 크기 */
}

/* 스케줄 세부 정보 스타일 */
.schedule-details {
  display: grid;
  /* 그리드 레이아웃 사용 */
  gap: 12px;
  /* 요소 간 간격 */
  color: #718096;
  /* 글자 색상 회색 */
}

/* 요일이 없는 스케줄 섹션 스타일 */
.weekdays-section {
  border: 1px solid #e2e8f0;
  /* 테두리 */
  border-radius: 8px;
  /* 둥근 모서리 */
  padding: 16px;
  /* 내부 여백 */
  margin-bottom: 24px;
  /* 아래 여백 */
}

/* 요일 헤더 스타일 */
.weekdays-header {
  color: #4a5568;
  /* 글자 색상 회색 */
  display: flex;
  /* 플렉스 박스 레이아웃 사용 */
  justify-content: space-between;
  /* 양 끝으로 요소 배치 */
}

/* 상태 메시지 스타일 */
.status {
  color: #718096;
  /* 글자 색상 회색 */
}

/* 모달 푸터 스타일 */
.modal-footer {
  display: flex;
  /* 플렉스 박스 레이아웃 사용 */
  justify-content: flex-end;
  /* 오른쪽으로 요소 정렬 */
  gap: 12px;
  /* 요소 간 간격 */
}

/* 취소 및 저장 버튼 스타일 */
.cancel-button,
.save-button {
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
  border: 1px solid #e2e8f0;
  /* 테두리 */
  background: #f7fafc;
  /* 배경색 연한 회색 */
  color: #4a5568;
  /* 글자 색상 회색 */
}

/* 저장 버튼 스타일 */
.save-button {
  border: none;
  /* 테두리 없음 */
  background: #3182ce;
  /* 배경색 파란색 */
  color: white;
  /* 글자 색상 흰색 */
}

/* 취소 버튼 호버 시 스타일 */
.cancel-button:hover {
  background: #edf2f7;
  /* 호버 시 배경색 약간 진한 회색 */
}

/* 저장 버튼 호버 시 스타일 */
.save-button:hover {
  background: #2c5282;
  /* 호버 시 배경색 더 어두운 파란색 */
}

/* 성공 메시지 스타일 */
.success {
  color: green;
  /* 글자 색상 초록색 */
}

/* 오류 메시지 스타일 */
.error {
  color: red;
  /* 글자 색상 빨간색 */
}
</style>
