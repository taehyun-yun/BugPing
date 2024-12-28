<template>
  <div class="attendance-list-wrapper">
    <div v-if="attendanceList.length > 0" class="attendance-list">
      <div
        v-for="(record, index) in attendanceList"
        :key="index"
        class="attendance-item"
      >
        <div class="user-profile">
          <img
            :src="record.image || '/placeholder-avatar.png'"
            :alt="`${record.name}의 프로필`"
            class="profile-image"
          />
        </div>
        <div class="attendance-details">
          <span class="user-name">
            {{ record.name }}
            <button
              class="end-overtime-button"
              v-if="record.isOvertime"
              @click="endOvertime(index)"
            >
              추가 근무 종료
            </button>
          </span>
          <p class="attendance-message">
            {{ record.isOvertime
              ? `${renderTime(record.startTime)} ~ 추가 근무중`
              : `${renderTime(record.startTime)} ~ ${renderTime(record.endTime)}` }}
          </p>
        </div>
      </div>
    </div>
    <div v-else>
      <p class="empty-message">표시할 데이터가 없습니다.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from "vue";
import axios from "axios";

// Prop 선언
const props = defineProps(["overtimeUser"]);

// 데이터 초기화
const attendanceList = ref([]);

// 시간 포맷팅 함수
function extractTimeOnly(dateString) {
  if (!dateString) {
    return "Invalid Time"; // 유효하지 않은 데이터 처리
  }


  try {
    const date = new Date(dateString);
    const hours = String(date.getHours()).padStart(2, "0");
    const minutes = String(date.getMinutes()).padStart(2, "0");

    if (isNaN(date)) {
      return "Invalid Time";  // 날짜 형식 오류 처리
    }

    return `${hours}:${minutes}`;
  } catch (error) {
    return "Invalid Time";
  }
}

// 추가 근무 종료 처리
async function endOvertime(index) {
  ("attendanceList 값:", attendanceList.value);
  

  const now = new Date();
  attendanceList.value[index].isOvertime = false;
  attendanceList.value[index].endTime = now;
  ("actualEnd 값 확인:", attendanceList.value[index].startTime);

  const overtimeData = {
    attendanceId: attendanceList.value[index].attendanceId,
    userId: attendanceList.value[index].userId,
    startTime: attendanceList.value[index].startTime,
    endTime: now.toISOString(),
  };

  ("전송할 데이터:", overtimeData);

  try {
    const response = await sendOvertimeData(overtimeData);
    ("서버 응답:", response.data);
    alert("추가 근무 종료가 완료되었습니다.");
  } catch (error) {
    alert("추가 근무 종료에 실패했습니다.");
  }
}

// 서버로 데이터 전송 함수
async function sendOvertimeData(overtimeData) {
  // overtimeStart는 actualEnd로 설정되어 있음
  const overtimeStart = overtimeData.startTime; // 이미 actualEnd로 설정됨
  const overtimePayload = {
    attendanceId: overtimeData.attendanceId,
    overtimeStart: overtimeStart ? formatToLocalDateTime(overtimeStart) : null, // 유효한 값이면 변환
    overtimeEnd: formatToLocalDateTime(overtimeData.endTime), // 퇴근 시간 처리
  };

  ("전송 데이터 확인:", overtimePayload);

  // overtimeStart와 overtimeEnd 유효성 검사
  if (!overtimePayload.overtimeStart || !overtimePayload.overtimeEnd) {
    return;
  }

  try {
    const response = await axios.post(
      "http://localhost:8707/api/overtime",
      overtimePayload
    );
    ("try 안의 overtimePayload : ", overtimePayload);
    return response;
  } catch (error) {
    throw error;
  }
}

// 시간 포맷팅 함수
// 화면에 표시할 시간 포맷팅 함수
function renderTime(timeString) {
  if (!timeString || isNaN(new Date(timeString))) {
    return "Invalid Date";
  }
  try {
    const date = new Date(timeString);
    return date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
  } catch (error) {
    return "Invalid Date";
  }
}

// formatToLocalDateTime 변환 함수
function formatToLocalDateTime(dateString) {
  if (!dateString) {
    return null;
  }

  try {
    const date = new Date(dateString);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}T${String(date.getHours()).padStart(2, "0")}:${String(date.getMinutes()).padStart(2, "0")}:${String(date.getSeconds()).padStart(2, "0")}`;
  } catch (error) {
    return null;
  }
}

// overtimeUser Prop 변경 감지 및 업데이트
watch(
  () => props.overtimeUser,
  (newUser) => {
    ("watch에서 전달된 newUser:", newUser);
    
    // newUser 또는 actualEnd가 없을 경우 예외 처리
    if (!newUser || !newUser.actualEnd) {
      return;
    }

    // 유효한 경우만 데이터 추가
    const existingRecord = attendanceList.value.find(
      (record) => record.attendanceId === newUser.attendanceId
    );

    if (!existingRecord) {
      attendanceList.value.push({
        attendanceId: newUser.attendanceId,
        userId: newUser.userId,
        name: newUser.userName,
        startTime: newUser.actualEnd, // actualEnd 값을 startTime으로 사용
        endTime: null,
        image: "https://via.placeholder.com/150",
        isOvertime: true,
      });
    } else {
      console.warn("중복 데이터가 감지되어 추가되지 않음:", newUser);
    }

    ("attendanceList 상태:", attendanceList.value);
  },
  { immediate: true }
);

</script>

<style scoped>
.attendance-list-wrapper {
  overflow: hidden;
}

.attendance-list {
  height: 100%;
  overflow-x: hidden;
  overflow-y: auto;
  padding-top: 20px;
}

.attendance-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 0.75rem;
  margin-bottom: 1rem;
}

.attendance-item:last-child {
  margin-bottom: 0;
}

.user-profile {
  flex-shrink: 0;
}

.profile-image {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.attendance-details {
  flex: 1;
}

.user-name {
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 0.25rem;
  display: block;
}

.attendance-message {
  color: #718096;
  font-size: 0.875rem;
  margin: 0;
}

.end-overtime-button {
  margin-left: 10px;
  padding: 0.25rem 0.5rem;
  font-size: 0.75rem;
  font-weight: 600;
  color: #ffffff;
  background: #e53e3e;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.end-overtime-button:hover {
  background: #c53030;
}

.empty-message {
  color: #718096;
  font-size: 0.875rem;
  text-align: center;
  margin-top: 20px;
}

.selected-user-details {
  margin-top: 20px;
  padding: 1rem;
  background: #e2e8f0;
  border-radius: 0.75rem;
}
</style>
