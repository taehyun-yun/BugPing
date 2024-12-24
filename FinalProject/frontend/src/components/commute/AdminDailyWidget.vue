<template>
  <div class="status-container">
    <h2 class="status-title">출퇴근 현황</h2>

    <div class="status-grid">
      <!-- 출근율 카드 -->
      <div class="status-card attendance-rate">
        <svg class="progress-ring" width="120" height="120">
          <circle
            class="progress-ring__circle--bg"
            stroke="#CCCCCC"
            stroke-width="8"
            fill="transparent"
            r="52"
            cx="60"
            cy="60"
          />
          <circle
            class="progress-ring__circle"
            :style="circleStyle"
            stroke="#0044CC"
            stroke-width="8"
            fill="transparent"
            r="52"
            cx="60"
            cy="60"
          />
        </svg>
        <div class="circle-content">
          <div class="label">출근율</div>
          <div class="percentage">{{ attendanceRate.toFixed(0) }}%</div>
          <div class="count">
            <span class="icon">👤</span> {{ totalAttended }} / {{ totalScheduled }}
          </div>
        </div>
      </div>

      <!-- 출근/휴무 통계 카드 -->
      <div class="status-card attendance-stats">
        <!-- 출근/휴무 -->
        <div class="stat-row">
          <div class="stat-group">
            <div class="stat-label">출근</div>
            <div class="stat-bar">
              <div class="bar-track">
                <div class="bar-progress" :style="{ width: `${attendedPercentage}%` }"></div>
              </div>
              <div class="stat-value">{{ totalAttended }}</div>
            </div>
          </div>
          <div class="stat-group">
            <div class="stat-comparison">휴무</div>
            <div class="bar-track">
              <div class="bar-progress red" :style="{ width: `${onLeavePercentage}%` }"></div>
            </div>
            <div class="stat-value red">{{ onLeave }}</div>
          </div>
        </div>

        <!-- 출근 전/미출근 행 -->
        <div class="stat-row">
          <div class="stat-group">
            <div class="stat-label">출근 전</div>
            <div class="stat-bar">
              <div class="bar-track">
                <div class="bar-progress" :style="{ width: `${notYetStartedPercentage}%` }"></div>
              </div>
              <div class="stat-value">{{ notYetStarted }}</div>
            </div>
          </div>
          <div class="stat-group">
            <div class="stat-comparison">추가 근무자</div>
            <div class="bar-track">
              <div class="bar-progress red" :style="{ width: `${extraWorkPercentage}%` }"></div>
            </div>
            <div class="stat-value red">{{ extraWork }}</div>
          </div>
        </div>

        <div class="employee-count">
          <span class="icon">👥</span>
          출근 대상
          <span class="count">{{ totalScheduled }}</span>
        </div>
      </div>

      <!-- 지각/조퇴 카드들 -->
      <div class="status-card late-card">
        <div class="status-label">지각</div>
        <div class="status-value">-</div>
      </div>

      <div class="status-card early-leave-card">
        <div class="status-label">조퇴</div>
        <div class="status-value">-</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';
import { axiosAddress } from '@/stores/axiosAddress';
import { useUserStore } from '@/stores/userStore';

const userStore = useUserStore();
const companyId = userStore.company.companyId;

const attendanceRate = ref(0);
const totalScheduled = ref(0);
const totalAttended = ref(0);
const onLeave = ref(0);
const notYetStarted = ref(0);
const extraWork = ref(0);

// 원형 그래프 계산
const radius = 52;
const circumference = 2 * Math.PI * radius;
const circleStyle = computed(() => {
  const offset = circumference * (1 - attendanceRate.value / 100);
  return {
    strokeDasharray: `${circumference} ${circumference}`,
    strokeDashoffset: offset,
  };
});

const attendedPercentage = computed(() =>
  totalScheduled.value > 0 ? (totalAttended.value / totalScheduled.value) * 100 : 0
);
const onLeavePercentage = computed(() =>
  totalScheduled.value > 0 ? (onLeave.value / totalScheduled.value) * 100 : 0
);
const notYetStartedPercentage = computed(() =>
  totalScheduled.value > 0 ? (notYetStarted.value / totalScheduled.value) * 100 : 0
);
const extraWorkPercentage = computed(() =>
  totalScheduled.value > 0 ? (extraWork.value / totalScheduled.value) * 100 : 0
);

