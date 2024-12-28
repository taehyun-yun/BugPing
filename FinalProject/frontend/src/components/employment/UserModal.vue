<!-- UserModal.vue -->

<template>
  <!-- 모달이 열려 있을 때만 이 부분이 보입니다. -->
  <div v-if="isOpen" class="modal-overlay" @click.self="closeModal">
    <!-- 모달 내용 영역. 배경 클릭 시 모달이 닫히지 않도록 @click.stop을 사용합니다. -->
    <div class="modal-content" @click.stop>
      <!-- 모달 제목 -->
      <h2 class="title">직원 선택</h2>

      <!-- 검색 컨테이너: 직원 검색을 위한 입력 필드와 아이콘 -->
      <div class="search-container">
        <!-- 검색 입력 필드: 사용자가 이름, ID로 직원 검색 -->
        <input v-model="searchQuery" type="text" placeholder="이름 / ID 검색"
          class="search-input" />
        <!-- 검색 아이콘: 검색 입력 필드 옆에 위치 -->
        <i class="search-icon"></i>
      </div>

      <!-- 필터 섹션: 필터 버튼과 초기화 버튼, 전체 직원 수 표시 -->
      <div class="filter-section">
        <!-- 필터 버튼: 클릭 시 필터 옵션을 제공할 수 있음 (추후 기능 추가 가능) -->
        <button class="filter-button">필터</button>
        <div class="filter-right">
          <!-- 초기화 버튼: 모든 선택과 필터를 초기화 -->
          <button @click="resetSelection" class="reset-button">초기화</button>
          <!-- 전체 직원 수 표시 -->
          <span class="total-count">전체 {{ filteredEmployees.length }}</span>
        </div>
      </div>

      <!-- 직원 목록 컨테이너: 필터링된 직원 목록을 보여줍니다. -->
      <div class="employee-container">

        <!-- 체크박스 그룹: 현재 일하는 직업만 조회할지 여부를 선택 -->
        <div class="checkbox-group">
          <label class="checkbox-label">
            <input type="checkbox" v-model="showCurrentJobOnly" />
            <span>현재 일하는 직원만 조회</span>
          </label>
        </div>

        <!-- 직원 리스트: 필터링된 직원들을 반복하여 표시 -->
        <div class="employee-list">
          <div v-for="employee in filteredEmployees" :key="employee.userId" class="employee-item">
            <label class="employee-label">
              <!-- 체크박스: 여러 직원 선택 가능 -->
              <input type="radio" :value="employee" v-model="selectedEmployee" />
              <!-- 아바타: 직원의 이름 첫 글자를 표시 -->
              <div class="avatar">{{ employee.name.charAt(0) }}</div>
              <!-- 직원 정보: 이름과 부서를 표시 -->
              <div class="employee-info">
                <div class="employee-name">{{ employee.name }}</div>
                <div class="employee-userId">{{ employee.userId }}</div>
              </div>
            </label>
          </div>
        </div>
      </div>

      <!-- 버튼 그룹: 취소 및 저장 버튼을 오른쪽 정렬 -->
      <div class="button-group">
        <button @click="closeModal" class="cancel-button">취소</button>
        <button @click="saveSelection" class="save-button">저장</button>
      </div>
    </div>
  </div>
</template>

<script setup>
/*
  Vue.js 3의 <script setup> 구문을 사용하여 컴포넌트 로직을 정의합니다.
  반응형 변수와 컴퓨티드 프로퍼티를 사용하여 데이터와 로직을 관리합니다.
*/
import axios from 'axios'; // axios 임포트
import { ref, computed, watch, onMounted } from 'vue' // Vue의 반응성 API 가져오기
import { defineProps, defineEmits } from 'vue' // Props와 Emits 정의를 위해 가져오기
import { useUserStore } from '@/stores/userStore';

// 부모 컴포넌트로부터 전달받는 Props 정의
const props = defineProps({
  isOpen: {
    type: Boolean, // 모달 열림 상태는 Boolean 타입
    required: true, // 반드시 전달되어야 함
    default: false // 기본값은 닫혀 있는 상태
  }
})

