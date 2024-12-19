<!-- ContractModal.vue -->

<template>
  <!-- 모달이 열려 있을 때만 이 부분이 보입니다. -->
  <div v-if="isOpen" class="modal-overlay" @click.self="closeModal">
    <!-- 모달 내용 영역. 배경 클릭 시 모달이 닫히지 않도록 @click.stop을 사용합니다. -->
    <div class="modal-content" @click.stop>
      <!-- 모달 헤더: 제목과 도움말 버튼 -->
      <div class="modal-header">
        <h2 class="title">
          계약 정보 수정
          <!-- 도움말 버튼: 클릭 시 추가적인 도움말을 제공할 수 있습니다. -->
          <button class="help-button">?</button>
        </h2>
      </div>

      <!-- 모달 본문: 계약 세부 정보와 스케줄 관리 -->
      <div class="modal-body">
        <!-- 구성원 섹션: 계약에 관련된 직원 정보를 보여줍니다. -->
        <section class="members-section">
          <h3>편집 대상 구성원</h3>
          <!-- 구성원 아이템: 클릭 시 사용자 모달을 엽니다. -->

          <div class="member-item" @click="handleMemberClick">
            <div class="profile-image">
              <template v-if="selectedEmployee?.name">
                <!-- 선택된 유저의 첫 글자 표시 -->
                <div class="avatar">{{ selectedEmployee.name.charAt(0) }}</div>
              </template>
              <template v-else-if="contract?.work?.user?.name">
                <!-- 계약된 유저의 첫 글자 표시 -->
                <div class="avatar">{{ contract.work.user.name.charAt(0) }}</div>
              </template>
              <template v-else>
                <!-- 기본 이미지 표시 -->
                <img src="@/assets/AdminContractImg/placeholder.png" alt="기본 이미지" />
              </template>
            </div>
            <span class="member-name">
              {{ selectedEmployee?.name || contract?.work?.user?.name || '이름 없음' }}
            </span>
          </div>
        </section>

        <!-- 계약 세부 정보 섹션: 시급, 계약 시작일, 계약 종료일을 편집할 수 있습니다. -->
        <section class="contract-details">
          <div class="form-group">
            <label for="hourlyWage">시급</label>
            <!-- 시급 입력 필드: 숫자만 입력 가능하며 최소값은 0, 100 단위로 증가합니다. -->
            <input id="hourlyWage" v-model="editedContract.hourlyWage" type="number" min="0" step="100" />
          </div>
          <div class="form-group">
            <label for="contractStart">계약 시작일</label>
            <!-- 계약 시작일 입력 필드: 날짜를 선택할 수 있습니다. -->
            <input id="contractStart" v-model="editedContract.contractStart" type="date" />
          </div>
          <div class="form-group">
            <label for="contractEnd">계약 종료일</label>
            <!-- 계약 종료일 입력 필드: 날짜를 선택할 수 있습니다. -->
            <input id="contractEnd" v-model="editedContract.contractEnd" type="date" />
          </div>
        </section>

        <!-- 스케줄 추가 버튼: 클릭 시 스케줄 추가 모달을 엽니다. -->
        <button class="add-button" @click="addSchedule">
          <span class="plus-icon">+</span>
          추가
        </button>

        <!-- 스케줄 목록 섹션: 계약에 스케줄이 있을 때만 표시됩니다. -->
        <section v-if="contract?.schedules?.length">
          <!-- 각 스케줄을 반복하여 표시합니다. -->
          <div v-for="schedule in contract.schedules" :key="schedule.scheduleId" class="schedule-section">
            <!-- 스케줄 헤더: 요일을 표시합니다. -->
            <div class="schedule-header day-box">
              <span class="day">{{ getDayName(schedule.day) }}</span>
            </div>
            <!-- 스케줄 액션: 수정 및 삭제 버튼을 포함합니다. -->
            <div class="schedule-actions">
              <button @click="editSchedule(schedule)" class="action-button edit-button">
                <span class="icon">✏️</span>
              </button>
              <button @click="handleDeleteSchedule(schedule)" class="action-button delete-button">
                <span class="icon">🗑️</span>
              </button>
            </div>
            <!-- 스케줄 세부 정보: 시작 시간, 종료 시간, 휴게 시간을 표시합니다. -->
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

        <!-- 스케줄이 없는 경우 표시되는 섹션 -->
        <section class="weekdays-section" v-if="!contract?.schedules?.length">
          <div class="weekdays-header">
            월, 화, 수, 목, 금, 토, 일
            <span class="status">일정 없음</span>
          </div>
        </section>

        <!-- 메시지 표시: 성공 또는 오류 메시지를 보여줍니다. -->
        <div v-if="message" :class="messageType" style="margin-top:20px;">
          {{ message }}
        </div>
      </div>

      <!-- 모달 푸터: 취소 및 저장 버튼을 포함합니다. -->
      <div class="modal-footer">
        <button class="cancel-button" @click="closeModal">취소</button>
        <button class="save-button" @click="saveContract">저장</button>
      </div>
    </div>

    <!-- 스케줄 추가/수정 모달 컴포넌트 -->
    <ScheduleModal :is-open="showScheduleModal" :schedule="currentSchedule" @close="closeScheduleModal"
      @confirm="handleScheduleConfirm" />

    <!-- 사용자 선택 모달 컴포넌트 -->
    <UserModal :is-open="showUserModal" @close="closeUserModal" @save="handleUserSelection" />
  </div>
