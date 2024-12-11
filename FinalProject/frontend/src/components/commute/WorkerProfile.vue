<template>
<div class="profile-container">
    <div class="profile-header-container">
      <div class="profile-header">
        <div class="avatar-section">
          <img class="avatar-image" src="@/assets/WorkerCommutingImg/myImage.png" alt="User Avatar">
          <div class="user-info">
            <div class="user-name">Esthera Jackson</div>
            <div class="user-email">esthera@simmmple.com</div>
          </div>
          <div class="edit-icon">
            <img src="@/assets/WorkerCommutingImg/Icon.png" alt="Edit Profile" class="edit-profile-image" />
          </div>
        </div>
        <div class="navigation-tabs">
          <div class="tab overview-tab">
            <!-- <img src="overview-icon.png" alt="Overview Icon" class="tab-icon"> -->
            <div class="tab-text">OVERVIEW</div>
          </div>
          <div class="tab teams-tab">
            <!-- <img src="teams-icon.png" alt="Teams Icon" class="tab-icon"> -->
            <div class="tab-text">TEAMS</div>
          </div>
          <div class="tab projects-tab">
            <!-- <img src="projects-icon.png" alt="Projects Icon" class="tab-icon"> -->
            <div class="tab-text">PROJECTS</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.container {
  margin: 0;
  /* 모든 마진 초기화 */
  padding: 0;
  /* 모든 패딩 초기화 */
  display: flex;
}

.sidebar {
  width: 250px;
}

.main-content {
  flex-grow: 1;
  padding: 20px;
}

.profile-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 15px;
  box-shadow: 0px 2px 5.5px 0px rgba(0, 0, 0, 0.02);
}

.profile-header-container {
  padding: 20px;
  background: linear-gradient(112.83deg, rgba(255, 255, 255, 0.82) 0%, rgba(255, 255, 255, 0.8) 100%);
  border-radius: 15px;
  border: 1.5px solid #ffffff;
  backdrop-filter: blur(10.5px);
}

.profile-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-image {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.user-name {
  font-family: "Acme-Regular", sans-serif;
  font-size: 18px;
  color: #2d3748;
}

.user-email {
  font-family: "ABeeZee-Regular", sans-serif;
  font-size: 14px;
  color: #718096;
}

.edit-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.edit-profile-image {
  width: 20px;
  height: 20px;
}

.navigation-tabs {
  display: flex;
  gap: 20px;
}

.tab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0px 2px 5.5px 0px rgba(0, 0, 0, 0.06);
  cursor: pointer;
}

.tab-icon {
  width: 15px;
  height: 15px;
}

.tab-text {
  font-family: "Acme-Regular", sans-serif;
  font-size: 10px;
  color: #2d3748;
}
</style>
