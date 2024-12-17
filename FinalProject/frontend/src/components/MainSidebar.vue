<template>
  <div class="side-menu">
    <div class="menu-item">
      <img src="../assets/SidebarImg/home.png" alt="Home Icon" class="icon" />
      <div class="menu-title">홈</div>
    </div>
    <div class="menu-item" @click="goPage('schedule')">
      <img src="../assets/SidebarImg/calendar.png" alt="Calendar Icon" class="icon" />
      <div class="menu-title">스케줄 관리</div>
    </div>
    <div class="menu-item" @click="goPage('CalculatorPage')">
      <img src="../assets/SidebarImg/dollar-sign.png" alt="Dollar Icon" class="icon" />
      <div class="menu-title">급여 관리</div>
    </div>
    <div class="menu-item" @click="goPage('commute')">
      <img src="../assets/SidebarImg/credit-card.png" alt="Credit Card Icon" class="icon" />
      <div class="menu-title">근태 관리</div>
    </div>
    <div class="menu-item" @click="goPage('notice')">
      <img src="../assets/SidebarImg/edit.png" alt="Edit Icon" class="icon" />
      <div class="menu-title">공지사항</div>
    </div>
    <div class="menu-item">
      <img src="../assets/SidebarImg/alert-circle.png" alt="Help Icon" class="icon" />
      <div class="menu-title">Help</div>
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
