<!-- AdminContractTable.vue -->

<template>
  <div class="table-container">
    <!-- 헤더 영역: 제목, 날짜 필터, 계약 생성 버튼 -->
    <div class="header-container">
      <h1 class="title">계약 목록</h1>
      <div class="date-filter">
        <!-- 날짜 필터: 사용자가 날짜를 선택하면 계약 목록을 필터링 -->
        <!-- <input type="date" v-model="filterDate" @change="filterContracts" /> -->

        <!-- 연도 선택 드롭다운 -->
        <select v-model="selectedYear" @change="handleYearChange">
        <option value="">전체 연도</option>
        <option v-for="year in availableYears" :key="year" :value="year">{{ year }}</option>
      </select>

      <select v-model="selectedMonth" :disabled="!selectedYear" @change="filterContracts">
        <option value="">전체 월</option>
        <option v-for="month in availableMonths" :key="month" :value="month">{{ month }}</option>
      </select>

      </div>
      <button @click="openCreateModal" class="create-button">
        <i class="fas fa-plus"></i>
        계약 생성하기
      </button>
    </div>

    <!-- 로딩 상태 표시: 데이터 로딩 중일 때 보여짐 -->
    <div v-if="contractsStore.loading" class="loading">로딩 중...</div>
    <!-- 오류 메시지 표시: 데이터 로딩 또는 처리 중 오류 발생 시 보여짐 -->
    <div v-if="contractsStore.error" class="error">{{ contractsStore.error }}</div>

    <!-- 계약 목록 테이블: 필터된 계약이 있을 때만 표시 -->
    <table class="employee-table" v-if="filteredContracts.length">
      <thead>
        <tr>
          <th></th>
          <th>ID</th>
          <th>이름</th>
          <th>시급</th>
          <th>계약 시작</th>
          <th>계약 종료</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <!-- 각 계약을 반복하여 표시 -->
        <template v-for="contract in filteredContracts" :key="contract.contractId">
          <!-- 부모 행: 계약 정보 표시 -->
          <tr class="parent-row">
            <td>
              <!-- 확장/축소 버튼: 클릭 시 스케줄 정보 표시 여부 토글 -->
              <button @click="toggleExpand(contract)" class="expand-button">
                {{ isExpanded(contract) ? '▼' : '▶' }}
              </button>
            </td>
            <!-- 계약 ID -->
            <td>{{ contract.work.user.userId }}</td>
            <!-- 계약자의 이름 -->
            <td>{{ contract.work.user.name }}</td>
            <!-- 시급을 통화 형식으로 표시 -->
            <td>{{ formatCurrency(contract.hourlyWage) }}</td>
            <!-- 계약 시작 날짜를 포맷하여 표시 -->
            <td>{{ formatDate(contract.contractStart) }}</td>
            <!-- 계약 종료 날짜를 포맷하여 표시 -->
            <td>{{ formatDate(contract.contractEnd) }}</td>
            <td>
              <!-- 계약 수정 버튼: 클릭 시 수정 모달 열기 -->
              <button class="edit-button" @click="openModal(contract)">수정</button>
              <!-- 계약 삭제 버튼: 클릭 시 계약 삭제 -->
              <button class="delete-button" @click="deleteContract(contract)">삭제</button>
            </td>
          </tr>
          <!-- 자식 행: 계약의 스케줄 정보 표시 (확장된 경우) -->
          <template v-if="isExpanded(contract)">
            <tr v-for="schedule in contract.schedules" :key="schedule.id" class="child-row">
              <td></td>
              <td colspan="6">
                <div class="schedule-info">
                  <span><strong>요일:</strong> {{ getDayName(schedule.day) }}</span>
                  <span><strong>시작 시간:</strong> {{ formatTime(schedule.officialStart) }}</span>
                  <span><strong>종료 시간:</strong> {{ formatTime(schedule.officialEnd) }}</span>
                  <span><strong>휴게 시간:</strong> {{ formatDuration(schedule.breakMinute) }}</span>
                </div>
              </td>
            </tr>
          </template>
        </template>
      </tbody>
    </table>

    <!-- 계약 생성/수정 모달 컴포넌트 -->
    <ContractModal
      :is-open="showModal"
      :contract="selectedContract"
      @close="closeModal"
      @save="updateContracts"
    />
  </div>