</template>

<script setup>
/*
  Vue.js 3의 <script setup> 구문을 사용하여 컴포넌트 로직을 정의합니다.
  Pinia 스토어와 다른 컴포넌트를 가져옵니다.
*/
import { ref, defineProps, defineEmits, watch , computed} from 'vue'
import { useContractsStore } from '@/stores/contracts' // Pinia 스토어 import
import ScheduleModal from '@/components/employment/ScheduleModal.vue' // 스케줄 모달 컴포넌트 import
import UserModal from '@/components/employment/UserModal.vue' // 사용자 모달 컴포넌트 import

// Props 정의: 부모 컴포넌트로부터 전달받는 데이터
const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false // 기본값은 모달이 닫혀 있는 상태
  },
  contract: {
    type: Object, // 계약 정보는 객체 타입으로 설정 (빈 객체 기본값)
    default: () => ({
      schedules: []
    }),
  }
  // contract: {
  //   type: [Object, null],
  //   required: false, // 계약 정보는 필수가 아님
  // }
})

// Emits 정의: 부모 컴포넌트로 이벤트를 보낼 때 사용
const emit = defineEmits(['close', 'save'])

// Pinia 스토어 사용: 계약 데이터를 관리
const contractsStore = useContractsStore()

//확인
// const a = computed(()=> props.contract?.work?.user?.name==undefined);
// watch(a, (newValue) => { console.log(newValue)})

// watch(
//   () => props.isOpen, // 모달이 열리는 상태를 감지
//   (newVal) => {
//     if (newVal) {
//       console.log('모달이 열렸습니다.');
//       console.log('contract?.work?.user?.name:', props.contract?.work?.user?.name);
//       console.log('selectedEmployee?.name:', selectedEmployee?.name);
//     }
//   }
// );


// 수정된 계약 데이터를 저장하는 반응형 변수
const editedContract = ref({
  hourlyWage: 0, // 시급
  contractStart: '', // 계약 시작일
  contractEnd: '' // 계약 종료일
})

// 메시지 상태를 관리하는 반응형 변수
const message = ref('')
const messageType = ref('') // 'success' 또는 'error'

// 사용자 모달 상태 관리
const showUserModal = ref(false) // 사용자 모달이 열려 있는지 여부
const selectedEmployee = ref(null) // 선택된 직원 정보

// 🟢 수정: 스케줄 모달 상태 관리 추가
const showScheduleModal = ref(false) // 스케줄 모달 열림 상태
const currentSchedule = ref({}) // 현재 스케줄 정보

// 🟢 수정: 스케줄 변경 사항을 저장할 로컬 리스트 추가
const addedSchedules = ref([]) // 추가된 스케줄 목록
const editedSchedules = ref([]) // 수정된 스케줄 목록
const deletedSchedules = ref([]) // 삭제된 스케줄 목록


// 🟢 수정: closeScheduleModal 메서드 정의
const closeScheduleModal = () => {
  showScheduleModal.value = false
}



const handleMemberClick = () => {
  console.log("Clicked member item");
  console.log("contract?.work?.user?.name:", props.contract?.work?.user?.name);
  console.log("selectedEmployee?.name:", selectedEmployee.value?.name);

  if (!props.contract?.work?.user?.name) {//&& !selectedEmployee.value?.name
    console.log("Opening user modal");
    openUserModal();
  } else {
    console.log("Conditions not met for opening modal");
  }
};