// 부모 컴포넌트로 이벤트를 보낼 때 사용
const emit = defineEmits(['close', 'save']) // 'close'와 'save' 이벤트 선언

// 검색 쿼리를 저장하는 반응형 변수
const searchQuery = ref('') // 초기값은 빈 문자열
const showCurrentJobOnly = ref(false) // 현재 일하는 직업만 조회 여부
const selectedEmployee = ref(null); // 🟦 선택된 직원 저장
const employees = ref([]);

// 스토어 아이디 가져오기 위해서 사용
const userStore = useUserStore();

const fetchAllEmployees = async () => {
  try {
    const baseUrl = import.meta.env.VITE_API_URL;
    const companyId = userStore.company.companyId; 
    //alert(companyId);
    const url = `${baseUrl}/api/worker/${companyId}`; // 전체 데이터를 가져옴

    const response = await axios.get(url);
    employees.value = response.data.map(work => ({
      workId: work.workId, // workId 추가
      userId: work.user.userId,
      name: work.user.name,
      tel: work.user.tel,
      resignDate: work.resignDate || null,
      hireDate: work.hireDate,
    }));
    ('전체 직원 데이터:', employees.value);
  } catch (error) {
    console.error('직원 데이터 가져오기 실패:', error);
  }
};

const filteredEmployees = computed(() => {
  return employees.value.filter(employee => {
    const matchesSearch = searchQuery.value
      ? employee.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
        employee.userId.toLowerCase().includes(searchQuery.value.toLowerCase())
      : true;
    const matchesCurrentJob = !showCurrentJobOnly.value || employee.resignDate === null;
    return matchesSearch && matchesCurrentJob;
  });
});



/**
 * 초기화 버튼 핸들러
 */
 const resetSelection = () => {
  searchQuery.value = '';
  showCurrentJobOnly.value = false;
  selectedEmployee.value = null;
  fetchAllEmployees();
};


// 선택된 직원을 저장하는 함수: 'save' 이벤트를 통해 부모 컴포넌트로 전달
// const saveSelection = () => {
//   // 체크박스를 사용하므로 여러 직원이 선택될 수 있습니다.
//   const selectedEmployees = employees.value.filter(employee => employee.selected)
//   ('선택된 직원:', selectedEmployees) // 선택된 직원 콘솔 출력
//   if (selectedEmployees.length > 0) {
//     emit('save', selectedEmployees) // 모든 선택된 직원을 'save' 이벤트로 전송
//   }
//   closeModal() // 모달 닫기
// }
const saveSelection = () => {
  if (selectedEmployee.value) {
    emit("save", { employee: selectedEmployee.value, workId: selectedEmployee.value.workId }); // 직원과 workId 함께 전달
    ("saveSelection-selectedEmployee.value:"+selectedEmployee.value);
    ("saveSelection-selectedEmployee.value.workId:"+selectedEmployee.value.workId);
    closeModal();
  } else {
    alert("직원을 선택해주세요.");
  }
};



// 모달을 닫는 함수: 부모 컴포넌트로 'close' 이벤트를 보내요.
const closeModal = () => {
  emit('close') // 'close' 이벤트 전송
}

// 컴포넌트 로드 시 직원 데이터 불러오기
onMounted(() => {
  fetchAllEmployees(); 
});

</script>

<style scoped>
/*
  이 스타일은 UserModal 컴포넌트에만 적용됩니다.
  scoped 속성을 사용하여 다른 컴포넌트에 영향을 주지 않도록 합니다.
*/

/* 모달의 배경 오버레이 스타일 */
.modal-overlay {
  position: fixed;
  /* 화면에 고정 */
  top: 0;
  left: 0;
  width: 100%;
  /* 전체 너비 */
  height: 100%;
  /* 전체 높이 */
  background-color: rgba(0, 0, 0, 0.5);
  /* 반투명 검은색 배경 */
  display: flex;
  /* 플렉스 박스 레이아웃 사용 */
  justify-content: center;
  /* 수평 가운데 정렬 */
  align-items: center;
  /* 수직 가운데 정렬 */
}

