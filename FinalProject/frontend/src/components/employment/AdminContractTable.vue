<!-- AdminContractTable.vue -->
<template>
  <div class="table-container">
    <h1>계약 목록</h1>
    <div v-if="contractsStore.loading">로딩 중...</div>
    <div v-if="contractsStore.error" class="error">{{ contractsStore.error }}</div>

    <table class="employee-table" v-if="contractsStore.contracts.length">
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
        <template v-for="contract in contractsStore.contracts" :key="contract.contractId">
          <tr class="parent-row">
            <td>
              <button @click="toggleExpand(contract)" class="expand-button">
                {{ contract.expanded ? '▼' : '▶' }}
              </button>
            </td>
            <td>{{ contract.work.user.userId }}</td>
            <td>{{ contract.work.user.name }}</td>
            <td>{{ formatCurrency(contract.hourlyWage) }}</td>
            <td>{{ formatDate(contract.contractStart) }}</td>
            <td>{{ formatDate(contract.contractEnd) }}</td>
            <td><button class="edit-button" @click="openModal(contract)">수정</button></td>
          </tr>
          <!-- <template v-if="contract.expanded"> -->
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

    <!-- ContractModal 추가 -->
    <ContractModal :is-open="showModal" :contract="selectedContract" @close="closeModal" @save="updateContract" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useContractsStore } from '@/stores/contracts';
import ContractModal from '@/components/employment/ContractModal.vue';

// Pinia 스토어 사용
const contractsStore = useContractsStore();

// 모달 상태 관리
const showModal = ref(false);
const selectedContract = ref(null);

// 계약 목록 가져오기
onMounted(() => {
  contractsStore.fetchContracts();
});

// // 확장 상태 토글
// const toggleExpand = (contract) => {
//   contract.expanded = !contract.expanded;
// };
const expandedContracts = ref([]);

// 확장 상태 토글
const toggleExpand = (contract) => {
  const index = expandedContracts.value.indexOf(contract.contractId);
  if (index === -1) {
    expandedContracts.value.push(contract.contractId);
  } else {
    expandedContracts.value.splice(index, 1);
  }
  console.log('Expanded Contracts:', expandedContracts.value);
};


// 확장 상태 확인
const isExpanded = (contract) => expandedContracts.value.includes(contract.contractId);


// 수정 모달 열기
const openModal = (contract) => {
  selectedContract.value = contract;
  showModal.value = true;
};

// 수정 모달 닫기
const closeModal = () => {
  showModal.value = false;
};

// 금액 포맷팅
const formatCurrency = (value) => {
  return new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW' }).format(value);
};

// 날짜 포맷팅
const formatDate = (dateString) => { 
  return dateString ? new Date(dateString).toLocaleDateString('ko-KR') : '날짜 없음';
  //return new Date(dateString).toLocaleDateString('ko-KR');
};

// 시간 포맷팅
const formatTime = (timeString) => {
  return timeString;
};

// 분 단위 시간을 "시간 분"으로 포맷팅
const formatDuration = (minutes) => {
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  return `${hours}시간 ${mins}분`;
};

// 요일 정보 가져오기 - getDayName 함수 (배열(Array) 기반 매핑)
const getDayName = (dayNumber) => {
  const days = ['월', '화', '수', '목', '금', '토', '일'];
  return days[dayNumber - 1] || '요일 정보 없음';
};

// 계약 업데이트 처리
const updateContract = (updatedContract) => {
  console.log('Updating contract:', updatedContract);
  const index = contractsStore.contracts.findIndex((c) => c.contractId === updatedContract.contractId);
  if (index !== -1) {
    contractsStore.contracts[index] = { ...contractsStore.contracts[index], ...updatedContract };
    console.log('Updated Contract:', contractsStore.contracts[index]);
  } else {
    console.error('Contract not found for update:', updatedContract.contractId);
  }
};



</script>

<style scoped>
.table-container {
  padding: 1rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.employee-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
}

.employee-table th {
  padding: 12px 16px;
  text-align: left;
  background-color: #f8f9fa;
  color: #333;
  font-weight: 500;
  font-size: 0.875rem;
}

.employee-table td {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  vertical-align: middle;
}

.edit-button {
  padding: 6px 12px;
  border: none;
  background: none;
  color: #1976d2;
  font-weight: 600;
  cursor: pointer;
  font-size: 0.875rem;
}

.edit-button:hover {
  text-decoration: underline;
}

.expand-button {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  color: #666;
}

.parent-row {
  background-color: #ffffff;
}

.child-row {
  background-color: #f9f9f9;
}

.schedule-info {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  font-size: 0.85rem;
}

.schedule-info span {
  margin-right: 16px;
}

.schedule-info strong {
  color: #555;
}

.error {
  color: red;
}
</style>
