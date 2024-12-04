<template>
    <div class="worker-widget">
      <div class="header-section">
        <div class="profile-banner"></div>
        <div class="profile-info">
          <div class="profile-image-section">
            <img class="profile-image" src="../../assets/frontimages/credits-to-unsplash-com0.png" alt="Profile Picture" />
          </div>
          <div class="profile-name-section">
            <div class="profile-name">Esthera Jackson</div>
            <div class="profile-email">esthera@simmmple.com</div>
          </div>
          <div class="edit-icon-section">
            <img class="edit-icon" src="../../assets/frontimages/edit0.svg" alt="Edit Icon" />
          </div>
        </div>
        <div class="tabs-section">
          <div class="tab project-tab">
            <div class="tab-content">
              <img class="tab-icon" src="../../assets/frontimages/icon0.svg" alt="Projects Icon" />
              <div class="tab-label">PROJECTS</div>
            </div>
          </div>
          <div class="tab team-tab">
            <div class="tab-content">
              <img class="tab-icon" src="../../assets/frontimages/icon1.svg" alt="Teams Icon" />
              <div class="tab-label">TEAMS</div>
            </div>
          </div>
          <div class="tab overview-tab">
            <div class="tab-content">
              <img class="tab-icon" src="../../assets/frontimages/icon2.svg" alt="Overview Icon" />
              <div class="tab-label">OVERVIEW</div>
            </div>
          </div>
        </div>
      </div>
      <div class="attendance-summary-section">
        <div class="monthly-attendance">
          <div class="attendance-title">이번달 출퇴근 현황</div>
          <div class="attendance-summary-cards">
            <div class="attendance-summary-card" style="background-color: #d0ebff;">
              <div class="card-title">출근율</div>
              <div class="card-value">0%</div>
            </div>
            <div class="attendance-summary-card" style="background-color: #ffe0b2;">
              <div class="card-title">출근</div>
              <div class="card-value">0</div>
            </div>
            <div class="attendance-summary-card" style="background-color: #dcedc8;">
              <div class="card-title">지각</div>
              <div class="card-value">0</div>
            </div>
            <div class="attendance-summary-card" style="background-color: #ffccbc;">
              <div class="card-title">결근</div>
              <div class="card-value">0</div>
            </div>
            <div class="attendance-summary-card" style="background-color: #f0f4c3;">
              <div class="card-title">조퇴</div>
              <div class="card-value">0</div>
            </div>
          </div>
        </div>
        <div class="work-attendance-button">
          <div class="attendance-button">
            출퇴근
          </div>
        </div>
      </div>
      <div class="attendance-record-section">
        <div class="attendance-details">
          <div class="attendance-title">출퇴근 기록</div>
          <div class="conversations">
            <div class="conversation-item">
              <div class="conversation-image">
                <img class="author-image" src="../../assets/frontimages/credits-to-unsplash-com1.png" alt="Profile Picture" />
              </div>
              <div class="conversation-details">
                <div class="conversation-author">Esthera Jackson</div>
                <div class="conversation-text">About files I can...</div>
              </div>
              <div class="reply-button">REPLY</div>
            </div>
            <div class="conversation-item">
              <div class="conversation-image">
                <img class="author-image" src="../../assets/frontimages/credits-to-unsplash-com2.png" alt="Profile Picture" />
              </div>
              <div class="conversation-details">
                <div class="conversation-author">Esthera Jackson</div>
                <div class="conversation-text">Have a great afternoon...</div>
              </div>
              <div class="reply-button">REPLY</div>
            </div>
            <div class="conversation-item">
              <div class="conversation-image">
                <img class="author-image" src="../../assets/frontimages/credits-to-unsplash-com3.png" alt="Profile Picture" />
              </div>
              <div class="conversation-details">
                <div class="conversation-author">Esthera Jackson</div>
                <div class="conversation-text">Awesome work, can you change...</div>
              </div>
              <div class="reply-button">REPLY</div>
            </div>
            <div class="conversation-item">
              <div class="conversation-image">
                <img class="author-image" src="../../assets/frontimages/credits-to-unsplash-com4.png" alt="Profile Picture" />
              </div>
              <div class="conversation-details">
                <div class="conversation-author">Esthera Jackson</div>
                <div class="conversation-text">Hi! I need more informations...</div>
              </div>
              <div class="reply-button">REPLY</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: "WorkerWidget",
  };
  </script>
  
  <style scoped>
  .worker-widget {
    width: 100%;
    max-width: 1200px;
    margin: auto;
    background-color: #f5f5f5;
    padding: 40px;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
  
  .header-section {
    width: 100%;
    background-color: #e0e0e0;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    position: relative;
  }
  
  .profile-info {
    display: flex;
    align-items: center;
    position: absolute;
    top: -40px;
    left: 20px;
    background-color: #ffffff;
    padding: 10px;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
  
  .profile-image-section {
    margin-right: 20px;
  }
  
  .profile-image {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    border: 4px solid #ffffff;
  }
  
  .tabs-section {
    display: flex;
    justify-content: flex-end;
    width: 100%;
  }
  
  .tab {
    margin-right: 20px;
  }
  
  .tab-content {
    display: flex;
    align-items: center;
    cursor: pointer;
  }
  
  .tab-icon {
    width: 20px;
    height: 20px;
    margin-right: 10px;
  }
  
  .attendance-summary-section {
    margin-top: 40px;
    display: flex;
    justify-content: space-around;
    gap: 20px;
    flex-wrap: wrap;
  }
  
  .monthly-attendance {
    flex: 1;
    background: #ffffff;
    padding: 20px;
    border-radius: 8px;
    text-align: center;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
    min-width: 200px;
  }
  
  .attendance-title {
    font-size: 1.5em;
    margin-bottom: 20px;
  }
  
  .attendance-summary-cards {
    display: flex;
    justify-content: space-between;
    width: 100%;
    gap: 10px;
    flex-wrap: wrap;
  }
  
  .attendance-summary-card {
    background: #ffffff;
    padding: 20px;
    border-radius: 8px;
    text-align: center;
    flex: 1;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
    min-width: 150px;
  }
  
  .work-attendance-button {
    text-align: center;
    margin-top: 20px;
  }
  
  .attendance-button {
    display: inline-block;
    padding: 10px 20px;
    background-color: #007bff;
    color: #ffffff;
    border-radius: 8px;
    cursor: pointer;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
  
  .attendance-record-section {
    margin-top: 40px;
    width: 100%;
    background: #ffffff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
  }
  
  .attendance-details {
    text-align: center;
  }
  
  .conversations {
    margin-top: 20px;
  }
  
  .conversation-item {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
    justify-content: space-between;
    background: #ffffff;
    padding: 10px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  .reply-button {
    cursor: pointer;
    color: #007bff;
    margin-left: 20px;
  }
  
  .conversation-image {
    margin-right: 20px;
  }
  
  .author-image {
    width: 40px;
    height: 40px;
    border-radius: 50%;
  }
  </style>
  