/* 모달 내용 영역 스타일 */
.modal-content {
  background-color: white;
  /* 흰색 배경 */
  border-radius: 8px;
  /* 둥근 모서리 */
  padding: 24px;
  /* 내부 여백 */
  width: 90%;
  /* 너비 90% */
  max-width: 500px;
  /* 최대 너비 500px */
  max-height: 600px;
  /* 최대 높이 설정 (필요에 따라 조정) */
  overflow-y: auto;
  /* 세로 스크롤 가능 */
}

/* 모달 제목 스타일 */
.title {
  font-size: 24px;
  /* 글자 크기 */
  font-weight: bold;
  /* 글자 두께 */
  margin-bottom: 20px;
  /* 아래 여백 */
}

/* 검색 컨테이너 스타일 */
.search-container {
  position: relative;
  /* 자식 요소의 위치 기준 */
  margin-bottom: 20px;
  /* 아래 여백 */
}

/* 검색 입력 필드 스타일 */
.search-input {
  width: 100%;
  /* 너비 100% */
  padding: 10px 10px 10px 35px;
  /* 내부 여백: 좌측에 공간 확보 */
  border: 1px solid #ddd;
  /* 테두리 */
  border-radius: 4px;
  /* 둥근 모서리 */
  font-size: 14px;
  /* 글자 크기 */
  box-sizing: border-box; /* 박스 크기 계산 */
}

/* 검색 아이콘 스타일 */
.search-icon {
  position: absolute;
  /* 부모 컨테이너를 기준으로 위치 */
  left: 10px;
  /* 왼쪽에서 10px 위치 */
  top: 50%;
  /* 수직 가운데 */
  transform: translateY(-50%);
  /* 정확히 가운데 정렬 */
  width: 16px;
  /* 아이콘 너비 */
  height: 16px;
  /* 아이콘 높이 */
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%23999'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z'%3E%3C/path%3E%3C/svg%3E");
  background-size: contain;
  /* 배경 이미지 크기 조절 */
  background-repeat: no-repeat;
  /* 배경 이미지 반복 없음 */
}

/* 필터 섹션 스타일 */
.filter-section {
  display: flex;
  /* 플렉스 박스 레이아웃 사용 */
  justify-content: space-between;
  /* 양 끝으로 요소 배치 */
  align-items: center;
  /* 수직 정렬 */
  margin-bottom: 20px;
  /* 아래 여백 */
}

/* 필터 버튼 스타일 */
.filter-button {
  color: #666;
  /* 글자 색상 회색 */
  font-size: 14px;
  /* 글자 크기 */
  background: none;
  /* 배경 없음 */
  border: none;
  /* 테두리 없음 */
  cursor: pointer;
  /* 커서 포인터 변경 */
}

/* 필터 오른쪽 컨테이너 스타일 */
.filter-right {
  display: flex;
  /* 플렉스 박스 레이아웃 사용 */
  align-items: center;
  /* 수직 정렬 */
  gap: 10px;
  /* 요소 간 간격 */
}

/* 초기화 버튼 스타일 */
.reset-button {
  color: #666;
  /* 글자 색상 회색 */
  font-size: 14px;
  /* 글자 크기 */
  background: none;
  /* 배경 없음 */
  border: none;
  /* 테두리 없음 */
  cursor: pointer;
  /* 커서 포인터 변경 */
}

/* 전체 직원 수 표시 스타일 */
.total-count {
  font-size: 14px;
  /* 글자 크기 */
  color: #666;
  /* 글자 색상 회색 */
}

/* 직원 컨테이너 스타일 */
.employee-container {
  border: 1px solid #ddd;
  /* 테두리 */
  border-radius: 4px;
  /* 둥근 모서리 */
  overflow: hidden;
  /* 넘치는 내용 숨김 */
}

/* 체크박스 그룹 스타일 */
.checkbox-group {
  padding: 15px;
  /* 내부 여백 */
  border-bottom: 1px solid #ddd;
  /* 하단 테두리 */
  background-color: #f8f8f8;
  /* 배경색 연한 회색 */
}