// 사용자 모달 열기 메서드
const openUserModal = () => {
  console.log("openUserModal");
  showUserModal.value = true // 사용자 모달을 열기 위해 상태를 true로 설정
}


// 사용자 모달에서 선택한 직원 처리 메서드
const handleUserSelection = (employee) => {
  selectedEmployee.value = employee // 선택된 직원을 저장
  message.value = `${employee.name}이(가) 선택되었습니다.` // 성공 메시지 설정
  messageType.value = 'success' // 메시지 타입을 성공으로 설정
  console.log('선택된 직원:', employee) // 콘솔에 선택된 직원 정보 출력

  // 계약에 선택된 직원 반영
  if (props.contract) {
    // Props는 직접 수정하지 않는 것이 좋습니다.
    // 대신, Pinia 스토어를 통해 업데이트하거나, 부모 컴포넌트에 변경 사항을 전달하세요.
    // 예시:
    contractsStore.updateContractUser(props.contract.contractId, employee)
  }
}

// 사용자 모달 닫기 메서드
const closeUserModal = () => {
  showUserModal.value = false // 사용자 모달을 닫기 위해 상태를 false로 설정
}

// 스케줄 모달 상태 관리
// const showScheduleModal = ref(false) // 스케줄 모달이 열려 있는지 여부
// const currentSchedule = ref({}) // 현재 수정 중인 스케줄 정보

// 스케줄 추가 함수
const addSchedule = () => {
  currentSchedule.value = {
    id: null, // 새로운 스케줄이므로 ID는 null//////
    day: '', // 요일
    officialStart: '', // 공식 시작 시간
    officialEnd: '', // 공식 종료 시간
    breakMinute: 0 // 휴게 시간 (분)
  }
  showScheduleModal.value = true // 스케줄 모달을 열기 위해 상태를 true로 설정
}

// 스케줄 수정 함수
const editSchedule = (schedule) => {
  currentSchedule.value = { ...schedule } // 기존 스케줄 정보를 복사하여 저장
  showScheduleModal.value = true // 스케줄 모달을 열기 위해 상태를 true로 설정
  console.log("editSchedule schedule:", JSON.stringify(schedule, null, 2)) // 수정할 스케줄 정보 콘솔 출력
}

// 🟢 수정: 스케줄 삭제 함수 수정
const handleDeleteSchedule = (schedule) => {
  // 먼저, 스케줄이 추가된 스케줄인지 확인
  const addedIndex = addedSchedules.value.findIndex(s => s.scheduleId === schedule.scheduleId)
  if (addedIndex !== -1) {
    // 🟢 스케줄이 추가된 스케줄이면, 추가된 스케줄 목록에서 제거
    addedSchedules.value.splice(addedIndex, 1)
    message.value = '새로 추가된 스케줄이 삭제되었습니다.'
    messageType.value = 'success'
  } else {
    // 🟢 기존 스케줄이면, 삭제된 스케줄 목록에 추가하고 화면에서 제거
    deletedSchedules.value.push(schedule)
    props.contract.schedules = props.contract.schedules.filter(s => s.scheduleId !== schedule.scheduleId)
    message.value = '스케줄이 삭제되었습니다.'
    messageType.value = 'success'
  }
}

// 🟢 수정: 스케줄 확인 처리 함수 추가
const handleScheduleConfirm = (schedule) => {
  if (schedule.scheduleId) { // 기존 스케줄 수정
    const index = props.contract.schedules.findIndex(s => s.scheduleId === schedule.scheduleId)
    if (index !== -1) {
      // 스케줄 업데이트
      props.contract.schedules[index] = schedule
      // 수정된 스케줄 목록에 추가
      editedSchedules.value.push(schedule)
      message.value = '스케줄이 수정되었습니다.'
      messageType.value = 'success'
    }
  } else { // 새로운 스케줄 추가
    const newSchedule = { ...schedule, id: Date.now() } // 🟢 임시 ID 할당
    props.contract.schedules.push(newSchedule)
    // 추가된 스케줄 목록에 추가
    addedSchedules.value.push(newSchedule)
    message.value = '스케줄이 추가되었습니다.'
    messageType.value = 'success'
  }
}


