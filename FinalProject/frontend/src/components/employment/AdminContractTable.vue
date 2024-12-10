<template>
  <div class="table-container">
    <table class="employee-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>이름</th>
          <th>시급</th>
          <th>계약 시작</th>
          <th>계약 종료</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="contract in contracts" :key="contract.id">
        <td>{{ contract.work.user.userId }}</td>
        <td>{{ contract.work.user.name }}</td>
        <td>{{ contract.hourlyWage }}</td>
        <td>{{ contract.contractStart }}</td>
        <td>{{ contract.contractEnd }}</td>
        <td><button class="edit-button" @click="openModal(contract)">EDIT</button></td>
      </tr>
      </tbody>
    </table>
    <ContractModal 
      :is-open="showModal" 
      @close="showModal = false" 
      @save="handleSave" 
    />
  </div>
</template>

<script setup>
// axios를 사용하여 API 데이터 가져오기
import { ref, onMounted } from 'vue';
import axios from 'axios';

import ContractModal from '@/components/employment/ContractModal.vue';


// 계약 정보를 저장하는 변수
const contracts = ref([]);

const showModal = ref(false);
const selectedContract = ref(null);

// API로부터 계약 정보 가져오기ssss
const fetchContracts = async () => {
    try {
      const apiUrl = `${import.meta.env.VITE_API_URL}/api/contracts`;
      const response = await axios.get(apiUrl, {
        headers: { 'Content-Type': 'application/json' },
      }); // 모든 계약 정보 가져오는 엔드포인트 호출
      contracts.value = response.data;
    } catch (error) {
      console.error('계약 정보를 가져오는 데 실패했습니다:', error);
    }
  };



  
  const openModal = (contract) => {
  selectedContract.value = contract;
  showModal.value = true;
};

const handleSave = (updatedContract) => {
  // 수정된 계약 정보를 저장하는 로직
  console.log('Updated contract:', updatedContract);
  showModal.value = false;
};









// 컴포넌트가 마운트될 때 계약 정보를 가져옴
onMounted(() => {
  fetchContracts();
});
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
</style>
