<template>
    <div class="dashboard-container">
      <!-- 출퇴근 기록 영역 -->
      <div class="attendance-record">
        <div class="attendance-card" @click="handleShiftClick">
          <div class="card-background">
            <div class="shift-title">출근<br />퇴근</div>
          </div>
        </div>
        <div class="conversations">
          <div class="conversation-card" v-for="(conversation, index) in conversationsData" :key="index">
            <div class="conversation-image">
              <img src="../assets/WorkWidgetImg/userprofile.png" alt="User Profile" class="profile-image" />
            </div>
            <div class="conversation-details">
              <div class="conversation-name">{{ conversation.name }}</div>
              <div class="conversation-text">{{ conversation.message }}</div>
              <div class="reply-button">REPLY</div>
            </div>
          </div>
        </div>
      </div>
  
      <!-- 이번달 출퇴근 현황 영역 -->
      <div class="monthly-attendance-status-container">
        <div class="monthly-attendance-status">
          <div class="status-card">
            <div class="status-circle">
              <!-- <img src="circle-background.png" alt="Attendance Circle" class="circle-background" /> -->
              <div class="status-info">
                <div class="status-title">출근율</div>
                <div class="status-percentage">{{ attendanceData.percentage }} %</div>
                <div class="status-count">{{ attendanceData.present }} / {{ attendanceData.total }}</div>
              </div>
            </div>
          </div>
          <div class="status-summary">
            <div class="summary-item" v-for="(item, index) in attendanceSummary" :key="index" :style="{ backgroundColor: item.color }">
              <div class="summary-title">{{ item.title }}</div>
              <div class="summary-count">{{ item.count }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  
  const conversationsData = ref([
    { name: 'Esthera Jackson', message: 'About files I can...', image: 'credits-to-unsplash-com0.png' },
    { name: 'Esthera Jackson', message: 'Have a great afternoon...', image: 'credits-to-unsplash-com1.png' },
    { name: 'Esthera Jackson', message: 'Awesome work', image: 'credits-to-unsplash-com2.png' },
    { name: 'Esthera Jackson', message: 'hi', image: 'credits-to-unsplash-com3.png' }
  ]);
  
  const attendanceData = ref({
    percentage: '-',
    present: 0,
    total: 0
  });
  
  const attendanceSummary = ref([
    { title: '출근', count: '-', color: '#9a2ac6' },
    { title: '지각', count: '-', color: '#4dc9aa' },
    { title: '결근', count: '-', color: '#f44d50' },
    { title: '조퇴', count: '-', color: '#ffbb00' }
  ]);
  
  const shiftState = ref(false);
  
  const handleShiftClick = () => {
    if (!shiftState.value) {
      alert('출근하였습니다');
    } else {
      alert('퇴근하였습니다');
    }
    shiftState.value = !shiftState.value;
  };
  </script>
  
  <style scoped>
  .dashboard-container {
    display: flex;
    gap: 20px;
    padding: 20px;
    align-items: flex-start;
    justify-content: center; /* 가운데 정렬 추가 */
  }
  
  .attendance-record {
    display: flex;
    align-items: center;
    padding: 20px;
    background: #f5f5f5;
  }
  
  .attendance-card {
    width: 280px;
    height: 340px;
    margin-right: 20px;
    background: #4fd1c5;
    border-radius: 15px;
    position: relative;
    cursor: pointer;
  }
  
  .card-background {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .shift-title {
    color: #ffffff;
    font-family: "Aclonica-Regular", sans-serif;
    font-size: 40px;
    line-height: 1.4;
    text-align: center;
  }
  
  .conversations {
    display: flex;
    flex-direction: column;
    gap: 15px;
  }
  
  .conversation-card {
    display: flex;
    align-items: center;
    padding: 10px;
    background: #ffffff;
    border-radius: 15px;
    box-shadow: 0 3.5px 5.5px rgba(0, 0, 0, 0.02);
  }
  
  .conversation-image {
    flex-shrink: 0;
    margin-right: 10px;
  }
  
  .profile-image {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    object-fit: cover;
  }
  
  .conversation-details {
    display: flex;
    flex-direction: column;
    flex-grow: 1;
  }
  
  .conversation-name {
    font-family: "Acme-Regular", sans-serif;
    font-size: 14px;
    color: #2d3748;
    margin-bottom: 5px;
  }
  
  .conversation-text {
    font-family: "ABeeZee-Regular", sans-serif;
    font-size: 14px;
    color: #718096;
  }
  
  .reply-button {
    color: #4fd1c5;
    font-family: "Acme-Regular", sans-serif;
    font-size: 10px;
    cursor: pointer;
    margin-left: auto;
  }
  
  .monthly-attendance-status-container {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }
  
  .monthly-attendance-status {
    display: flex;
    flex-direction: row;
    gap: 20px;
    background: #f5f5f5;
    border-radius: 15px;
    padding: 20px;
    align-items: flex-start;
    height: 340px; /* 왼쪽 출근율 카드와 높이 맞춤 */
  }
  
  .status-card {
    position: relative;
    width: 280px;
    height: 300px;
    background: #3299fe;
    border-radius: 16px;
    padding: 20px;
  }
  
  .status-circle {
    position: relative;
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .circle-background {
    position: absolute;
    width: 200px;
    height: 200px;
  }
  
  .status-info {
    position: absolute;
    text-align: center;
    color: #ffffff;
  }
  
  .status-title {
    font-family: "Inter-Medium", sans-serif;
    font-size: 14px;
    font-weight: 500;
    margin-bottom: 5px;
  }
  
  .status-percentage {
    font-family: "Inter-Bold", sans-serif;
    font-size: 24px;
    font-weight: 700;
    margin-bottom: 5px;
  }
  
  .status-count {
    font-family: "Inter-Medium", sans-serif;
    font-size: 14px;
    font-weight: 500;
  }
  
  .status-summary {
    display: grid;
    grid-template-columns: repeat(2, 1fr); /* 두 개의 컬럼 */
    gap: 20px;
    justify-content: center;
    height: 100%; /* 부모 컨테이너에 맞추어 높이 조정 */
    margin: auto;
  }
  
  .summary-item {
    width: 120px;
    height: 140px; /* 출근율 카드의 높이에 맞추기 위해 높이 조정 */
    border-radius: 16px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #ffffff;
    padding: 10px;
  }
  
  .summary-title {
    font-family: "Inter-Bold", sans-serif;
    font-size: 16px;
    margin-bottom: 5px;
  }
  
  .summary-count {
    font-family: "Inter-SemiBold", sans-serif;
    font-size: 24px;
  }
  </style>
