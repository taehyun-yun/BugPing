<template>
  <div class="table-container">
    <table class="employee-table">
      <thead>
        <tr>
          <th>Name</th>
          <th>ID</th>
          <th>출근시간</th>
          <th>퇴근시간</th>
          <th>근무 여부</th>
          <th>총 근무 시간</th>
          <th>초과 근무</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="emp in employees" :key="emp.userId">
          <td>{{ emp.userName }}</td>
          <td>{{ emp.userId }}</td>
          <td>{{ formatTime(emp.actualStart) }}</td>
          <td>{{ formatTime(emp.actualEnd) }}</td>
          <td>{{ emp.commuteStatus }}</td> <!-- 근무 여부 -->
          <td>{{ calculateTotalWorkMinutes(emp.actualStart, emp.actualEnd) }}</td>
          <td><button @click="handleOvertimeClick(emp)">추가 근무 시작</button></td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { axiosAddress } from "@/stores/axiosAddress";
import { useUserStore } from "@/stores/userStore";

// 회사 ID 가져오기
const userStore = useUserStore();
const companyId = userStore.company.companyId;

// 스케줄과 출근 데이터 저장
const employees = ref([]);
const schedules = ref([]);
const attendances = ref([]);

// 이벤트 정의
const emit = defineEmits(["overtime-click"]);

onMounted(async () => {
  try {
    // 스케줄 데이터 가져오기
    const scheduleResponse = await axios.get(`${axiosAddress}/api/attendances/schedulesList`, {
      params: { companyId },
    });
    schedules.value = scheduleResponse.data;

    // 출근 데이터 가져오기
    const attendanceResponse = await axios.get(`${axiosAddress}/api/today`, {
      params: { companyId },
    });
    attendances.value = attendanceResponse.data;

    ("출근 데이터:", attendances.value); // attendanceId 필드 확인
    // 데이터 병합
    mergeScheduleAndAttendance();
  } catch (error) {
    console.error("데이터 가져오기 실패:", error);
  }
});

// 스케줄과 출근 데이터 병합
function mergeScheduleAndAttendance() {
  const mergedData = schedules.value.map(schedule => {
    (`스케줄 데이터: ${schedule.userId}`);

    // userId를 기준으로 출근 데이터 찾기 (대소문자 무시)
    const matchedAttendance = attendances.value.find(
      attendance => attendance.userId.toLowerCase() === schedule.userId.toLowerCase()
    );

    (
      `Comparing User ID: ${schedule.userId} with Attendance User ID: ${
        matchedAttendance?.userId || 'undefined'
      }`
    );

    ("스케줄 데이터:", schedule);
    ("매칭된 출근 데이터:", matchedAttendance);

    return {
      attendanceId: matchedAttendance?.attendanceId || null, // 추가
      userId: schedule.userId,
      userName: schedule.userName,
      officialStart: schedule.officialStart,
      officialEnd: schedule.officialEnd,
      actualStart: matchedAttendance?.actualStart || null,
      actualEnd: matchedAttendance?.actualEnd || null,
      commuteStatus: determineAttendanceStatus(matchedAttendance, schedule),
      totalMinute: matchedAttendance?.totalMinute || 0,
    };
  });

  ('병합된 데이터:', mergedData);
  employees.value = mergedData;
}



// 근무 상태 계산
function determineAttendanceStatus(attendance, schedule) {
  const now = new Date();
  const officialStart = schedule.officialStart
    ? new Date(1970, 0, 1, ...schedule.officialStart.split(":"))
    : null;

  if (!attendance) {
    if (officialStart && now < officialStart) return "출근 전";
    return "미출근";
  }

  if (attendance.actualEnd) return "퇴근 완료";
  if (attendance.actualStart) return "근무중";
  return "미출근";
}

// 시간 포맷 함수
function formatTime(timeString) {
  if (!timeString) return ""; // 데이터가 없으면 빈 문자열 반환
  try {
    const date = new Date(timeString);
    if (isNaN(date)) {
      console.error("Invalid date format:", timeString);
      return "Invalid Date";
    }
    return date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
  } catch (error) {
    console.error("시간 포맷 오류:", error, timeString);
    return "Invalid Date";
  }
}


// 총 근무 시간 계산
function calculateTotalWorkMinutes(actualStart, actualEnd) {
  if (!actualStart) return "계산 불가";

  const start = new Date(actualStart).getTime();
  const end = actualEnd ? new Date(actualEnd).getTime() : new Date().getTime();
  const diffMilliseconds = end - start;
  if (diffMilliseconds < 0) return "시간 오류";

  const diffMinutes = Math.floor(diffMilliseconds / 60000);
  return `${diffMinutes} 분`;
}

// 초과 근무 시간 계산
function calculateOvertimeMinutes(emp) {
  try {
    const officialStart = new Date(`1970-01-01T${emp.officialStart}`).getTime();
    const officialEnd = new Date(`1970-01-01T${emp.officialEnd}`).getTime();
    const officialMinutes = Math.floor((officialEnd - officialStart) / 60000);

    if (!emp.actualStart || !emp.actualEnd) return "0 분"; // 출근/퇴근 시간이 없으면 0
    const actualStart = new Date(emp.actualStart).getTime();
    const actualEnd = new Date(emp.actualEnd).getTime();
    const actualMinutes = Math.floor((actualEnd - actualStart) / 60000);

    const overtime = actualMinutes - officialMinutes;
    return `${overtime > 0 ? overtime : 0} 분`;
  } catch (error) {
    console.error("초과 근무 시간 계산 오류:", error, emp);
    return "계산 오류";
  }
}

function handleOvertimeClick(emp) {

  ("넘겨받은 데이터:", emp);

  emit("overtime-click", {
    attendanceId: emp.attendanceId, // 수정: 필드명 일관성 유지
    userId: emp.userId,
    userName: emp.userName,
    actualEnd: emp.actualEnd,
  });

  ("넘겨주는 데이터:", {
    attendanceId: emp.attendanceId,
    userId: emp.userId,
    userName: emp.userName,
    actualEnd: emp.actualEnd,
  });
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