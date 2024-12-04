<template>
    <div class="header-wrapper">
        <div class="header">
          <div class="header-left">
            <span class="header-title">운영의 달인</span>
          </div>
          <div class="header-right">
            <div class="search-fieldset">
              <span class="search-text">검색</span>
            </div>
            <div class="header-icons">
              <img src="../assets/MainheaderImg/book.png" alt="Book Icon" class="header-icon" />
              <img src="../assets/MainheaderImg/cloud.png" alt="Cloud Icon" class="header-icon" />
              <div class="language-selector">
                <!-- <img src="../assets/MainheaderImg/Language.png" alt="Language Icon" class="language-icon" /> -->
                <span class="language-text">한국어</span>
              </div>
              <img src="../assets/MainheaderImg/message.png" alt="ChannelTalk Icon" class="header-icon" />
              <div class="bell-notification">
                <img src="../assets/MainheaderImg/bell.png" alt="Bell Icon" class="bell-icon" />
                <div class="notification-indicator"></div>
              </div>
              <img src="../assets/MainheaderImg/myprofile.png" alt="Profile Icon" class="profile-icon" />
            </div>
          </div>
        </div>
      </div>
</template>
  
<script setup>

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

/* 모든 페이지가 아래로 밀리도록 공통 클래스 추가 */
.page-content {
    padding-top: 54px; /* 헤더의 높이만큼 아래로 밀리도록 조정 */
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
    padding: 4px 8px;
    height: 32px;
    margin-right: 20px;
}

.search-icon {
    width: 18px;
    height: 18px;
    margin-right: 8px;
}

.search-text {
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

.language-selector {
    display: flex;
    align-items: center;
    margin-right: 16px;
}

.language-icon {
    width: 24px;
    height: 24px;
    margin-right: 8px;
}

.language-text {
    color: #ffffff;
    font-family: "Inter-Medium", sans-serif;
    font-size: 14px;
    font-weight: 500;
}

.bell-notification {
    position: relative;
}

.bell-icon {
    width: 24px;
    height: 24px;
}
.profile-icon {
    padding-left: 25px;
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
</style>