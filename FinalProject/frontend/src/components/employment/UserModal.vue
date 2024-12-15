<!-- UserModal.vue -->

<template>
  <div v-if="isOpen" class="modal-overlay" @click.self="closeModal">
    <div class="modal-content"  @click.stop>
      <h2 class="title">직원 선택</h2>
      
      <div class="search-container">
        <input
          v-model="searchQuery"
          @input="searchEmployees"
          type="text"
          placeholder="이름/사번/휴대폰 번호로 검색"
          class="search-input"
        />
        <i class="search-icon"></i>
      </div>

      <div class="filter-section">
        <button class="filter-button">필터</button>
        <div class="filter-right">
          <button @click="resetSelection" class="reset-button">초기화</button>
          <span class="total-count">전체 {{ filteredEmployees.length }}</span>
        </div>
      </div>

      <div class="employee-container">
        <div class="checkbox-group">
          <label class="checkbox-label">
            <input type="checkbox" v-model="selectAllSubgroups" />
            <span>하위그룹도 한번에 체크</span>
          </label>
        </div>
        
        <div class="checkbox-group">
          <label class="checkbox-label">
            <input type="checkbox" v-model="showCurrentJobOnly" />
            <span>현재 일하는 직업만 조회</span>
          </label>
        </div>

        <div class="employee-list">
          <div v-for="employee in filteredEmployees" :key="employee.id" class="employee-item">
            <label class="employee-label">
              <input type="radio" v-model="employee.selected" />
              <div class="avatar">{{ employee.name.charAt(0) }}</div>
              <div class="employee-info">
                <div class="employee-name">{{ employee.name }}</div>
                <div class="employee-department">{{ employee.department }}</div>
              </div>
            </label>
          </div>
        </div>
      </div>

      <div class="button-group">
        <button @click="closeModal" class="cancel-button">취소</button>
        <button @click="saveSelection" class="save-button">저장</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

import { defineProps, defineEmits } from 'vue' 

const props = defineProps({
  isOpen: {
    type: Boolean,
    required: true,
    default: false
  }
})

const emit = defineEmits(['close', 'save']) // **emits 선언 추가**


const searchQuery = ref('')
const selectAllSubgroups = ref(false)
const showCurrentJobOnly = ref(false)
const employees = ref([
  { id: 1, name: '이철수', department: '개발팀', selected: false, currentJob: true },
  { id: 2, name: '박지민', department: '마케팅팀', selected: false, currentJob: true },
  { id: 3, name: '최수진', department: '디자인팀', selected: false, currentJob: false },
  { id: 4, name: '이철', department: '개발팀', selected: false, currentJob: true },
  { id: 5, name: '박민', department: '마케팅팀', selected: false, currentJob: true },
  { id: 6, name: '최진', department: '디자인팀', selected: false, currentJob: false },
])

const filteredEmployees = computed(() => {
  return employees.value.filter(employee => 
    (employee.name.includes(searchQuery.value) ||
    employee.department.includes(searchQuery.value)) &&
    (!showCurrentJobOnly.value || employee.currentJob)
  )
})

const searchEmployees = () => {
  // 실시간 검색 처리
}

const resetSelection = () => {
  employees.value.forEach(employee => employee.selected = false)
  selectAllSubgroups.value = false
  showCurrentJobOnly.value = false
}

const closeModal = () => {
  emit('close')
}
const saveSelection = () => {
  const selectedEmployees = employees.value.filter(employee => employee.selected)
  console.log('선택된 직원:', selectedEmployees)
  if (selectedEmployees.length > 0) {
    emit('save', selectedEmployees[0]) // **선택된 직원 emit**
  }
  closeModal()
}

//`selectAllSubgroups` 체크박스 동작 구현
// watch(selectAllSubgroups, (newVal) => {
//   if (newVal) {
//     filteredEmployees.value.forEach(employee => {
//       employee.selected = true
//     })
//   } else {
//     filteredEmployees.value.forEach(employee => {
//       employee.selected = false
//     })
//   }
// })
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  padding: 24px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
}

.search-container {
  position: relative;
  margin-bottom: 20px;
}

.search-input {
  width: 100%;
  padding: 10px 10px 10px 35px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.search-icon {
  position: absolute;
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
  width: 16px;
  height: 16px;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%23999'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z'%3E%3C/path%3E%3C/svg%3E");
}

.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.filter-button {
  color: #666;
  font-size: 14px;
  background: none;
  border: none;
  cursor: pointer;
}

.filter-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.reset-button {
  color: #666;
  font-size: 14px;
  background: none;
  border: none;
  cursor: pointer;
}

.total-count {
  font-size: 14px;
  color: #666;
}

.employee-container {
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
}

.checkbox-group {
  padding: 15px;
  border-bottom: 1px solid #ddd;
  background-color: #f8f8f8;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #333;
  cursor: pointer;
}

.employee-list {
  max-height: 300px;
  overflow-y: auto;
}

.employee-item {
  padding: 15px;
  border-bottom: 1px solid #ddd;
}

.employee-item:last-child {
  border-bottom: none;
}

.employee-label {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.avatar {
  width: 40px;
  height: 40px;
  background-color: #e2e2e2;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  color: #666;
}

.employee-info {
  flex: 1;
}

.employee-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.employee-department {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}

.button-group {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.cancel-button {
  padding: 8px 20px;
  border: none;
  border-radius: 4px;
  background-color: #f0f0f0;
  color: #333;
  cursor: pointer;
}

.save-button {
  padding: 8px 20px;
  border: none;
  border-radius: 4px;
  background-color: #4a90e2;
  color: white;
  cursor: pointer;
}

.cancel-button:hover {
  background-color: #e5e5e5;
}

.save-button:hover {
  background-color: #357abd;
}

/* 체크박스 커스텀 스타일 */
input[type="checkbox"] {
  width: 16px;
  height: 16px;
  margin: 0;
  cursor: pointer;
}

/* 스크롤바 스타일 */
.employee-list::-webkit-scrollbar {
  width: 6px;
}

.employee-list::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.employee-list::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 3px;
}

.employee-list::-webkit-scrollbar-thumb:hover {
  background: #999;
}
</style>