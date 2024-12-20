<template>
  <div class="table-container">
    <table class="employee-table">
      <thead>
        <tr>
          <th>Name</th>
          <th>ID</th>
          <th>출근시간</th>
          <th>퇴근시간</th>
          <th>정상 출근 여부</th>
          <th>총 근무 시간</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="emp in employees" :key="emp.attendanceId">
          <td>{{ emp.userName }}</td>
          <td>{{ emp.userId }}</td>
          <td>{{ formatTime(emp.actualStart) }}</td>
          <td>{{ emp.actualEnd ? formatTime(emp.actualEnd) : "미출근" }}</td>
          <td>{{ determineAttendanceStatus(emp.actualStart) }}</td>
          <td> 계산 불가 </td>
          <td>
            <button class="edit-button" @click="handleClick(emp)">버튼</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";

const employees = ref([]);

onMounted(async () => {
  try {
    const response = await axios.get(
      "http://localhost:8707/api/attendances/attendancesList"
    );
    console.log("API Response:", response.data);
    employees.value = response.data;
  } catch (error) {
    console.error(error);
  }
});

// 출근 시간 및 퇴근 시간 포맷
function formatTime(dateString) {
  if (!dateString) return "미출근";
  const date = new Date(dateString);
  return date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
}

// 정상 출근 여부 계산
function determineAttendanceStatus(actualStart) {
  const now = new Date().getTime();
  if (!actualStart) return "미출근";

  const actualStartTime = new Date(actualStart).getTime();
  return actualStartTime <= now ? "근무중" : "미출근";
}

// // 총 근무 시간 계산
// function calculateTotalWorkMinutes(actualStart, actualEnd, dbTotalMinutes) {
//   // DB에서 가져온 값이 존재하고 0이 아닌 경우
//   if (dbTotalMinutes !== undefined && dbTotalMinutes > 0) {
//     return `${dbTotalMinutes} 분`;
//   }

//   // DB 값이 없거나 0일 경우 계산
//   if (!actualStart) return "미출근";

//   const start = new Date(actualStart).getTime();
//   const end = actualEnd ? new Date(actualEnd).getTime() : new Date().getTime();

//   const diffMilliseconds = end - start;
//   if (diffMilliseconds < 0) return "시간 오류";

//   const diffMinutes = Math.floor(diffMilliseconds / 60000);
//   return `${diffMinutes} 분`;
// }


function handleClick(emp) {
  console.log("Clicked on:", emp);
  // 추가 로직 작성
}
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
  
  .employee-info {
    display: flex;
    align-items: center;
    gap: 1rem;
  }
  
  .avatar {
    width: 45px;
    height: 45px;
    border-radius: 50%;
    object-fit: cover;
  }
  
  .employee-details {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
  }
  
  .employee-name {
    margin: 0;
    font-weight: 600;
    color: #333;
  }
  
  .employee-email {
    margin: 0;
    color: #666;
    font-size: 0.875rem;
  }
  
  .title {
    margin: 0;
    color: #333;
    font-weight: 500;
  }
  
  .department {
    margin: 0;
    color: #666;
    font-size: 0.875rem;
  }
  
  .status-badge {
    padding: 6px 12px;
    border-radius: 50px;
    font-size: 0.75rem;
    font-weight: 500;
  }
  
  .status-badge.active {
    background-color: #e8f5e9;
    color: #2e7d32;
  }
  
  .status-badge.onboarding {
    background-color: #e3f2fd;
    color: #1976d2;
  }
  
  .status-badge.awaiting {
    background-color: #fff3e0;
    color: #f57c00;
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