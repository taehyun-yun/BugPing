<template>
    <div class="status-container">
      <h2 class="status-title">출퇴근 현황</h2>
      
      <div class="status-grid">
        <!-- 출근율 카드 -->
        <div class="status-card attendance-rate">
          <div class="circle-progress">
            <div class="circle-content">
              <div class="label">출근율</div>
              <div class="percentage">-%</div>
              <div class="count">
                <span class="icon">👤</span> 0 / 0
              </div>
            </div>
          </div>
        </div>
  
        <!-- 출근/휴무 통계 카드 -->
        <div class="status-card attendance-stats">
          <div class="stat-row">
            <div class="stat-label">출근</div>
            <div class="stat-bar">
              <div class="bar-track">
                <div class="bar-progress" style="width: 0%"></div>
              </div>
              <div class="stat-value">-</div>
            </div>
            <div class="stat-comparison">휴무</div>
            <div class="bar-track">
              <div class="bar-progress red" style="width: 0%"></div>
            </div>
            <div class="stat-value red">-</div>
          </div>
  
          <div class="stat-row">
            <div class="stat-label">출근 전</div>
            <div class="stat-bar">
              <div class="bar-track">
                <div class="bar-progress" style="width: 0%"></div>
              </div>
              <div class="stat-value">-</div>
            </div>
            <div class="stat-comparison">미출근</div>
            <div class="bar-track">
              <div class="bar-progress red" style="width: 0%"></div>
            </div>
            <div class="stat-value red">-</div>
          </div>
  
          <div class="employee-count">
            <span class="icon">👥</span>
            출근 대상
            <span class="count">-</span>
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
  // 필요한 데이터와 메서드를 여기에 추가할 수 있습니다
  </script>
  
  <style scoped>
  .status-container {
    padding: 24px;
    max-width: 1200px;
    margin: 0 auto;
  }
  
  .status-title {
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 24px;
  }
  
  .status-grid {
    display: grid;
    grid-template-columns: 1fr 1.5fr 1fr 1fr;
    gap: 20px;
  }
  
  .status-card {
    background: white;
    border-radius: 16px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }
  
  /* 출근율 카드 스타일 */
  .attendance-rate {
    background: #2196f3;
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    aspect-ratio: 1;
  }
  
  .circle-progress {
    width: 80%;
    height: 80%;
    border: 8px solid rgba(255, 255, 255, 0.2);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .circle-content {
    text-align: center;
  }
  
  .circle-content .label {
    font-size: 18px;
    margin-bottom: 8px;
  }
  
  .circle-content .percentage {
    font-size: 32px;
    font-weight: bold;
    margin-bottom: 8px;
  }
  
  .circle-content .count {
    font-size: 14px;
    opacity: 0.9;
  }
  
  /* 출근/휴무 통계 카드 스타일 */
  .attendance-stats {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
  
  .stat-row {
    display: grid;
    grid-template-columns: auto 1fr auto 1fr auto;
    align-items: center;
    gap: 12px;
  }
  
  .stat-label {
    font-weight: 500;
    min-width: 60px;
  }
  
  .stat-comparison {
    color: #ff5252;
    min-width: 60px;
  }
  
  .bar-track {
    height: 8px;
    background: #f5f5f5;
    border-radius: 4px;
    overflow: hidden;
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
    min-width: 30px;
  }
  
  .stat-value.red {
    color: #ff5252;
  }
  
  .employee-count {
    display: flex;
    align-items: center;
    gap: 8px;
    padding-top: 12px;
    border-top: 1px solid #eee;
    color: #666;
  }
  
  .employee-count .count {
    margin-left: auto;
    font-weight: 500;
  }
  
  /* 지각/조퇴 카드 스타일 */
  .late-card {
    background: #4caf50;
    color: white;
  }
  
  .early-leave-card {
    background: #ffc107;
    color: white;
  }
  
  .late-card,
  .early-leave-card {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    aspect-ratio: 1;
  }
  
  .status-label {
    font-size: 20px;
    margin-bottom: 12px;
  }
  
  .status-value {
    font-size: 32px;
    font-weight: bold;
  }
  
  .icon {
    font-size: 16px;
  }
  
  /* 반응형 디자인 */
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