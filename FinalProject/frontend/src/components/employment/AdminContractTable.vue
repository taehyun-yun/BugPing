<!-- AdminContractTable.vue -->

<template>
  <div class="table-container">
    <div class="header-container">
      <h1 class="title">계약 목록</h1>
      <div class="date-filter">
        <input type="date" v-model="filterDate" @change="filterContracts" />
      </div>
      <button @click="openCreateModal" class="create-button">
        <i class="fas fa-plus"></i>
        계약 생성하기
      </button>
    </div>

    <div v-if="contractsStore.loading" class="loading">로딩 중...</div>
    <div v-if="contractsStore.error" class="error">{{ contractsStore.error }}</div>

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
        <template v-for="contract in filteredContracts" :key="contract.contractId">
          <tr class="parent-row">
            <td>
              <button @click="toggleExpand(contract)" class="expand-button">
                {{ isExpanded(contract) ? '▼' : '▶' }}
              </button>
            </td>
            <td>{{ contract.work.user.userId }}</td>
            <td>{{ contract.work.user.name }}</td>
            <td>{{ formatCurrency(contract.hourlyWage) }}</td>
            <td>{{ formatDate(contract.contractStart) }}</td>
            <td>{{ formatDate(contract.contractEnd) }}</td>
            <td>
              <button class="edit-button" @click="openModal(contract)">수정</button>
              <button class="delete-button" @click="deleteContract(contract)">삭제</button>
            </td>
          </tr>
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

    <ContractModal :is-open="showModal" :contract="selectedContract" @close="closeModal" @save="updateContract" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useContractsStore } from '@/stores/contracts';
import ContractModal from '@/components/employment/ContractModal.vue';

const contractsStore = useContractsStore();
const showModal = ref(false);
const selectedContract = ref(null);
const expandedContracts = ref([]);
const filterDate = ref(new Date().toISOString().split('T')[0]);

onMounted(() => {
  contractsStore.fetchContracts();
});

const filteredContracts = computed(() => {
  if (!filterDate.value) return contractsStore.contracts;
  return contractsStore.contracts.filter(contract => 
    new Date(contract.contractStart) <= new Date(filterDate.value) &&
    new Date(contract.contractEnd) >= new Date(filterDate.value)
  );
});

const filterContracts = () => {
  // This function is called when the date filter changes
  // The filtering is handled by the computed property 'filteredContracts'
};

const toggleExpand = (contract) => {
  const index = expandedContracts.value.indexOf(contract.contractId);
  if (index === -1) {
    expandedContracts.value.push(contract.contractId);
  } else {
    expandedContracts.value.splice(index, 1);
  }
};

const isExpanded = (contract) => expandedContracts.value.includes(contract.contractId);

const openCreateModal = () => {
  selectedContract.value = null;
  showModal.value = true;
};

const openModal = (contract) => {
  selectedContract.value = contract;
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
};

const formatCurrency = (value) => {
  return new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW' }).format(value);
};

const formatDate = (dateString) => {
  return dateString ? new Date(dateString).toLocaleDateString('ko-KR') : '날짜 없음';
};

const formatTime = (timeString) => {
  return timeString;
};

const formatDuration = (minutes) => {
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  return `${hours}시간 ${mins}분`;
};

const getDayName = (dayNumber) => {
  const days = ['월', '화', '수', '목', '금', '토', '일'];
  return days[dayNumber - 1] || '요일 정보 없음';
};

const updateContract = (updatedContract) => {
  const index = contractsStore.contracts.findIndex((c) => c.contractId === updatedContract.contractId);
  if (index !== -1) {
    contractsStore.contracts[index] = { ...contractsStore.contracts[index], ...updatedContract };
  } else {
    console.error('Contract not found for update:', updatedContract.contractId);
  }
};

const deleteContract = (contract) => {
  if (confirm('정말로 이 계약을 삭제하시겠습니까?')) {
    // Here you would typically call an API to delete the contract
    // For now, we'll just remove it from the local array
    const index = contractsStore.contracts.findIndex((c) => c.contractId === contract.contractId);
    if (index !== -1) {
      contractsStore.contracts.splice(index, 1);
    }
  }
};
</script>

<style scoped>
.table-container {
  padding: 2rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.title {
  font-size: 2rem;
  font-weight: 700;
  color: #2c3e50;
}

.date-filter {
  display: flex;
  align-items: center;
}

.date-filter input {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.create-button {
  display: flex;
  align-items: center;
  padding: 0.75rem 1.5rem;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.2s ease-in-out;
}

.create-button:hover {
  background-color: #45a049;
}

.create-button i {
  margin-right: 0.5rem;
}

.employee-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.employee-table th {
  padding: 1rem;
  text-align: left;
  background-color: #f8f9fa;
  color: #2c3e50;
  font-weight: 600;
  font-size: 0.9rem;
  text-transform: uppercase;
}

.employee-table td {
  padding: 1rem;
  border-bottom: 1px solid #f0f0f0;
  vertical-align: middle;
}

.edit-button, .delete-button {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.875rem;
  transition: background-color 0.2s ease-in-out;
  margin-right: 0.5rem;
}

.edit-button {
  background-color: #3498db;
  color: white;
}

.edit-button:hover {
  background-color: #2980b9;
}

.delete-button {
  background-color: #e74c3c;
  color: white;
}

.delete-button:hover {
  background-color: #c0392b;
}

.expand-button {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  color: #7f8c8d;
  transition: color 0.2s ease-in-out;
}

.expand-button:hover {
  color: #34495e;
}

.parent-row {
  background-color: #ffffff;
  transition: background-color 0.2s ease-in-out;
}

.parent-row:hover {
  background-color: #f5f5f5;
}

.child-row {
  background-color: #f9f9f9;
}

.schedule-info {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem 0;
  font-size: 0.85rem;
}

.schedule-info span {
  margin-right: 1rem;
}

.schedule-info strong {
  color: #34495e;
}

.loading, .error {
  text-align: center;
  padding: 1rem;
  font-size: 1rem;
}

.loading {
  color: #3498db;
}

.error {
  color: #e74c3c;
}
</style>