</template>

<script setup>
/* 
  Vue.js 3의 <script setup> 구문을 사용하여 컴포넌트 로직을 정의합니다.
  Pinia 스토어와 컴포넌트를 가져옵니다.
*/
import { ref, computed, onMounted } from 'vue'; // Vue의 반응성 API
import { useContractsStore } from '@/stores/contracts'; // Pinia 스토어
import ContractModal from '@/components/employment/ContractModal.vue'; // 계약 모달 컴포넌트

/* 
  Pinia 스토어 인스턴스를 가져옵니다. 
  이 스토어는 계약 데이터와 관련된 상태와 액션을 관리합니다.
*/
const contractsStore = useContractsStore();

/* 
  모달 창의 표시 여부를 관리하는 반응형 변수.
  true면 모달이 열려 있고, false면 닫혀 있습니다.
*/
const showModal = ref(false);

/* 
  선택된 계약을 저장하는 반응형 변수.
  사용자가 계약을 수정할 때, 해당 계약의 데이터를 모달에 전달합니다.
*/
const selectedContract = ref(null);

/* 
  확장된 계약의 ID를 저장하는 배열.
  사용자가 특정 계약의 스케줄을 보기 위해 행을 확장했는지 여부를 추적합니다.
*/
const expandedContracts = ref([]);


/* 
  컴포넌트가 마운트될 때 계약 데이터를 가져옵니다.
  onMounted는 Vue의 라이프사이클 훅 중 하나로, 컴포넌트가 마운트된 후에 실행됩니다.
*/
onMounted(() => {
  contractsStore.fetchContracts(); // Pinia 스토어의 fetchContracts 액션 호출
});

/* 
  날짜 필터링에 사용되는 날짜 값을 저장하는 반응형 변수.
  기본값은 오늘 날짜로 설정됩니다.
*/
// const filterDate = ref(new Date().toISOString().split('T')[0]);

// 선택된 연도와 월
const selectedYear = ref(''); // 초기값: 전체 연도
const selectedMonth = ref(''); // 초기값: 전체 월

// 필터링 가능한 연도와 월
const availableYears = computed(() => {
  if (!contractsStore.contracts.length) return [];
  
  // 가장 오래된 시작 연도와 가장 최신 종료 연도 계산
  const earliestYear = Math.min(...contractsStore.contracts.map(contract =>
    new Date(contract.contractStart).getFullYear()
  ));
  const latestYear = Math.max(...contractsStore.contracts.map(contract =>
    new Date(contract.contractEnd).getFullYear()
  ));

  return Array.from({ length: latestYear - earliestYear + 1 }, (_, i) => (earliestYear + i).toString());
});

const availableMonths = Array.from({ length: 12 }, (_, i) => (i + 1).toString().padStart(2, '0'));

// 필터링된 계약 목록 계산
const filteredContracts = computed(() => {
  // 전체 조회
  if (!selectedYear.value) return contractsStore.contracts;

  // 특정 연도 조회
  if (selectedYear.value && !selectedMonth.value) {
    return contractsStore.contracts.filter(contract =>
      new Date(contract.contractStart).getFullYear() <= parseInt(selectedYear.value) &&
      new Date(contract.contractEnd).getFullYear() >= parseInt(selectedYear.value)
    );
  }

  // 특정 연월 조회
  if (selectedYear.value && selectedMonth.value) {
    const startDate = new Date(`${selectedYear.value}-${selectedMonth.value}-01`);
    const endDate = new Date(
      new Date(startDate.getFullYear(), startDate.getMonth() + 1, 0).toISOString()
    );

    return contractsStore.contracts.filter(contract =>
      new Date(contract.contractStart) <= endDate &&
      new Date(contract.contractEnd) >= startDate
    );
  }

  return contractsStore.contracts;
});

// 연도 변경 시 월 초기화 및 필터링
const handleYearChange = () => {
  selectedMonth.value = ''; // 연도 변경 시 월 초기화
  filterContracts();
};

// 필터 변경 시 호출
const filterContracts = () => {
  console.log("필터링: ", selectedYear.value, selectedMonth.value);
};


