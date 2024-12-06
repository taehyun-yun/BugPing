<template>
  <div class="side-menu">
    <div class="header-item">
      <img src="../assets/SidebarImg/home.png" alt="Home Icon" class="icon-home-header" />
      <div class="header-title">운영의 달인</div>
    </div>
    <div class="menu-item">
      <img src="../assets/SidebarImg/home.png" alt="Home Icon" class="icon-home" />
      <div class="menu-title">홈</div>
    </div>
    <div class="menu-item" @click="goToSchedule">
      <img src="../assets/SidebarImg/calendar.png" alt="Calendar Icon" class="icon-calendar" />
      <div class="menu-title">스케줄 관리</div>
    </div>
    <div class="menu-item" @click="goToCalculatorPage">
      <img src="../assets/SidebarImg/dollar-sign.png" alt="Dollar Icon" class="icon-dollar" />
      <div class="menu-title">급여 관리</div>
    </div>
    <div class="menu-item" @click="goToWorkerCommuting">
      <img src="../assets/SidebarImg/credit-card.png" alt="Credit Card Icon" class="icon-credit-card" />
      <div class="menu-title">근태 관리</div>
    </div>
    <div class="menu-item" @click="goToNoticeMain">
      <img src="../assets/SidebarImg/edit.png" alt="Edit Icon" class="icon-edit" />
      <div class="menu-title">공지사항</div>
    </div>
    <div class="menu-item">
      <img src="../assets/SidebarImg/alert-circle.png" alt="Help Icon" class="icon-help" />
      <div class="menu-title">Help</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const goToCalculatorPage = () => {
  router.push({ path: '/cal' });
};

const goToWorkerCommuting = () => {
  router.push({ path: '/commute' });
};

const goToSchedule = () => {
  router.push({ path: '/schedule' });
};

const goToNoticeMain = () => {
  router.push({ path: '/noticemain' });
};
</script>

<style scoped>
.side-menu {
  position: relative;
  width: 250px;
  height: 100vh; /* 화면 전체 높이에 맞추어 */
  background-color: #ffffff;
  box-shadow: 0px 10px 60px 0px rgba(226, 236, 249, 0.5);
  z-index: 10; /* 다른 요소보다 앞에 있도록 */
  border-right: 2px solid #d7dae5;
}
.menu-item.highlighted {
  background-color: #5932ea;
  color: #ffffff;
}
.sidebar-background {
  width: 330px;
  height: 1080px;
  position: absolute;
  left: 0;
  top: 0;
  background: #ffffff;
}
.highlight-bar {
  background: #5932ea;
  border-radius: 8px;
  width: 330px;
  height: 41.47px;
  position: absolute;
  left: 0;
  top: 175.79px;
}
.menu-item {
  display: flex;
  align-items: center;
  padding: 20px;
  cursor: pointer;
  transition: background-color 0.3s ease, color 0.3s ease;
}
.menu-item:hover {
  background-color: #5932ea;
  color: #ffffff;
}
.menu-item.highlighted {
  background-color: #5932ea;
  color: #ffffff;
}
.icon-home, .icon-calendar, .icon-dollar, .icon-credit-card, .icon-edit, .icon-help {
  width: 24px;
  height: 24px;
  margin-right: 20px;
}
.icon-chevron {
  margin-left: auto;
  width: 18px;
  height: 18px;
}
.menu-title {
  font-family: "Poppins-Medium", sans-serif;
  font-size: 14px;
  font-weight: 500;
  color: #9197b3;
}
.header-item {
  display: flex;
  align-items: center;
  padding: 20px;
}
.header-title {
  font-family: "Poppins-SemiBold", sans-serif;
  font-size: 26px;
  font-weight: 600;
  color: #000;
  margin-left: 10px;
}
.icon-home-header {
  width: 24px;
  height: 24px;
}
</style>