/* 체크박스 레이블 스타일 */
.checkbox-label {
  display: flex;
  /* 플렉스 박스 레이아웃 사용 */
  align-items: center;
  /* 수직 정렬 */
  gap: 8px;
  /* 요소 간 간격 */
  font-size: 14px;
  /* 글자 크기 */
  color: #333;
  /* 글자 색상 어두운 회색 */
  cursor: pointer;
  /* 커서 포인터 변경 */
}

/* 직원 리스트 스타일 */
.employee-list {
  max-height: 300px;
  /* 최대 높이 설정 */
  overflow-y: auto;
  /* 세로 스크롤 가능 */
}

/* 직원 아이템 스타일 */
.employee-item {
  padding: 15px;
  /* 내부 여백 */
  border-bottom: 1px solid #ddd;
  /* 하단 테두리 */
}

/* 마지막 직원 아이템의 하단 테두리 제거 */
.employee-item:last-child {
  border-bottom: none;
}

/* 직원 라벨 스타일 */
.employee-label {
  display: flex;
  /* 플렉스 박스 레이아웃 사용 */
  align-items: center;
  /* 수직 정렬 */
  gap: 12px;
  /* 요소 간 간격 */
  cursor: pointer;
  /* 커서 포인터 변경 */
}

/* 아바타 스타일 */
.avatar {
  width: 40px;
  /* 너비 */
  height: 40px;
  /* 높이 */
  background-color: #e2e2e2;
  /* 배경색 회색 */
  border-radius: 50%;
  /* 원형 */
  display: flex;
  /* 플렉스 박스 레이아웃 사용 */
  align-items: center;
  /* 수직 정렬 */
  justify-content: center;
  /* 수평 정렬 */
  font-weight: bold;
  /* 글자 두께 */
  color: #666;
  /* 글자 색상 회색 */
}

/* 직원 정보 컨테이너 스타일 */
.employee-info {
  flex: 1;
  /* 남은 공간을 모두 차지 */
}

/* 직원 이름 스타일 */
.employee-name {
  font-size: 14px;
  /* 글자 크기 */
  font-weight: 500;
  /* 글자 두께 */
  color: #333;
  /* 글자 색상 어두운 회색 */
}

/* 직원 부서 스타일 */
.employee-userId {
  font-size: 12px;
  /* 글자 크기 */
  color: #666;
  /* 글자 색상 회색 */
  margin-top: 4px;
  /* 위 여백 */
}

/* 버튼 그룹 스타일 */
.button-group {
  display: flex;
  /* 플렉스 박스 레이아웃 사용 */
  justify-content: flex-end;
  /* 오른쪽으로 요소 정렬 */
  gap: 10px;
  /* 요소 간 간격 */
  margin-top: 20px;
  /* 위 여백 */
}

/* 취소 버튼 스타일 */
.cancel-button {
  padding: 8px 20px;
  /* 내부 여백 */
  border: none;
  /* 테두리 없음 */
  border-radius: 4px;
  /* 둥근 모서리 */
  background-color: #f0f0f0;
  /* 배경색 연한 회색 */
  color: #333;
  /* 글자 색상 어두운 회색 */
  cursor: pointer;
  /* 커서 포인터 변경 */
}

/* 저장 버튼 스타일 */
.save-button {
  padding: 8px 20px;
  /* 내부 여백 */
  border: none;
  /* 테두리 없음 */
  border-radius: 4px;
  /* 둥근 모서리 */
  background-color: #4a90e2;
  /* 배경색 파란색 */
  color: white;
  /* 글자 색상 흰색 */
  cursor: pointer;
  /* 커서 포인터 변경 */
}

/* 취소 버튼 호버 시 스타일 */
.cancel-button:hover {
  background-color: #e5e5e5;
  /* 호버 시 배경색 약간 진한 회색 */
}

/* 저장 버튼 호버 시 스타일 */
.save-button:hover {
  background-color: #357abd;
  /* 호버 시 배경색 더 어두운 파란색 */
}
</style>
