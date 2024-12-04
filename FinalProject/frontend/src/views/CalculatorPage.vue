<template>
  <div class="calculator-page">
    <!-- 사이드바 컴포지트 -->
    <MainSidebar />
    <MainHeader />

    <!-- 급여 계산 영역 -->
    <div class="payroll-calculator">

      <div class="summary-header">
        <div class="summary-box">
          <div class="ellipse-background">
            <img src="../assets/CalculatorImg/users.png" alt="Group Icon" class="icon-group" />
          </div>
          <div class="summary-title">총 인원</div>
          <div class="summary-count">18</div>
        </div>

        <div class="separator-line"></div>

        <div class="summary-box">
          <div class="ellipse-background">
            <img src="../assets/CalculatorImg/user-check.png" alt="User Check Icon" class="icon-user-check" />
          </div>
          <div class="summary-title">지급</div>
          <div class="summary-count">13</div>
        </div>

        <div class="separator-line"></div>

        <div class="summary-box">
          <div class="ellipse-background">
            <img src="../assets/CalculatorImg/user-x.png" alt="User X Icon" class="icon-user-x" />
          </div>
          <div class="summary-title">미지급</div>
          <div class="summary-count">5</div>
        </div>
      </div>

      <h2 class="title">급여 계산</h2>
      <div class="search-sort-container">
        <div class="search-bar">
          <!-- <img src="../assets/CalculatorImg/search.png" alt="Search Icon" class="icon-search" /> -->
          <input type="text" placeholder="Search" class="search-input" />
        </div>
        <div class="sort-dropdown">
          <label for="sort-select">Sort by:</label>
          <select id="sort-select" class="sort-select">
            <option value="newest">Newest</option>
            <option value="oldest">Oldest</option>
            <option value="name">Name</option>
          </select>
        </div>
      </div>

      <div class="payroll-container">
        <div class="payroll-header">
          <div class="payroll-header-item">이름</div>
          <div class="payroll-header-item">정산 시작일</div>
          <div class="payroll-header-item">시급</div>
          <div class="payroll-header-item">월 근무시간</div>
          <div class="payroll-header-item">월 근무일수</div>
          <div class="payroll-header-item">급여</div>
          <div class="payroll-header-item">지급</div>
        </div>

        <div v-for="employee in employees" :key="employee.id" class="payroll-row">
          <div class="payroll-item">{{ employee.name }}</div>
          <div class="payroll-item">{{ employee.startDate }}</div>
          <div class="payroll-item">{{ employee.hourlyWage }}</div>
          <div class="payroll-item">{{ employee.monthlyHours }}</div>
          <div class="payroll-item">{{ employee.workDays }}</div>
          <div class="payroll-item">{{ employee.salary }}</div>
          <div class="payroll-item">
            <button @click="togglePaid(employee)" :class="employee.paid ? 'paid-button' : 'unpaid-button'">
              {{ employee.paid ? '지급' : '미지급' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import MainSidebar from '@/components/MainSidebar.vue';
import MainHeader from '@/components/MainHeader.vue';
import { ref } from 'vue';

const employees = ref([
  { id: 1, name: '윤태현', startDate: '2024/10/3', hourlyWage: '12,000', monthlyHours: 60, workDays: 12, salary: '907,599', paid: false },
  { id: 2, name: '유지훈', startDate: '2024/10/3', hourlyWage: '12,000', monthlyHours: 60, workDays: 12, salary: '907,599', paid: false },
  { id: 3, name: '장종현', startDate: '2024/10/3', hourlyWage: '12,000', monthlyHours: 60, workDays: 12, salary: '907,599', paid: false },
  { id: 4, name: '문준호', startDate: '2024/10/3', hourlyWage: '12,000', monthlyHours: 60, workDays: 12, salary: '907,599', paid: true },
  { id: 5, name: '임은선', startDate: '2024/10/9', hourlyWage: '12,000', monthlyHours: 42, workDays: 9, salary: '504,000', paid: true },
  { id: 6, name: '배영재', startDate: '2024/10/9', hourlyWage: '9,860', monthlyHours: 42, workDays: 9, salary: '504,000', paid: true },
  { id: 7, name: '이경진', startDate: '2024/10/21', hourlyWage: '9,860', monthlyHours: 36, workDays: 6, salary: '354,960', paid: false },
]);

const togglePaid = (employee) => {
  employee.paid = !employee.paid;
};
</script>

<style scoped>
:root {
  background-color: #ffffff;
  color-scheme: light;
}

.calculator-page {
  display: flex;
}

.payroll-calculator {
  flex: 1;
  padding: 20px;
}

.summary-header {
  display: flex;
  align-items: center;
  justify-content: space-around;
  background: white;
  padding: 20px;
  border-radius: 30px;
  box-shadow: 0 10px 30px rgba(226, 236, 249, 0.5);
  margin-bottom: 20px;
}

.summary-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}

.ellipse-background {
  background: linear-gradient(201.18deg, rgba(211, 255, 231, 1) 0%, rgba(239, 255, 246, 1) 100%);
  border-radius: 50%;
  width: 90.68px;
  height: 86.23px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
}

.users,
.user-check,
.user-x {
  width: 40px;
  height: 40px;
}

.summary-title {
  font-family: "Poppins-Regular", sans-serif;
  font-size: 14px;
  color: #acacac;
  margin-bottom: 5px;
}

.summary-count {
  font-family: "Poppins-SemiBold", sans-serif;
  font-size: 32px;
  font-weight: 600;
  color: #333;
}

.separator-line {
  width: 1px;
  height: 80px;
  background-color: #f0f0f0;
  margin: 0 20px;
}

.title {
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 20px;
}

.search-sort-container {
  display: flex;
  align-items: center;
  justify-content: flex-end; /* 왼쪽 정렬 */
  margin-bottom: 20px;
}

.search-bar {
  display: flex;
  align-items: center;
  margin-right: 20px; /* Sort By와 검색 박스 사이 간격 */
}

.search-input {
  padding: 5px;
  font-size: 12px;
  color: #b5b7c0;
  margin-right: 10px;
}

.icon-search {
  width: 18px;
  height: 18px;
}

.sort-dropdown {
  display: flex;
  align-items: center;
}

.sort-select {
  padding: 5px;
  font-size: 12px;
  color: #292d32;
  margin-left: 10px;
}

.payroll-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.payroll-header {
  display: flex;
  border-bottom: 1px solid #eeeeee;
  padding-bottom: 10px;
  margin-bottom: 10px;
}

.payroll-header-item {
  flex: 1;
  font-weight: 500;
  font-size: 14px;
  color: #b5b7c0;
}

.payroll-row {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid #eeeeee;
}

.payroll-item {
  flex: 1;
  font-size: 14px;
  color: #292d32;
}

.paid-button {
  padding: 5px 10px;
  background-color: #00b087;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.unpaid-button {
  padding: 5px 10px;
  background-color: #ff6b6b;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
</style>