async function fetchAttendanceStatistics() {
  try {
    const response = await axios.get(`${axiosAddress}/api/today/attendance-statistics`, {
      params: { companyId },
    });
    const data = response.data;
    attendanceRate.value = data.attendanceRate || 0;
    totalScheduled.value = data.totalScheduled || 0;
    totalAttended.value = data.attended || 0;
    onLeave.value = data.onLeave || 0;
    notYetStarted.value = data.notYetStarted || 0;
    extraWork.value = data.extraWork || 0;
  } catch (error) {
    console.error('Error fetching attendance statistics:', error);
  }
}

onMounted(fetchAttendanceStatistics);
</script>

<style scoped>
.status-container {
  padding: 20px;
  max-width: 720px;
  margin: 0 auto;
}

.status-title {
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 12px;
}

.status-grid {
  display: grid;
  grid-template-columns: 45fr 55fr 20fr;
  grid-template-rows: auto auto;
  gap: 10px;
  align-items: stretch;
}

.status-card {
  background: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  font-size: 110%;
}

.attendance-rate {
  background: #2196f3;
  color: white;
  display: flex;
  flex-direction: column; /* 세로로 정렬 */
  align-items: center; /* 가로 가운데 정렬 */
  justify-content: center; /* 세로 가운데 정렬 */
  aspect-ratio: 1;
  grid-column: 1 / 2;
  grid-row: 1 / 3;
  min-height: 100px;
  position: relative; /* 자식의 절대 위치를 기준으로 설정 */
}

.progress-ring {
  position: relative; /* 절대 위치 대신 상대 위치를 사용 */
  width: 120px;
  height: 120px;
}

.progress-ring__circle--bg {
  stroke-dasharray: 327.2;
  stroke-dashoffset: 0;
}

.progress-ring__circle {
  transition: stroke-dashoffset 0.5s ease;
  stroke-dasharray: 327.2;
  stroke-dashoffset: 327.2;
}

.circle-content {
  position: absolute; /* 텍스트를 SVG 위에 배치 */
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}

.label {
  font-size: 12px;
  font-weight: bold;
}

.percentage {
  font-size: 16px;
  font-weight: bold;
  margin-top: 4px;
}

.count {
  font-size: 10px;
  margin-top: 4px;
  opacity: 0.9;
}

.attendance-stats {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 6px;
  gap: 6px;
  grid-column: 2 / 3;
  grid-row: 1 / 3;
  min-height: 100px;
}

.stat-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  border-bottom: 1px solid #eee;
  padding-bottom: 12px;
  margin-bottom: 12px;
}

.stat-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-label,
.stat-comparison {
  font-weight: 500;
}

.stat-label {
  font-size: 20px;
}

.stat-comparison {
  color: #ff5252;
  font-size: 20px;
}

.bar-track {
  height: 8px;
  background: #f5f5f5;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 4px;
}

.bar-progress {
  height: 100%;
  background: #2196f3;
  width: 0;
  transition: width 0.5s ease-in-out;
}

.bar-progress.red {
  background: #ff5252;
}

.stat-value {
  color: #2196f3;
  font-weight: 500;
  font-size: 10px;
}

.employee-count {
  display: flex;
  align-items: center;
  gap: 4px;
  padding-top: 6px;
  border-top: 1px solid #eee;
  color: #666;
  font-size: 16px;
}

.employee-count .count {
  margin-left: auto;
  font-weight: 500;
}

.late-card,
.early-leave-card {
  aspect-ratio: 1;
  min-height: 50px;
  gap: 6px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.late-card {
  background: #4caf50;
  color: white;
  grid-column: 3 / 4;
  grid-row: 1 / 2;
}

.early-leave-card {
  background: #ffc107;
  color: white;
  grid-column: 3 / 4;
  grid-row: 2 / 3;
}

.status-label {
  font-size: 27px;
}

.status-value {
  font-size: 16px;
  font-weight: bold;
}

.icon {
  font-size: 8px;
}
</style>