// 스케줄 모달 닫기 함수
// const closeScheduleModal = () => {
//   showScheduleModal.value = false // 스케줄 모달을 닫기 위해 상태를 false로 설정
// }

// 계약 데이터 감시 및 편집 데이터 초기화
watch(() => props.contract, (newContract) => {
  if (newContract) { // 새로운 계약 정보가 들어오면
    // LocalDateTime 형식을 YYYY-MM-DD로 변환
    const startDate = newContract.contractStart ? newContract.contractStart.split('T')[0] : ''
    const endDate = newContract.contractEnd ? newContract.contractEnd.split('T')[0] : ''

    // 편집된 계약 데이터를 설정
    editedContract.value = {
      hourlyWage: newContract.hourlyWage, // 시급
      contractStart: startDate, // 계약 시작일
      contractEnd: endDate, // 계약 종료일
    }
  } else { // 계약 정보가 없으면 기본값으로 초기화
    editedContract.value = {
      hourlyWage: 0,
      contractStart: '',
      contractEnd: '',
    }
  }
}, { immediate: true }) // 컴포넌트가 처음 로드될 때도 실행

// 모달 닫기 함수: 부모 컴포넌트로 'close' 이벤트를 보냅니다.
const closeModal = () => {
  emit('close') // 부모 컴포넌트에 'close' 이벤트 전달
}

// 요일 정보 가져오기 함수: 숫자를 요일 이름으로 변환
const getDayName = (day) => {
  const dayMapNumber = {
    1: '월',
    2: '화',
    3: '수',
    4: '목',
    5: '금',
    6: '토',
    7: '일',
  }
  return dayMapNumber[day] || '요일 정보 없음' // 유효하지 않은 숫자는 '요일 정보 없음'으로 표시
}

// 휴게 시간 포맷팅 함수: 분을 시와 분으로 변환
const formatDuration = (minutes) => {
  const hours = Math.floor(minutes / 60) // 전체 시 계산
  const mins = minutes % 60 // 남은 분 계산
  return `${hours}시간 ${mins}분` // "1시간 30분"과 같은 형식으로 반환
}

// 🟢 수정: 계약 저장 함수 수정
const saveContract = async () => {
  const baseUrl = import.meta.env.VITE_API_URL
  console.log('baseUrl:', baseUrl)

  console.log('props.contract:', props.contract)
  if (props.contract && props.contract.contractId) {
    const updatedContract = {
      ...props.contract,
      ...editedContract.value,
      contractStart: editedContract.value.contractStart ? `${editedContract.value.contractStart}T00:00:00` : null,
      contractEnd: editedContract.value.contractEnd ? `${editedContract.value.contractEnd}T00:00:00` : null,
    }

    try {
      // 🟢 계약 정보 업데이트
      await contractsStore.updateContract(props.contract.contractId, updatedContract)

      // 🟢 추가된 스케줄 저장
      for (const schedule of addedSchedules.value) {
        console.log('Adding schedule:', schedule)
        await contractsStore.addSchedule(props.contract.contractId, schedule)
      }

      // 🟢 수정된 스케줄 저장
      for (const schedule of editedSchedules.value) {
        console.log('Editing schedule:', schedule)
        await contractsStore.editSchedule(props.contract.contractId, schedule.scheduleId, schedule)
      }

      // 🟢 삭제된 스케줄 삭제
      for (const schedule of deletedSchedules.value) {
        console.log('Deleting schedule:', schedule)
        await contractsStore.deleteSchedule(props.contract.contractId, schedule.scheduleId)
      }
      // 🟢 모든 스케줄 변경 사항 초기화
      addedSchedules.value = []
      editedSchedules.value = []
      deletedSchedules.value = []

      // 부모 컴포넌트에 저장 이벤트 전달
      emit('save', updatedContract)
      console.log('계약 업데이트 성공')
      message.value = '계약 정보가 성공적으로 업데이트되었습니다.'
      messageType.value = 'success'
    } catch (error) {
      console.error('계약 업데이트 실패:', error)
      message.value = '계약 업데이트에 실패했습니다. 다시 시도해주세요.'
      messageType.value = 'error'
    }

    // 2초 후에 모달을 닫고 메시지를 초기화합니다.
    setTimeout(() => {
      closeModal()
      message.value = ''
      messageType.value = ''
    }, 2000)
  } else {
    message.value = '유효한 계약 ID가 없습니다.'
    messageType.value = 'error'
  }
}
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
