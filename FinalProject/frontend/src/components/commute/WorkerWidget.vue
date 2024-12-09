<template>
  <div class="dashboard-container">
    <!-- 왼쪽 섹션: 출퇴근 기록 -->
    <section class="attendance-section">
      <div class="attendance-card" @click="handleShiftClick">
        <div class="card-content">
          <h2 class="shift-title">출근<br/>퇴근</h2>
        </div>
      </div>
      
      <div class="attendance-list-wrapper">
        <div class="attendance-list">
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
              <span class="user-name">{{ record.name }}</span>
              <p class="attendance-message">{{ record.message }}</p>
            </div>
            <button 
              @click="handleReply(record.name)"
              class="reply-button"
            >
              REPLY
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- 오른쪽 섹션: 출근 통계 -->
    <section class="stats-section">
      <div class="attendance-rate">
        <div class="rate-content">
          <h3>출근율</h3>
          <div class="rate-numbers">
            <span class="percentage">{{ monthlyStatus.percentage }}%</span>
            <span class="count">{{ monthlyStatus.present }} / {{ monthlyStatus.total }}</span>
          </div>
        </div>
      </div>
      
      <div class="stats-grid">
        <div 
          v-for="stat in summaryList" 
          :key="stat.title"
          class="stat-card"
          :style="{ backgroundColor: stat.color }"
        >
          <h4>{{ stat.title }}</h4>
          <span class="stat-count">{{ stat.count }}</span>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const attendanceList = ref([
  { name: 'Esthera Jackson', message: '출근하였습니다.', image: '/profile1.png' },
  { name: 'John Doe', message: '조퇴하였습니다.', image: '/profile2.png' },
  { name: 'Jane Smith', message: '지각하였습니다.', image: '/profile3.png' },
  { name: 'Chris Johnson', message: '결근하였습니다.', image: '/profile4.png' },
  // 스크롤 테스트를 위한 추가 데이터
  { name: 'Alice Brown', message: '출근하였습니다.', image: '/profile5.png' },
  { name: 'Bob Wilson', message: '출근하였습니다.', image: '/profile6.png' }
])

const monthlyStatus = ref({
  percentage: 0,
  present: 0,
  total: 0
})

const summaryList = ref([
  { title: '출근', count: 0, color: '#9a2ac6' },
  { title: '지각', count: 0, color: '#4dc9aa' },
  { title: '결근', count: 0, color: '#f44d50' },
  { title: '조퇴', count: 0, color: '#ffbb00' }
])

const shiftState = ref(false)

const handleShiftClick = () => {
  if (!shiftState.value) {
    alert('출근하였습니다')
    monthlyStatus.value.present++
    monthlyStatus.value.total++
    summaryList.value[0].count++
    attendanceList.value.unshift({
      name: '나',
      message: '출근하였습니다.',
      image: '/my-profile.png'
    })
  } else {
    alert('퇴근하였습니다')
    monthlyStatus.value.total++
  }
  shiftState.value = !shiftState.value
}

const handleReply = (name) => {
  alert(`${name} 님에게 답장을 보냅니다.`)
}
</script>

<style scoped>
.dashboard-container {
  max-width: 1400px;
  margin: 2rem auto;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 2rem;
  background: white;
  border-radius: 1.5rem;
  padding: 2rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

/* 왼쪽 섹션 스타일링 */
.attendance-section {
  display: grid;
  grid-template-columns: 250px 1fr;
  gap: 1.5rem;
  height: 100%;
}

.attendance-card {
  background: #4fd1c5;
  border-radius: 1rem;
  height: 100%;
  cursor: pointer;
  transition: transform 0.2s;
}

.attendance-card:hover {
  transform: translateY(-2px);
}

.card-content {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.shift-title {
  color: white;
  font-size: clamp(1.5rem, 4vw, 2.5rem);
  text-align: center;
  line-height: 1.4;
}

.attendance-list-wrapper {
  height: 400px; /* 고정 높이 설정 */
  overflow: hidden;
}

.attendance-list {
  height: 100%;
  overflow-x: hidden; /* 강제로 좌우 스크롤 방지 */
  overflow-y: auto;
  padding-right: 0.5rem;
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

.reply-button {
  padding: 0.5rem 1rem;
  color: #4fd1c5;
  font-weight: 600;
  font-size: 0.75rem;
  border: none;
  background: transparent;
  cursor: pointer;
  transition: color 0.2s;
}

.reply-button:hover {
  color: #38b2ac;
}

/* 오른쪽 섹션 스타일링 */
.stats-section {
  display: grid;
  grid-template-columns: 250px 1fr;
  gap: 1.5rem;
  height: 100%;
}

.attendance-rate {
  background: #3299fe;
  border-radius: 1rem;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  padding: 2rem;
}

.rate-content {
  text-align: center;
}

.rate-content h3 {
  font-size: clamp(1.25rem, 3vw, 1.5rem);
  margin-bottom: 1rem;
}

.rate-numbers {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.percentage {
  font-size: clamp(2rem, 5vw, 3rem);
  font-weight: 700;
}

.count {
  font-size: clamp(1rem, 2vw, 1.25rem);
  opacity: 0.9;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  grid-template-rows: repeat(2, 1fr);
  gap: 1rem;
}

.stat-card {
  border-radius: 1rem;
  padding: 1.5rem;
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.stat-card h4 {
  font-size: clamp(1rem, 2vw, 1.25rem);
  margin-bottom: 0.5rem;
}

.stat-count {
  font-size: clamp(1.5rem, 3vw, 2rem);
  font-weight: 600;
}

/* 스크롤바 스타일링 */
.attendance-list::-webkit-scrollbar {
  width: 6px;
}

.attendance-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.attendance-list::-webkit-scrollbar-thumb {
  background: #cbd5e0;
  border-radius: 3px;
}

.attendance-list::-webkit-scrollbar-thumb:hover {
  background: #a0aec0;
}

/* 반응형 디자인 */
@media (max-width: 1200px) {
  .dashboard-container {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
}

@media (max-width: 768px) {
  .attendance-section,
  .stats-section {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .attendance-card,
  .attendance-rate {
    height: 200px;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .dashboard-container {
    margin: 1rem;
    padding: 1rem;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>