/* 
  계약 행의 확장 상태를 토글하는 함수.
  확장된 계약 목록에 계약 ID를 추가하거나 제거합니다.
*/
const toggleExpand = (contract) => {
  const index = expandedContracts.value.indexOf(contract.contractId);
  if (index === -1) {
    // 계약이 확장되지 않은 상태라면 확장 목록에 추가
    expandedContracts.value.push(contract.contractId);
  } else {
    // 계약이 이미 확장된 상태라면 확장 목록에서 제거
    expandedContracts.value.splice(index, 1);
  }
};

/* 
  특정 계약이 확장되었는지 여부를 확인하는 함수.
  확장된 계약 목록에 계약 ID가 포함되어 있으면 true를 반환합니다.
*/
const isExpanded = (contract) => expandedContracts.value.includes(contract.contractId);

/* 
  계약 생성 모달을 여는 함수.
  선택된 계약을 null로 설정하고, 모달을 표시합니다.
*/
const openCreateModal = () => {
  selectedContract.value = null; // 새로운 계약을 생성하기 때문에 선택된 계약을 null로 설정
  showModal.value = true; // 모달을 열기 위해 showModal을 true로 설정
};

/* 
  계약 수정 모달을 여는 함수.
  선택된 계약을 설정하고, 모달을 표시합니다.
*/
const openModal = (contract) => {
  selectedContract.value = contract; // 수정할 계약을 설정
  showModal.value = true; // 모달을 열기 위해 showModal을 true로 설정
};

/* 
  모달을 닫는 함수.
  showModal을 false로 설정하여 모달을 닫습니다.
*/
const closeModal = () => {
  showModal.value = false; // 모달을 닫기 위해 showModal을 false로 설정
};

/* 
  시급 값을 통화 형식으로 포맷하는 함수.
  예: 25000 -> "25,000 KRW"
*/
const formatCurrency = (value) => {
  return new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW' }).format(value);
};

/* 
  날짜 문자열을 읽기 쉬운 형식으로 포맷하는 함수.
  예: "2024-12-17T00:00:00Z" -> "2024. 12. 17."
*/
const formatDate = (dateString) => {
  return dateString ? new Date(dateString).toLocaleDateString('ko-KR') : '날짜 없음';
};

/* 
  시간 문자열을 포맷하는 함수.
  현재는 단순히 시간을 반환하지만, 필요에 따라 추가 포맷팅을 할 수 있습니다.
*/
const formatTime = (timeString) => {
  return timeString;
};

/* 
  휴게 시간을 시와 분으로 포맷하는 함수.
  예: 90 -> "1시간 30분"
*/
const formatDuration = (minutes) => {
  const hours = Math.floor(minutes / 60); // 전체 시 계산
  const mins = minutes % 60; // 남은 분 계산
  return `${hours}시간 ${mins}분`;
};

/* 
  요일 번호를 요일 이름으로 변환하는 함수.
  예: 1 -> "월", 2 -> "화", ..., 7 -> "일"
*/
const getDayName = (dayNumber) => {
  const days = ['월', '화', '수', '목', '금', '토', '일'];
  return days[dayNumber - 1] || '요일 정보 없음';
};

const updateContracts = (updatedContract) => {
  const index = contractsStore.contracts.findIndex(
    (c) => c.contractId === updatedContract.contractId
  );
  if (index !== -1) {
    // 기존 계약 업데이트
    contractsStore.contracts[index] = { ...contractsStore.contracts[index], ...updatedContract };
  } else {
    // 새 계약 추가
    contractsStore.contracts.push(updatedContract);
  }
};

const deleteContract = async (contract) => {
  if (!contract.contractId) {
    console.error("유효하지 않은 계약 ID입니다:", contract);
    alert("삭제할 수 없는 계약입니다.");
    return;
  }

  if (confirm("정말로 이 계약을 삭제하시겠습니까?")) {
    try {
      await contractsStore.deleteContract(contract.contractId);
      alert("계약이 성공적으로 삭제되었습니다.");
    } catch (error) {
      console.error("계약 삭제 중 오류 발생:", error);
      alert("계약 삭제에 실패했습니다. 다시 시도해주세요.");
    }
  }
};

</script>

