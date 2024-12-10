<template>
  <div class="table-container">
    <table class="employee-table">
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
        <template v-for="contract in contracts" :key="contract.id">
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
          <template v-if="contract.expanded">
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
    <ContractModal :is-open="showModal" :contract="selectedContract" @close="showModal = false" @save="handleSave" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import ContractModal from '@/components/employment/ContractModal.vue';

const contracts = ref([]);
const showModal = ref(false);
const selectedContract = ref(null);

// 계약 정보 가져오기
const fetchContracts = async () => {
  try {
    const apiUrl = `${import.meta.env.VITE_API_URL}/api/contracts`;
    const response = await axios.get(apiUrl, {
      headers: { 'Content-Type': 'application/json' },
    });
    contracts.value = await Promise.all(
      response.data.map(async (contract) => {
        // [추가된 부분] 스케줄 데이터를 동적으로 가져오기
        const schedules = await fetchSchedules(contract.contractId);
        return {
          ...contract,
          expanded: false, // 확장 상태 초기화
          schedules, // API로 가져온 스케줄 데이터를 추가
        };
      })
    );
  } catch (error) {
    console.error('계약 정보를 가져오는 데 실패했습니다:', error);
  }
};

// [추가된 함수] 스케줄 데이터 가져오기
const fetchSchedules = async (contractId) => {
  try {
    const apiUrl = `${import.meta.env.VITE_API_URL}/api/contracts/${contractId}/schedules`;
    const response = await axios.get(apiUrl, {
      headers: { 'Content-Type': 'application/json' },
    });
    return response.data; // 스케줄 리스트 반환
  } catch (error) {
    console.error(`스케줄 정보를 가져오는 데 실패했습니다: 계약 ID ${contractId}`, error);
    return []; // 실패 시 빈 배열 반환
  }
};

// 확장 상태 토글
const toggleExpand = (contract) => {
  contract.expanded = !contract.expanded;
};

// 수정 모달 열기
const openModal = (contract) => {
  selectedContract.value = contract;
  showModal.value = true;
};

// 수정 저장 처리
const handleSave = (updatedContract) => {
  console.log('Updated contract:', updatedContract);
  showModal.value = false;
};

// 금액 포맷팅
const formatCurrency = (value) => {
  return new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW' }).format(value);
};

// 날짜 포맷팅
const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('ko-KR');
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

// 요일 이름 가져오기
const getDayName = (dayNumber) => {
  const days = ['일', '월', '화', '수', '목', '금', '토'];
  return days[dayNumber - 1];
};

// 컴포넌트가 마운트될 때 데이터 가져오기
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
</style>
