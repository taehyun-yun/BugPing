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
          <div class="search-fieldset">
            <input type="hidden" placeholder="검색" class="header-search-input" v-model="selectedCompany" />
            <select v-model="selectedCompany" @change="updateCompany">
              <option v-for="company in userStore.companies" :key="company.id" :value="company.cname">
                {{ company.cname }}
              </option>
            </select>
          </div>
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
                  <div class="row-elem" @click="showModalChange">
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
    <Teleport to="body">
      <div class="modal-overlay" @click.self="showModalChange" v-show="showModal">
        <div class="modal">
          <div>
            <form ref="pwForm">
              <p>비밀번호 변경</p>
              <input type="text" placeholder="비밀번호 입력" required v-model="newPassword" minlength="4">
              <button type="button" @click="setNewEmail">변경</button>
            </form>
          </div>
          <div v-if="!registered">
            <form ref="emailForm">
              <p>이메일 등록</p>
              <input type="email" placeholder="이메일 입력" required>
              <button type="button" @click="registEmail">변경</button>
            </form>
          </div>
          <div>
            <p @click="">탈퇴</p>
          </div>
        </div>
      </div>
    </Teleport>
  </template>

<script setup>
import { axiosAddress } from '@/stores/axiosAddress';
import { useUserStore } from '@/stores/userStore';
import axios from 'axios';
import { ref, watch, onMounted, computed } from 'vue';
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
//누르면 app.vue에 있는 sidebar와 연결됨
const showOrNot = ref(useRoute().meta.sidebar);
const emit = defineEmits(['showSidebar']);
const sidebar = () =>{
  showOrNot.value = !showOrNot.value;
  emit('showSidebar',showOrNot.value);
}

const userStore = useUserStore();
const selectedCompany = ref('');

// 초기값 설정
onMounted(() => {
  if (userStore.companies.length > 0) {
    selectedCompany.value = userStore.companies[0].cname;
    userStore.setCompany(selectedCompany.value);
  }
});

// 선택된 값 업데이트
const updateCompany = () => {
  userStore.setCompany(selectedCompany.value);
};

// watch로 상태 동기화
watch(selectedCompany, (newValue) => {
  userStore.setCompany(newValue);
});
// 마이페이지
const showModal = ref(false);
const showModalChange = () =>{
  showModal.value = !showModal.value;
}
const registered = computed(() => userStore.email !== '');
const emailForm = ref(null);
const newPassword = ref('');
const setNewEmail = () =>{
  if(pwForm.value.reportValidity()){
    axios.post(`${axiosAddress}/setNewPassword`,{ userId : userStore.userId, newPassword : newPassword},{withCredentials : true})
  }
}
const pwForm = ref(null);
const registEmail = () =>{
  if(emailForm.value.reportValidity()){

  }
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
  .search-fieldset select{
    appearance: none; /* 브라우저 기본 스타일 제거 */
    -webkit-appearance: none;
    -moz-appearance: none;
    width: 100%;
    padding: 8px 12px;
    font-size: 14px;
    color: #fff;
    border: none;
    background: transparent;
    cursor: pointer;
    outline: none;
  }
  .search-fieldset::after{
    content: "▼"; /* 화살표 */
    position: absolute;
    top: 50%;
    right: 10px;
    transform: translateY(-50%);
    color: #aaa;
    pointer-events: none; /* 클릭 방지 */
  }
  .search-fieldset:focus{
    outline: none;
    border: none;
    box-shadow: 0 0 5px mediumaquamarine;
  }
  .search-fieldset option{
    background: #fff;
    outline: none;
    color: #333;
    font-size: 14px;
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
  /* 마이페이지 */
  .modal-overlay {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.6);
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 999;
  }
  .modal {
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        padding: 20px;
        background-color: #ffffff;
        border-radius: 16px;
        width: 600px;
        max-width: 90%;
        box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: space-between;
        gap: 20px;
  }

</style>