<style scoped>
/* 
  컴포넌트에만 적용되는 스타일을 정의합니다.
  scoped 속성을 사용하여 스타일이 전역으로 적용되지 않도록 합니다.
*/

/* 테이블 컨테이너 스타일 */
.table-container {
  padding: 2rem; /* 내부 여백 */
  background: white; /* 배경색 흰색 */
  border-radius: 8px; /* 모서리 둥글게 */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); /* 그림자 효과 */
}

/* 헤더 컨테이너 스타일: 제목, 필터, 버튼을 가로로 배치 */
.header-container {
  display: flex; /* 플렉스 박스 레이아웃 사용 */
  justify-content: space-between; /* 양 끝으로 요소 배치 */
  align-items: center; /* 수직 정렬 */
  margin-bottom: 2rem; /* 아래 여백 */
}

/* 제목 스타일 */
.title {
  font-size: 2rem; /* 글자 크기 */
  font-weight: 700; /* 글자 두께 */
  color: #2c3e50; /* 글자 색상 */
}

/* 날짜 필터 컨테이너 스타일 */
.date-filter {
  display: flex; /* 플렉스 박스 레이아웃 사용 */
  align-items: center; /* 수직 정렬 */
}

/* 날짜 필터 입력 필드 스타일 */
.date-filter input {
  padding: 0.5rem; /* 내부 여백 */
  border: 1px solid #ddd; /* 테두리 */
  border-radius: 4px; /* 모서리 둥글게 */
  font-size: 1rem; /* 글자 크기 */
}

/* 계약 생성 버튼 스타일 */
.create-button {
  display: flex; /* 플렉스 박스 레이아웃 사용 */
  align-items: center; /* 수직 정렬 */
  padding: 0.75rem 1.5rem; /* 내부 여백 */
  background-color: #4CAF50; /* 배경색 초록색 */
  color: white; /* 글자 색상 흰색 */
  border: none; /* 테두리 없음 */
  border-radius: 4px; /* 모서리 둥글게 */
  cursor: pointer; /* 커서 포인터 변경 */
  font-size: 1rem; /* 글자 크기 */
  transition: background-color 0.2s ease-in-out; /* 배경색 전환 효과 */
}

/* 계약 생성 버튼 호버 스타일 */
.create-button:hover {
  background-color: #45a049; /* 호버 시 배경색 변경 */
}

/* 계약 생성 버튼 아이콘 마진 */
.create-button i {
  margin-right: 0.5rem; /* 오른쪽 여백 */
}

/* 직원 테이블 스타일 */
.employee-table {
  width: 100%; /* 너비 100% */
  border-collapse: separate; /* 테두리 분리 */
  border-spacing: 0; /* 테두리 간격 없음 */
  background: white; /* 배경색 흰색 */
  border-radius: 8px; /* 모서리 둥글게 */
  overflow: hidden; /* 넘치는 내용 숨김 */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05); /* 그림자 효과 */
}

/* 테이블 헤더 셀 스타일 */
.employee-table th {
  padding: 1rem; /* 내부 여백 */
  text-align: left; /* 왼쪽 정렬 */
  background-color: #f8f9fa; /* 배경색 회색빛 */
  color: #2c3e50; /* 글자 색상 */
  font-weight: 600; /* 글자 두께 */
  font-size: 0.9rem; /* 글자 크기 */
  text-transform: uppercase; /* 대문자 변환 */
}

/* 테이블 데이터 셀 스타일 */
.employee-table td {
  padding: 1rem; /* 내부 여백 */
  border-bottom: 1px solid #f0f0f0; /* 하단 테두리 */
  vertical-align: middle; /* 수직 정렬 중간 */
}

/* 수정 및 삭제 버튼 스타일 */
.edit-button, .delete-button {
  padding: 0.5rem 1rem; /* 내부 여백 */
  border: none; /* 테두리 없음 */
  border-radius: 4px; /* 모서리 둥글게 */
  cursor: pointer; /* 커서 포인터 변경 */
  font-size: 0.875rem; /* 글자 크기 */
  transition: background-color 0.2s ease-in-out; /* 배경색 전환 효과 */
  margin-right: 0.5rem; /* 오른쪽 여백 */
}

