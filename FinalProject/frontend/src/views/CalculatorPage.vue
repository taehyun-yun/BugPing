<template>
  <div class="calculator-page">

    <!-- 급여 계산 영역 -->
    <div class="payroll-calculator">

      <div class="summary-header">
        <div class="summary-box">
          <div class="ellipse-background">
            <img src="../assets/CalculatorImg/users.png" alt="Group Icon" class="icon-group" />
          </div>
          <div class="summary-title">총 인원</div>
          <div class="summary-count">{{ totalEmployees }}</div>
        </div>

        <div class="separator-line"></div>

        <div class="summary-box">
          <div class="ellipse-background">
            <img src="../assets/CalculatorImg/user-check.png" alt="User Check Icon" class="icon-user-check" />
          </div>
          <div class="summary-title">지급</div>
          <div class="summary-count">{{ paidEmployees }}</div>
        </div>

        <div class="separator-line"></div>

        <div class="summary-box">
          <div class="ellipse-background">
            <img src="../assets/CalculatorImg/user-x.png" alt="User X Icon" class="icon-user-x" />
          </div>
          <div class="summary-title">미지급</div>
          <div class="summary-count">{{ unpaidEmployees }}</div>
        </div>
      </div>

      <h2 class="title">급여 계산</h2>
      <div class="search-sort-container">
        <div class="search-bar">
          <input v-model="searchQuery" type="text" placeholder="이름 또는 ID로 검색" class="search-input" />
        </div>
        <div class="sort-dropdown">
          <select v-model="sortOption" id="sort-select" class="sort-select">
            <option value="longest">오랜 기간 일한 순</option>
            <option value="shortest">가장 적게 일한 순</option>
            <option value="highestSalary">월급 많은 순</option>
            <option value="lowestSalary">월급 적은 순</option>
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

        <div v-for="employee in sortedEmployees" :key="employee.employeeId" class="payroll-row">
        <!-- <div v-for="employee in sortedEmployees" :key="employee.id" class="payroll-row"></div> -->
          <div 
            class="payroll-item" 
            @click="showModal(employee)" 
            @mouseenter="handleMouseEnter(employee.id)" 
            @mouseleave="handleMouseLeave"
            :class="{ 'hover': hoveredEmployeeId === employee.id }"
          >
            {{ employee.name }} ({{ employee.employeeId }})
          </div>  
          <div class="payroll-item">{{ employee.startDate }}</div>
          <div class="payroll-item">{{ employee.hourlyWage }}</div>
          <div class="payroll-item">{{ isNaN(employee.monthlyHours) ? 0 : employee.monthlyHours }}</div>
          <div class="payroll-item">{{ employee.workDays || 0 }}</div>
          <div class="payroll-item">{{ employee.totalSalary || 0}}</div>
          <div class="payroll-item">
            <button @click="togglePaid(employee)" :class="employee.isPaid ? 'paid-button' : 'unpaid-button'">
              {{ employee.isPaid ? '지급' : '미지급' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!--명세서 모달-->
  <div v-if="isModalVisible && selectedEmployee" class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3 class="modal-title">{{ selectedEmployee.name }} 급여 명세서</h3>
        <button @click="closeModal" class="modal-close">✖</button>
      </div>
      <div class="modal-subtitle">
        <span>귀속월</span> &nbsp;&nbsp;&nbsp;<span> {{ currentMonth }} 월</span>
      </div>
      <div class="modal-payment-date">
        <span class="modal-label">지급 일자</span>
        <span class="modal-value">&nbsp;&nbsp;&nbsp; {{ selectedEmployee.paymentDate }} 지급</span>
        <span class="dotted-line"></span>
      </div>

      <div class="modal-received-amount">
        <span class="modal-label">실 수령액</span>
        <span class="modal-value">{{ selectedEmployee.totalSalary }} 원</span>
      </div>
      <hr class="bold-hr">

      <div class="modal-body">
        <div class="modal-section">
          <span class="modal-label">기본급</span>
          <span class="modal-value"> {{ selectedEmployee.basicSalary }} 원</span>
        </div>
        <hr />
        <div class="modal-section">
          <span class="modal-label">주휴 수당</span>
          <span class="modal-value"> {{ selectedEmployee.weeklyAllowance }} 원</span>
        </div>
        <hr />
        <div class="modal-section">
          <span class="modal-label">연장 수당</span>
          <span class="modal-value"> {{ selectedEmployee.overtimePay }} 원</span>
        </div>
        <hr />
        <div class="modal-section">
          <span class="modal-label">야간 수당</span>
          <span class="modal-value"> {{ selectedEmployee.nightPay }} 원</span>
        </div>
        <hr />
        <div class="modal-section">
          <span class="modal-label">공제액</span>
          <span class="modal-value"> {{ selectedEmployee.deduction }} 원</span>
        </div>
        <hr />
      </div>
    </div>
  </div>

  <!-- 페이지 네이션 관련 코드 -->
  <div class="pagination-container">
  <button @click="handlePageChange(currentPage - 1)" :disabled="currentPage === 1">&lt;</button>
  <button 
    v-for="page in totalPages" 
    :key="page" 
    @click="handlePageChange(page)" 
    :class="{ active: currentPage === page }"
  >
    {{ page }}
  </button>
  <button @click="handlePageChange(currentPage + 1)" :disabled="currentPage === totalPages">&gt;</button>
</div>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue';
import axios from 'axios';

const employees = ref([]);
const isModalVisible = ref(false);


// 검색어와 정렬 옵션
const searchQuery = ref(""); // 검색 키워드
const sortOption = ref("longest"); // 정렬 기본값: 오랜 기간 일한 순

// 검색된 직원 목록
const filteredEmployees = computed(() => {
  if (!searchQuery.value) {
    return employees.value; // 검색어가 없으면 전체 데이터 반환
  }
  return employees.value.filter((employee) => {
    const keyword = searchQuery.value.toLowerCase();
    return (
      employee.name.toLowerCase().includes(keyword) ||
      employee.employeeId.toLowerCase().includes(keyword)
    );
  });
});

// 정렬을 위한 totalSalary 계산 함수
const calculateTotalSalary = (employee) => {
  const basicSalary = employee.basicSalary || 0;
  const weeklyAllowance = employee.weeklyAllowance || 0;
  const nightPay = employee.nightPay || 0;
  const overtimePay = employee.overtimePay || 0;
  const deduction = employee.deduction || 0;

  return basicSalary + weeklyAllowance + nightPay + overtimePay - deduction;
};

// 정렬된 직원 목록
const sortedEmployees = computed(() => {
  const sortKey = sortOption.value;

  return filteredEmployees.value.slice().sort((a, b) => {
    switch (sortKey) {
      case "highestSalary":
        return b.totalSalary - a.totalSalary; // 월급 많은 순
      case "lowestSalary":
        return a.totalSalary - b.totalSalary; // 월급 적은 순
      default: // "longest"
        return b.totalSalary - a.totalSalary; // 월급 많은 순
    }
  });
});

const selectedEmployee = ref({
  userId: null,
  startDate: new Date().toISOString().slice(0, 7) + '-01', // 현재 월의 첫날
  endDate: new Date(new Date().getFullYear(), new Date().getMonth() + 1, 0)
    .toISOString()
    .slice(0, 10), // 현재 월의 마지막 날
});

// 시작 날짜 로그 확인
console.log("초기 시작 날짜:", selectedEmployee.value.startDate);

// 현재 달 계산
const currentMonth = computed(() => {
  const startDate = selectedEmployee.value.startDate || new Date().toISOString().slice(0, 7) + '-01'; // 기본값 설정

  const [year, month, day] = startDate.split('-').map(Number);
  return month; // 월 반환
});

const hoveredEmployeeId = ref(null); // hover 상태의 직원 ID를 저장

const currentPage = ref(1);
const totalPages = ref(1);
const pageSize = ref(5);

// 근무자 리스트 출력.
// const fetchEmployeeList = async () => {
//   try {
//     const response = await axios.get("http://localhost:8707/api/employees", {
//       withCredentials: true // 쿠키를 서버로 넘길 것 인가?
//     });
//     console.log("API 응답 데이터: ", response.data); // 로그로 응답 데이터 확인
//     employees.value = response.data; // API에서 가져온 데이터를 employee에 저장
//     console.log("====================", employees.value);
//   } catch (error) {
//     console.error("Error fetchEmployeeList : ", error);
//   }
// };

// 지급/미지급 계산
const totalEmployees = computed(() => employees.value.length); // 총 인원
const paidEmployees = computed(() => employees.value.filter(emp => emp.isPaid).length); // 지급 인원
const unpaidEmployees = computed(() => employees.value.filter(emp => !emp.isPaid).length); // 미지급 인원




// 페이징 처리 메서드
const fetchEmployeesWithPagination = async () => {
  try {
    const response = await axios.get("http://localhost:8707/api/employees/paging", {
      params: {
        page: currentPage.value - 1, // Spring은 0-base 페이지 인덱스 사용
        size: pageSize.value
      },
      withCredentials: true
    });
    
    console.log("중복 제거된 직원 리스트: ", response.data);

    employees.value = response.data.content; // 현재 페이지 직원 데이터 저장
    totalPages.value = response.data.totalPages; // 전체 페이지 수
  } catch (error) {
    console.error("페이징된 데이터 가져오기 실패: ", error);
  }
};

const handlePageChange = (page) => {
  if (page > 0 && page <= totalPages.value) {
    currentPage.value = page; // 현재 페이지 업데이트
    fetchEmployeesWithPagination(); // 새 페이지 데이터 요청
  }
};


// 지급 상태 여부
const togglePaid = async (employee) => {
  console.log("Employee Data:", employee);
  console.log("PayRoll ID:", employee.payRollId);

  try {
    // 서버에 PATCH 요청 전송
    await axios.patch(`http://localhost:8707/api/payroll/${employee.payRollId}/paid`, null, {
      withCredentials: true,
      params: {
        isPaid: !employee.isPaid,
      },
    });
    

    // 서버에서 최신 데이터 가져오기
    const response = await axios.get("http://localhost:8707/api/employees/paging", {
      params: { page: currentPage.value - 1, size: pageSize.value },
      withCredentials: true,
    });
    employees.value = response.data.content; // Vue 데이터 갱신

    // 데이터 재할당 (강제 갱신)
    employees.value = [...response.data.content]; 
    console.log("API 응답 데이터:", employees.value);
  
    // 서버 응답에 따른 상태 갱신
    employee.isPaid = !employee.isPaid;
    
    console.log(`지급 상태 업데이트 성공 - PayRoll ID: ${employee.payRollId}, 지급 상태: ${!employee.isPaid}`);
  } catch (error) {
    console.error("Error updating payroll status: ", error);
  }
};

// 초기 데이터 로드
onMounted(() => {
  fetchEmployeesWithPagination(); // 페이지네이션 데이터 불러오기
});

const showModal = async (employee) => {
  console.log("선택된 직원 데이터:", employee); // 데이터 확인

  try {
    // 선택된 직원 데이터를 직접 모달에 반영
    selectedEmployee.value = {
      name: employee.name,
      paymentDate: "2024-12-31", // 지급일
      basicSalary: employee.basicSalary || 0,
      weeklyAllowance: employee.weeklyAllowance || 0, // 주휴 수당
      overtimePay: employee.overtimePay || 0, // 연장 수당
      nightPay: employee.nightPay || 0, // 야간 수당
      deduction: employee.deduction || 0, // 공제액
      totalSalary: employee.totalSalary || 0, // 총 급여
    };

    // 모달 표시
    isModalVisible.value = true;
  } catch (error) {
    console.error("Error opening modal: ", error);
    alert("모달 데이터를 설정하는 중 문제가 발생했습니다.");
  }
};

const closeModal = () => {
  isModalVisible.value = false;
};

const handleMouseEnter = (employeeId) => {
  hoveredEmployeeId.value = employeeId;
};

const handleMouseLeave = () => {
  hoveredEmployeeId.value = null;
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
  background: linear-gradient(201.18deg, rgb(211 238 255) 0%, rgb(224 232 255) 100%);
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
  background: #f3f2f3;
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
  padding: 20px 0;
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

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 200;
}

.modal-content {
  background: #fff;
  padding: 40px;
  border-radius: 20px;
  width: 700px;
  max-width: 90%;
  position: relative;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.modal-title {
  font-family: "Poppins-SemiBold", sans-serif;
  font-size: 22px;
  font-weight: 600;
}

.modal-subtitle {
  font-family: "Poppins-Regular", sans-serif;
  font-size: 14px;
  color: #080808;
  margin-bottom: 10px;
  display: flex;
  padding-bottom: 20px;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
}

.modal-payment-date {
  display: flex;
  align-items: center;
  margin: 10px 0;
  font-family: "Poppins-Regular", sans-serif;
  font-size: 14px;
  color: #777; /* 작은 텍스트 스타일을 적용 */
  padding-bottom: 125px;
}

.modal-payment-date .dotted-line {
  flex-grow: 1;
  border-top: 1px dotted gray;
  height: 0;
  margin-left: 5px;
}

.modal-body {
  font-family: "Poppins-Regular", sans-serif;
  font-size: 14px;
}

.modal-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.modal-label {
  color: #292d32;
  font-weight: 500;
}

.modal-value {
  color: #292d32;
  position: relative;
}

.dotted-line {
  flex-grow: 1;
  border-top: 1px dotted gray;
  height: 0;
  margin-left: 5px;
}

.modal-body hr {
  border: none;
  border-top: 1px solid #8a9cb1;
  margin: 22px 0;
}

.modal-received-amount {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  font-size: 14px;
  font-weight: 500;
}

.bold-hr {
  border: none;
  border-top: 2px solid #000;
  margin: 15px 0;
  padding-bottom: 50px;
}
.payroll-item.hover {
  cursor: pointer;
}

.pagination-container {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 20px;
  margin-bottom: 20px; /* 하단에 추가 여백을 주어 중앙에 위치하도록 조정 */
  position: relative; /* 부모의 중앙을 유지하도록 설정 */
}
.pagination-button {
  padding: 8px 12px;
  border: none;
  background-color: #f3f3f3;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.pagination-button:hover {
  background-color: #e0e0e0;
}

.pagination-button.active {
  background-color: #6b3ef7;
  color: #fff;
}

.pagination-ellipsis {
  padding: 8px;
  color: #888;
}
</style>
