<template>
    <div class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <h2 class="text-xl font-bold mb-4">직원 선택</h2>
        <div class="mb-4">
          <div class="relative">
            <input
              v-model="searchQuery"
              @input="searchEmployees"
              type="text"
              placeholder="이름/사번/휴대폰 번호로 검색"
              class="w-full p-2 pl-8 border rounded"
            />
            <i class="fas fa-search absolute left-3 top-3 text-gray-400"></i>
          </div>
        </div>
  
        <div class="mb-4 flex justify-between items-center">
          <button class="text-sm text-gray-600">필터</button>
          <div>
            <button @click="resetSelection" class="text-sm text-gray-600 mr-2">초기화</button>
            <span class="text-sm">전체 {{ filteredEmployees.length }}</span>
          </div>
        </div>
  
        <div class="border rounded">
          <div class="p-3 border-b">
            <label class="flex items-center">
              <input type="checkbox" v-model="selectAllSubgroups" class="mr-2" />
              <span class="text-sm">하위그룹도 한번에 체크</span>
            </label>
          </div>
          <div v-for="employee in filteredEmployees" :key="employee.id" class="p-3 border-b flex items-center">
            <input type="checkbox" v-model="employee.selected" class="mr-3" />
            <div class="w-8 h-8 rounded-full bg-gray-300 mr-3"></div>
            <div>
              <div class="font-medium">{{ employee.name }}</div>
              <div class="text-sm text-gray-600">{{ employee.department }}</div>
            </div>
          </div>
        </div>
  
        <div class="mt-4 flex justify-end">
          <button @click="closeModal" class="px-4 py-2 bg-gray-200 rounded mr-2">취소</button>
          <button @click="saveSelection" class="px-4 py-2 bg-blue-500 text-white rounded">저장</button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, computed } from 'vue'
  
  const searchQuery = ref('')
  const selectAllSubgroups = ref(false)
  const employees = ref([
    { id: 1, name: '문준호', department: '준호컴퍼니', selected: false },
    { id: 2, name: '김영희', department: '인사팀', selected: false },
    { id: 3, name: '이철수', department: '개발팀', selected: false },
    // 더 많은 직원 데이터를 여기에 추가할 수 있습니다.
  ])
  
  const filteredEmployees = computed(() => {
    return employees.value.filter(employee => 
      employee.name.includes(searchQuery.value) ||
      employee.department.includes(searchQuery.value)
    )
  })
  
  const searchEmployees = () => {
    // 실시간 검색 결과는 computed 속성인 filteredEmployees에 의해 자동으로 처리됩니다.
  }
  
  const resetSelection = () => {
    employees.value.forEach(employee => employee.selected = false)
    selectAllSubgroups.value = false
  }
  
  const closeModal = () => {
    // 모달 닫기 로직
  }
  
  const saveSelection = () => {
    const selectedEmployees = employees.value.filter(employee => employee.selected)
    console.log('선택된 직원:', selectedEmployees)
    // 여기에 선택된 직원 저장 로직을 추가하세요
    closeModal()
  }
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
    padding: 2rem;
    border-radius: 0.5rem;
    width: 90%;
    max-width: 500px;
    max-height: 80vh;
    overflow-y: auto;
  }
  </style>