<template>
    <div class="header-wrapper">
      <div class="header">
        <div class="header-left">
          <span class="header-title">운영의 달인</span>
          <img src="../assets/MainheaderImg/log-in.png" alt="Login Icon" class="login-icon" @click="goToLoginPage" />
        </div>
        <div class="header-right">
          <div class="search-fieldset">
            <img src="../assets/MainheaderImg/header-search.png" alt="Search Icon" class="search-icon" />
            <input type="text" placeholder="검색" class="header-search-input" />
          </div>
          <div class="header-icons">
            <img src="../assets/MainheaderImg/book.png" alt="Book Icon" class="header-icon" />
            <img src="../assets/MainheaderImg/cloud.png" alt="Cloud Icon" class="header-icon" />
            <img src="../assets/MainheaderImg/message.png" alt="ChannelTalk Icon" class="header-icon" />
            <div class="bell-notification">
              <img src="../assets/MainheaderImg/bell.png" alt="Bell Icon" class="bell-icon" />
              <div class="notification-indicator"></div>
            </div>
            <img src="../assets/MainheaderImg/myprofile.png" alt="profile Icon" class="profile-icon" />
          </div>
        </div>
      </div>
    </div>
  </template>

  <script setup>
  import { onMounted } from 'vue';
  import { useRouter } from 'vue-router';
  const router = useRouter();

  // 헤더 컴포넌트가 로드되면 body에 padding-top 추가하여 모든 페이지에서 여백을 맞춤
  // onMounted(() => {
  //   document.body.style.paddingTop = '54px';
  // });

  // 로그인 페이지로 이동하는 함수
  const goToLoginPage = () => {
    router.push('/login');
  };
  </script>
  
  <style scoped>
  .header-wrapper {
      width: 100%;
      position: fixed;
      top: 0;
      left: 0;
      z-index: 100;
  }
  
  .header {
      background: var(--dashboardshoplworkscom-mine-shaft, #333333);
      border-style: solid;
      border-color: var(--dashboardshoplworkscom-mine-shaft, #333333);
      border-width: 0 0 1px 0;
      height: 54px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0 20px;
  }
  
  .header-left .header-title {
      color: var(--dashboardshoplworkscom-white, #ffffff);
      font-family: "Inter-Medium", sans-serif;
      font-size: 16px;
      line-height: 20px;
      font-weight: 500;
  }
  
  .header-right {
      display: flex;
      align-items: center;
  }
  
  .search-fieldset {
      background: rgba(255, 255, 255, 0.1);
      border-radius: 16px;
      display: flex;
      align-items: center;
      padding: 4px 12px;
      height: 32px;
      margin-right: 20px;
      position: relative;
  }
  
  .search-icon {
      width: 18px;
      height: 18px;
      margin-right: 8px;
  }
  
  .header-search-input {
      background: transparent;
      border: none;
      outline: none;
      color: rgba(255, 255, 255, 0.5);
      font-family: "Inter-Medium", sans-serif;
      font-size: 14px;
      font-weight: 500;
  }
  
  .header-icons {
      display: flex;
      align-items: center;
  }
  
  .header-icon {
      width: 24px;
      height: 24px;
      margin-right: 16px;
  }

  .profile-icon {
    padding-left: 20px;
  }
  
  .bell-notification {
      position: relative;
  }
  
  .bell-icon {
      width: 24px;
      height: 24px;
  }
  
  .notification-indicator {
      background: #ff6666;
      border-radius: 4.5px;
      border: 2px solid var(--dashboardshoplworkscom-mine-shaft, #333333);
      width: 9px;
      height: 9px;
      position: absolute;
      top: 0;
      right: 0;
  }

  .login-icon {
      width: 24px;
      height: 24px;
      cursor: pointer;
  }

  .login-icon:hover {
      cursor: pointer;
  }
  </style>
  