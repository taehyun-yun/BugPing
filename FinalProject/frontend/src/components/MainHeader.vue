<template>
    <div class="header-wrapper">
      <div class="header">
        <div class="header-left">
          <div class="header-icons">
            <img src="/src/assets/MainheaderImg/bars-solid.svg" class="icon" @click="sidebar">
            <span class="header-title">운영의 달인</span>
          </div>
        </div>
        <div class="header-right">
          <!-- <div class="search-fieldset">
            <img src="../assets/MainheaderImg/header-search.png" alt="Search Icon" class="search-icon" />
            <input type="text" placeholder="검색" class="header-search-input" />
          </div> -->
          <div class="header-icons">
            <!-- <img src="../assets/MainheaderImg/book.png" alt="Book Icon" class="icon" />
            <img src="../assets/MainheaderImg/cloud.png" alt="Cloud Icon" class="icon" />
            <img src="../assets/MainheaderImg/message.png" alt="ChannelTalk Icon" class="icon" /> -->
            <div class="column">
              <img src="../assets/MainheaderImg/bell.png" alt="Bell Icon" class="icon" />
              <div class="row"></div>
            </div>
            <div class="column">
              <img src="../assets/MainheaderImg/circle-user-solid.svg" alt="profile Icon" class="icon" />
                <div class="row">
                  <div class="row-elem" @click="goToLoginPage">
                      <img src="../assets/MainheaderImg/gear-solid.svg" alt="Gear Icon" class="icon"/> 마이페이지
                  </div>
                  <div class="row-elem" @click="goToLoginPage">
                      <img src="../assets/MainheaderImg/right-from-bracket-solid.svg" alt="Logout Icon" class="icon"/> 로그아웃
                  </div>
                </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>

<script setup>
import { axiosAddress } from '@/stores/axiosAddress';
import axios from 'axios';
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
const router = useRouter();

const goToLoginPage = async() => {
  await axios
  .get(axiosAddress+"/logout",{withCredentials : true})
  .then(()=>{
    alert("로그아웃되었습니다.")
  })
  router.push("/login");
};
const showOrNot = ref(useRoute().meta.sidebar);
const emit = defineEmits(['showSidebar']);
const sidebar = () =>{
  showOrNot.value = !showOrNot.value;
  emit('showSidebar',showOrNot.value);
}

</script> 
<style scoped>
  .header-wrapper {
      width: 100%;
      position: fixed;
      top: 0;
      left: 0;
      z-index: 100;
      white-space: nowrap;
  }
  
  .header {
      background: var(--dashboardshoplworkscom-mine-shaft, #333333);
      border-style: solid;
      border-color: var(--dashboardshoplworkscom-mine-shaft, #333333);
      border-width: 0 0 1px 0;
      height: 40px;
      display: flex;
      justify-content: space-between;
      /* align-items: center; */
      padding: 5px 5px;
  }
  
  .header-left .header-title {
      color: var(--dashboardshoplworkscom-white, #ffffff);
      font-family: "Inter-Medium", sans-serif;
      font-size: 16px;
      line-height: 20px;
      font-weight: 500;
      align-items: center;
  }
  
  .header-right {
      display: flex;
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
      font-size: 12px;
      font-weight: 500;
  }
  
  .header-icons {
    margin-top: 10px;
    display: flex;
    /* align-items: center; */
  }
  
  .icon {
      width: 24px;
      height: 24px;
      margin-right: 16px;
  }
  .icon:hover{
    cursor: pointer;
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
  .row{
    display: none;
  }
  .column{
    position: relative;
  }
  .column:hover .row{
    position: absolute;
    right: -10px;
    display: block;
  }
  .row-elem{
    background-color: #333333;
    padding: 5px;
    display: block;
    color : #4FD1C5;
    cursor: pointer;
    line-height: 1;
    display: flex;
    align-items: center;
  }
</style>