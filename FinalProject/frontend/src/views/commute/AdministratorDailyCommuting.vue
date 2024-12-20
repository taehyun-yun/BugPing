<template>
  <div class="today">{{ today }}</div>
  <div class="page-layout">
    <!-- 상단 섹션 -->
    <div class="top-section-container">
      <div class="top-section">
        <div class="dashboard-section">
          <admin-daily-widget />
          <admin-daily-colleague-widget/>
        </div>
      </div>
    </div>

    <!-- 하단 섹션(테이블) -->
    <div class="attendance-section-container">
      <div class="attendance-section">
        <admin-daily-attendance-table />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import AdminDailyWidget from '@/components/commute/AdminDailyWidget.vue';
// import AdminDailyColleagueWidget from '@/components/commute/AdminDailyColleagueWidget.vue';
//import AdminDailyAttendanceTable from '@/components/commute/AdminDailyAttendanceTable.vue';

const today = ref(new Date().toLocaleDateString());
</script>

<style scoped>
.page-layout {
  background: #f0f0f0;
  padding: 20px 0; /* 상하 패딩 */
  /* 전체 페이지에 대한 스타일 필요시 추가 */
}

.top-section, .attendance-section {
  max-width: 1200px;  /* 상하 섹션 동일한 최대 너비 */
  margin: 0 auto;     /* 중앙 정렬 */
  padding: 0 20px;    /* 좌우 동일한 패딩 */
}

.dashboard-section {
  background-color: #f5f5f5;
  border-radius: 8px;
  padding: 20px;
  display: flex; 
  gap: 20px;
  justify-content: center; /* 내부 위젯들이 중앙으로 정렬되도록 */
}

.attendance-section {
  background-color: #ffffff;
  border-radius: 8px;
  padding: 20px;
  margin-top: 20px; /* 상단 섹션과의 간격 */
}

.today {
  text-align: center;
  font-weight: bold;
  margin-bottom: 10px;
  font-size: 1.2rem;
}
</style>
