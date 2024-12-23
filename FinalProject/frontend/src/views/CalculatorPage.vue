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
            <option value="longest">정산 오래된 순</option>
            <option value="shortest">정산 최근 순</option>
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
        <span>귀속월</span> &nbsp;&nbsp;&nbsp;<span> {{ currentMonth }}월</span>
      </div>
      <div class="modal-payment-date">
        <span class="modal-label">지급 일자</span>
        <span class="modal-value">&nbsp;&nbsp;&nbsp; {{ selectedEmployee.paymentDate }}</span>
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
</template>

<script setup>
import { onMounted, ref, computed } from "vue";
import axios from "axios";
import { axiosAddress } from "@/stores/axiosAddress";
import { useUserStore } from "@/stores/userStore";

const employees = ref([]);
const searchQuery = ref("");
const sortOption = ref("longest");
const isModalVisible = ref(false);
const selectedEmployee = ref(null);
const hoveredEmployeeId = ref(null);
const userStore = useUserStore();  // pinia에 저장된 user정보 불러오기

const totalEmployees = computed(() => employees.value.length);
const paidEmployees = computed(() => employees.value.filter(emp => emp.isPaid).length);
const unpaidEmployees = computed(() => employees.value.filter(emp => !emp.isPaid).length);

// PayRoll 데이터 생성
const generatePayroll = async() => {
  try {
    const response = await axios.post(`${axiosAddress}/api/generate`, {}, { withCredentials: true });
    console.log(response.data);
  } catch (error) {
    console.error("payroll 데이터 생성 싱패 : ", error)
  }
}



// 직원 데이터 가져오기
const fetchEmployees = async () => {
  try {
    const response = await axios.get(`${axiosAddress}/api/employees`, {
      params: {
        companyId: userStore.company.companyId,
      },
    });

    // 응답 데이터에서 employees 키 추출
    if (response.data && Array.isArray(response.data.employees)) {
      employees.value = response.data.employees; // employees 리스트 설정
    } else {
      console.error("응답 데이터가 올바르지 않습니다:", response.data);
      employees.value = []; // 오류 발생 시 빈 배열로 초기화
    }
  } catch (error) {
    console.error("데이터 로드 실패:", error);
    employees.value = []; // 오류 발생 시 빈 배열로 초기화
  }
};


// 검색 및 정렬된 직원 목록
const filteredEmployees = computed(() => {
  const keyword = searchQuery.value.toLowerCase();
  return employees.value.filter(employee =>
    employee.name.toLowerCase().includes(keyword) ||
    employee.employeeId.toLowerCase().includes(keyword)
  );
});

const sortedEmployees = computed(() => {
  const sortKey = sortOption.value;
  return filteredEmployees.value.slice().sort((a, b) => {
    switch (sortKey) {
      case "highestSalary":
        return b.totalSalary - a.totalSalary;
      case "lowestSalary":
        return a.totalSalary - b.totalSalary;
      case "longest":
        return new Date(a.startDate) - new Date(b.startDate);
      case "shortest":
        return new Date(b.startDate) - new Date(a.startDate);
      default:
        return 0;
    }
  });
});

// 지급 상태 변경
const togglePaid = async (employee) => {
  try {
    await axios.patch(`${axiosAddress}/api/payroll/${employee.payRollId}/paid`, null, {
      params: { isPaid: !employee.isPaid },
      withCredentials: true
    });
    employee.isPaid = !employee.isPaid;
  } catch (error) {
    console.error("지급 상태 변경 실패:", error);
    alert("지급 상태 변경에 실패했습니다. 다시 시도해주세요."); // 사용자 알림
  }
};

const currentMonth = computed(() => new Date().getMonth() + 1);

// 모달 표시
const showModal = (employee) => {
  selectedEmployee.value = { 
    ...employee,
    paymentDate: new Date().toISOString().split('T')[0] // 지급일자 추가
  };
  isModalVisible.value = true;
};

const closeModal = () => {
  isModalVisible.value = false;
};

// 마우스 엔터 이벤트 핸들러
function handleMouseEnter(employeeId) {
  hoveredEmployeeId.value = employeeId;
}
// 마우스 리브 이벤트 핸들러
function handleMouseLeave() {
  hoveredEmployeeId.value = null;
}

//급여 페이지 진입 시 호출
onMounted(async () => {
  await generatePayroll(); // PayRoll 데이터 생성
  await fetchEmployees();  // 생성된 데이터 로드
});
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
