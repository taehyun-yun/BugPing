<template>
  <div class="status-container">
    <h2 class="status-title">출퇴근 현황</h2>

    <div class="status-grid">
      <!-- 출근율 카드 -->
      <div class="status-card attendance-rate">
        <div class="circle-progress">
          <div class="circle-content">
            <div class="label">출근율</div>
            <div class="percentage">{{ attendanceRate.toFixed(0) }}%</div>
            <div class="count">
              <span class="icon">👤</span> {{ totalAttended }} / {{ totalScheduled }}
            </div>
          </div>
        </div>
      </div>

      <!-- 출근/휴무 통계 카드 -->
      <div class="status-card attendance-stats">
        <!-- 출근/휴무 행 -->
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
              <div class="bar-progress red" :style="{ width: `${30}%` }"></div>
            </div>
            <div class="stat-value red">{{ 3 }}</div>
          </div>
        </div>

        <!-- 출근 전/미출근 행 -->
        <div class="stat-row">
          <div class="stat-group">
            <div class="stat-label">출근 전</div>
            <div class="stat-bar">
              <div class="bar-track">
                <div class="bar-progress" :style="{ width: `${extraWorkPercentage}%` }"></div>
              </div>
              <div class="stat-value">{{ extraWork }}</div>
            </div>
          </div>
          <div class="stat-group">
            <div class="stat-comparison">추가 근무자</div>
            <div class="bar-track">
              <div class="bar-progress red" :style="{ width: `${0}%` }"></div>
            </div>
            <div class="stat-value red">{{ 0 }}</div>
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
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { axiosAddress } from '@/stores/axiosAddress';

// 반응형 변수 정의
const attendanceRate = ref(0); // 출근율
const totalScheduled = ref(0);
const totalAttended = ref(0);
const onLeave = ref(0);
const notAttended = ref(0);
const extraWork = ref(0);

// 계산 속성
const attendedPercentage = computed(() =>
  totalScheduled.value > 0 ? (totalAttended.value / totalScheduled.value) * 100 : 0
);
const onLeavePercentage = computed(() =>
  totalScheduled.value > 0 ? (onLeave.value / totalScheduled.value) * 100 : 0
);
const notAttendedPercentage = computed(() =>
  totalScheduled.value > 0 ? (notAttended.value / totalScheduled.value) * 100 : 0
);
const extraWorkPercentage = computed(() =>
  totalScheduled.value > 0 ? (extraWork.value / totalScheduled.value) * 100 : 0
);

// 데이터 로드 함수
async function fetchAttendanceStatistics() {
  try {
    const response = await axios.get(`${axiosAddress}/api/today/attendance-statistics`);
    const data = response.data;

    // 상태 업데이트
    attendanceRate.value = data.attendanceRate || 0;
    totalScheduled.value = data.totalScheduled || 0;
    totalAttended.value = data.attended || 0;
    onLeave.value = data.onLeave || 0;
    notAttended.value = data.notAttended || 0;
    extraWork.value = data.extraWork || 0;
  } catch (error) {
    console.error('Error fetching attendance statistics:', error);
  }
}

// 컴포넌트 마운트 시 데이터 로드
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
    align-items: center;
    justify-content: center;
    aspect-ratio: 1;
    grid-column: 1 / 2;
    grid-row: 1 / 3;
    min-height: 100px;
  }

  .circle-progress {
    width: 80%;
    height: 80%;
    border: 4px solid rgba(255, 255, 255, 0.2);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .circle-content .label {
    font-size: 30px;
    margin-bottom: 35px;
  }

  .circle-content .percentage {
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 4px;
  }

  .circle-content .count {
    font-size: 8px;
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
    grid-template-columns: 1fr 1fr; /* 한 행에 2개 영역 */
    /* align-items: center; */
    gap: 12px;  /* 원래 6px → 12px */
    /* margin-bottom: 30px; */
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
    font-weight: 500;
    min-width: 30px;            
    font-size: 20px;            
  }

  .stat-comparison {
    color: #ff5252;
    min-width: 30px;            
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
    transition: width 0.3s ease;
  }

  .bar-progress.red {
    background: #ff5252;
  }

  .stat-value {
    color: #2196f3;
    font-weight: 500;
    min-width: 15px;
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
    margin-bottom: 0px;
  }

  .status-value {
    font-size: 16px;
    font-weight: bold;
  }

  .icon {
    font-size: 8px;
  }

  @media (max-width: 1024px) {
    .status-grid {
      grid-template-columns: 1fr 1fr;
    }
    
    .attendance-stats {
      grid-column: span 2;
    }
  }

  @media (max-width: 640px) {
    .status-grid {
      grid-template-columns: 1fr;
    }
    
    .attendance-stats {
      grid-column: span 1;
    }
    
    .stat-row {
      grid-template-columns: auto 1fr auto;
    }
    
    .stat-comparison {
      grid-column: 1;
      grid-row: 2;
    }
    
    .bar-track:nth-child(4) {
      grid-column: 2;
      grid-row: 2;
    }
    
    .stat-value:last-child {
      grid-column: 3;
      grid-row: 2;
    }
  }
</style>

