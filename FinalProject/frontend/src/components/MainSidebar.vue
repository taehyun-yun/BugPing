<template>
  <div class="side-menu">
    <div class="menu-item" @click="goPage('home')">
      <img src="../assets/SidebarImg/home.png" alt="Home Icon" class="icon" />
      <div class="menu-title">홈</div>
    </div>
    <div class="menu-item" @click="goPage('contract')" v-if="forEmployer">
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 384 512"><path d="M64 0C28.7 0 0 28.7 0 64L0 448c0 35.3 28.7 64 64 64l256 0c35.3 0 64-28.7 64-64l0-288-128 0c-17.7 0-32-14.3-32-32L224 0 64 0zM256 0l0 128 128 0L256 0zM80 64l64 0c8.8 0 16 7.2 16 16s-7.2 16-16 16L80 96c-8.8 0-16-7.2-16-16s7.2-16 16-16zm0 64l64 0c8.8 0 16 7.2 16 16s-7.2 16-16 16l-64 0c-8.8 0-16-7.2-16-16s7.2-16 16-16zm54.2 253.8c-6.1 20.3-24.8 34.2-46 34.2L80 416c-8.8 0-16-7.2-16-16s7.2-16 16-16l8.2 0c7.1 0 13.3-4.6 15.3-11.4l14.9-49.5c3.4-11.3 13.8-19.1 25.6-19.1s22.2 7.7 25.6 19.1l11.6 38.6c7.4-6.2 16.8-9.7 26.8-9.7c15.9 0 30.4 9 37.5 23.2l4.4 8.8 54.1 0c8.8 0 16 7.2 16 16s-7.2 16-16 16l-64 0c-6.1 0-11.6-3.4-14.3-8.8l-8.8-17.7c-1.7-3.4-5.1-5.5-8.8-5.5s-7.2 2.1-8.8 5.5l-8.8 17.7c-2.9 5.9-9.2 9.4-15.7 8.8s-12.1-5.1-13.9-11.3L144 349l-9.8 32.8z"/></svg>
      <!-- <img src="../assets/SidebarImg/contract.svg" alt="Contract Icon" class="icon" /> -->
      <div class="menu-title">계약 관리</div>
    </div>
    <div class="menu-item" @click="goPage('schedule')">
      <img src="../assets/SidebarImg/calendar.png" alt="Calendar Icon" class="icon" />
      <div class="menu-title">스케줄 관리</div>
    </div>
    <div class="menu-item" @click="goPage('CalculatorPage')" v-if="forEmployer">
      <img src="../assets/SidebarImg/dollar-sign.png" alt="Dollar Icon" class="icon" />
      <div class="menu-title">급여 관리</div>
    </div>
    <!-- <div class="menu-item" @click="goPage('commute')">
      <img src="../assets/SidebarImg/credit-card.png" alt="Credit Card Icon" class="icon" />
      <div class="menu-title">근태 관리</div>
    </div> -->
    <div class="menu-item" @click="goPage('notice')">
      <img src="../assets/SidebarImg/edit.png" alt="Edit Icon" class="icon" />
      <div class="menu-title">공지사항</div>
    </div>
    <div class="menu-item" @click="goPage('AdministratorDailyCommuting')">
      <img src="../assets/SidebarImg/alert-circle.png" alt="Help Icon" class="icon" />
      <div class="menu-title">출결관리</div>
    </div>
    <div class="menu-item" @click="goPage('enrollEmployee')" v-if="forEmployee">
      <img src="../assets/SidebarImg/building-user-solid.svg" alt="enrollEmployee Icon" class="icon" />
      <div class="menu-title">근무지 등록</div>
    </div>
    <div class="menu-item" @click="goPage('enrollWorkplace')" v-if="forEmployer">
      <img src="../assets/SidebarImg/building-user-solid.svg" alt="enrollWorkplace Icon" class="icon"/>
      <div class="menu-title">사업장 등록</div>
    </div>
  </div>
</template>

<script setup>
import { useUserStore } from '@/stores/userStore';
import { useRouter } from 'vue-router';

const router = useRouter();

const goPage = (pageName)=>{
  router.push({name : pageName});
}

//메뉴 권한
const userStore = useUserStore();
const forEmployer = userStore.roles.includes("employer");
const forEmployee = userStore.roles.includes("employee");
</script>

<style scoped>
.side-menu {
  position: fixed;
  top : 50px;
  width: 250px;
  height: 100%;
  background-color: #ffffff;
  box-shadow: 0px 10px 60px 0px rgba(226, 236, 249, 0.5);
  border-right: 2px solid #d7dae5;
  z-index: 10;
}
.menu-item {
  display: flex;
  align-items: center;
  padding: 20px;
  cursor: pointer;
  transition: background-color 0.3s ease, color 0.3s ease;
}
.menu-item:hover {
  background-color: #4FD1C5;
}
.menu-item:hover .menu-title{
  color : white;
}
.icon {
  width: 24px;
  height: 24px;
  margin-right: 20px;
} 
svg {
  width: 24px;
  height: 24px;
  margin-right: 20px;
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
.icon-home-header {
  width: 24px;
  height: 24px;
}
</style>