/* 수정 버튼 스타일 */
.edit-button {
  background-color: #3498db; /* 배경색 파란색 */
  color: white; /* 글자 색상 흰색 */
}

/* 수정 버튼 호버 스타일 */
.edit-button:hover {
  background-color: #2980b9; /* 호버 시 배경색 변경 */
}

/* 삭제 버튼 스타일 */
.delete-button {
  background-color: #e74c3c; /* 배경색 빨간색 */
  color: white; /* 글자 색상 흰색 */
}

/* 삭제 버튼 호버 스타일 */
.delete-button:hover {
  background-color: #c0392b; /* 호버 시 배경색 변경 */
}

/* 확장 버튼 스타일 */
.expand-button {
  background: none; /* 배경 없음 */
  border: none; /* 테두리 없음 */
  cursor: pointer; /* 커서 포인터 변경 */
  font-size: 1rem; /* 글자 크기 */
  color: #7f8c8d; /* 글자 색상 회색 */
  transition: color 0.2s ease-in-out; /* 글자 색상 전환 효과 */
}

/* 확장 버튼 호버 스타일 */
.expand-button:hover {
  color: #34495e; /* 호버 시 글자 색상 변경 */
}

/* 부모 행 스타일 */
.parent-row {
  background-color: #ffffff; /* 배경색 흰색 */
  transition: background-color 0.2s ease-in-out; /* 배경색 전환 효과 */
}

/* 부모 행 호버 스타일 */
.parent-row:hover {
  background-color: #f5f5f5; /* 호버 시 배경색 변경 */
}

/* 자식 행 스타일 */
.child-row {
  background-color: #f9f9f9; /* 배경색 회색빛 */
}

/* 스케줄 정보 스타일 */
.schedule-info {
  display: flex; /* 플렉스 박스 레이아웃 사용 */
  justify-content: space-between; /* 양 끝으로 요소 배치 */
  padding: 0.5rem 0; /* 위아래 여백 */
  font-size: 0.85rem; /* 글자 크기 */
}

/* 스케줄 정보 내 개별 항목 스타일 */
.schedule-info span {
  margin-right: 1rem; /* 오른쪽 여백 */
}

/* 스케줄 정보 내 굵은 글씨 스타일 */
.schedule-info strong {
  color: #34495e; /* 글자 색상 어두운 회색 */
}

/* 로딩 및 오류 메시지 스타일 */
.loading, .error {
  text-align: center; /* 가운데 정렬 */
  padding: 1rem; /* 내부 여백 */
  font-size: 1rem; /* 글자 크기 */
}

/* 로딩 메시지 색상 */
.loading {
  color: #3498db; /* 파란색 */
}

/* 오류 메시지 색상 */
.error {
  color: #e74c3c; /* 빨간색 */
}
.date-filter {
  display: flex;
  gap: 1rem;
}
/* 드롭다운 기본 스타일 */
select {
  padding: 10px 14px; /* 내부 여백 */
  font-size: 1rem; /* 글자 크기 */
  border: 1px solid #ccc; /* 기본 테두리 */
  border-radius: 6px; /* 모서리 둥글게 */
  background-color: #f8f9fa; /* 배경 색상 */
  color: #333; /* 글자 색상 */
  cursor: pointer; /* 커서 변경 */
  transition: all 0.2s ease-in-out; /* 전환 효과 */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 그림자 효과 */
}

/* 드롭다운 호버 상태 */
select:hover {
  border-color: #007bff; /* 테두리 파란색으로 변경 */
  background-color: #ffffff; /* 배경 흰색으로 변경 */
  box-shadow: 0 4px 8px rgba(0, 123, 255, 0.2); /* 더 강한 그림자 효과 */
}

/* 드롭다운 포커스 상태 */
select:focus {
  outline: none; /* 기본 아웃라인 제거 */
  border-color: #0056b3; /* 포커스 시 테두리 어두운 파란색 */
  box-shadow: 0 0 6px rgba(0, 86, 179, 0.5); /* 포커스 그림자 */
}

/* 드롭다운 비활성화 상태 */
select:disabled {
  background-color: #e9ecef; /* 비활성화 배경 */
  color: #6c757d; /* 비활성화 글자 색상 */
  cursor: not-allowed; /* 커서 변경 */
}


</style>
