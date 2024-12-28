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
            <select v-model="selectedCompany">
              <option v-for="company in userStore.companies" 
                      :key="company.id" 
                      :value="company">
                {{ company.cname }}
              </option>
            </select>
          </div>
          <div class="header-icons">
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
          <p>{{ userStore.userId }}</p>
          <div class="item">
            <p>비밀번호 변경</p>
            <form ref="pwCheck" v-show="!valid">
              <input type="text" placeholder="현재 비밀번호를 입력해주세요" required v-model="inputPassword" minlength="4">
              <button type="button" @click="checkPassword">확인</button>
            </form>
            <form ref="pwForm" v-show="valid">
              <input type="text" placeholder="새로운 비밀번호를 입력해주세요" required v-model="newPassword" minlength="4">
              <button type="button" @click="setNewPassword">변경</button>
            </form>
          </div>
          <div class="item" v-if="!registered">
            <form ref="emailForm">
              <p>이메일 등록</p>
              <input type="email" placeholder="이메일 입력" required v-model="inputEmail">
              <button class="send-button" @click="sendCode" type="button">
                <img v-show="wait" style="margin : 0 ; width: 20px; height: 20px; " src="/src/assets/Loginimg/Dual Ring.svg">
                <div v-show="!wait">{{sendButtonMsg}}</div>
              </button>
            </form>
            <div class="input-group">
              <input type="text" class="input-field" placeholder="인증번호 입력" maxlength="20" v-model="inputCode">
              <button class="send-button" @click="checkCode" type="button">확인</button>
            </div>
          </div>
          <div class="item" v-if="registered">
            <p>이메일 : {{ userStore.email }}</p>
          </div>
          <!-- <div>
            <p @click="">탈퇴</p>
          </div> -->
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
//누르면 app.vue에 있는 sidebar와 연결됨-----------------------------------------------
const showOrNot = ref(useRoute().meta.sidebar);
const emit = defineEmits(['showSidebar']);
const sidebar = () =>{
  showOrNot.value = !showOrNot.value;
  emit('showSidebar',showOrNot.value);
}

// 회사 설정 --------------------------------------
const userStore = useUserStore();
const selectedCompany = ref({ cname: '', companyId: '' });

// 초기값 설정
onMounted(() => {
  if (userStore.companies.length > 0) {
    selectedCompany.value = userStore.company;
  }
});
// watch로 상태 동기화
let initialLoad = true; // 첫 로드 여부를 추적

watch(selectedCompany, (newValue, oldValue) => {
  if (!initialLoad) {
    userStore.setCompany(newValue);

    // 특정 조건에서만 reload
    if (newValue !== oldValue) {
      window.location.reload();
    }
  }
  initialLoad = false; // 첫 로드 후 초기화
});

// 마이페이지-----------------------------------------------
const showModal = ref(false);
const showModalChange = () =>{
  showModal.value = !showModal.value;
}
 //비밀번호 변경
const pwForm = ref(null);
const newPassword = ref('');
const inputPassword = ref('');
const valid = ref(false);
 //현재 비밀번호 입력
const checkPassword = async() =>{
  try{
    //로그인 유효성 체크
    const res = await axios.post(`${axiosAddress}/login`,{
    "userId" : userStore.userId,
    "password" : inputPassword.value,
    },{ withCredentials: true })
    valid.value = true;
    } catch (err){
      alert(err.response.data.msg);
    }
}
 // 새로운 비밀번호 입력
const setNewPassword = () =>{
  if(pwForm.value.reportValidity()){
    axios.post(`${axiosAddress}/setNewPassword`,{ userId : userStore.userId, newPassword : newPassword.value },{withCredentials : true})
  }
}

const registered = computed(() => userStore.email && userStore.email !== '');
const inputEmail = ref('');
const inputEmailSaved = ref('');
const isCooltime = ref(false);
const currentCooltime = ref(0);

const sendCode = async() =>{
    if(currentCooltime.value == 0 ){
        //쿨타임 초기화
        isCooltime.value =  true;
        currentCooltime.value = 10;
        //현재 입력한 이메일을 저장
        inputEmailSaved.value = inputEmail.value;
        //로딩이미지 보이기 설정
        wait.value = true;
        //메세지 전송
        await axios.post(axiosAddress+"/sendCode",{ inputEmail : inputEmailSaved.value},{ withCredentials : true })
        .then((res)=>{
            alert(res.data);
        })
        //로딩이미지 안보이기 설정
        wait.value = false;
        //쿨타임
        const cooldown = setInterval(()=>{currentCooltime.value -= 1;},1000);
        setTimeout(()=>{
        isCooltime.value = false;
        currentCooltime.value = 0;
        clearInterval(cooldown);
        }, 1 * 10 * 1000);
    }
}
const inputCode = ref(''); 
const checkCode = async() =>{
  try {
  const res = await axios.post(axiosAddress + "/checkCode", {
    inputEmail: inputEmailSaved.value,
    inputCode: inputCode.value,
  }, { withCredentials: true });

  if (res.data) {
    alert(inputEmailSaved.value + " 인증되었습니다.");
    const setEmailRes = await axios.post(axiosAddress + "/setEmail", {
      email: inputEmailSaved.value,
    }, { withCredentials: true });
    userStore.email = inputEmailSaved.value;
    alert(setEmailRes.data);
    } else {
      alert("인증번호가 일치하지 않습니다. 다시 발급받아 주세요.");
    }
  } catch (error) {
    console.error("Error:", error);
    alert("요청 중 문제가 발생했습니다. 다시 시도해 주세요.");
  }
}
const sendButtonMsg = ref('인증번호 발송');
const wait = ref(false);
watch(currentCooltime,(newValue)=>{
    if(newValue != 0){
        sendButtonMsg.value = newValue;
    } else {
        sendButtonMsg.value = '인증번호 발송';
    }
},{ deep : true})

// 로그아웃 ----------------------
const goToLoginPage = async() => {
  await axios
  .get(axiosAddress+"/logout",{withCredentials : true})
  .then(()=>{
    //pinia persist true로 인해 localstorage에 저장된 데이터 삭제
    userStore.$reset();
    alert("로그아웃되었습니다.")
  })
  router.push("/login");
};
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
      width: 180px;
      margin-right: 80px;
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
.item{
  width: 100%;
}
    /* 헤더 텍스트 */
  .modal .item p {
    font-size: 16px;
    font-weight: bold;
    color: #333;
    margin-bottom: 8px;
  }

  /* 입력 필드 */
  .modal input[type="text"],
  .modal input[type="email"] {
    width: 70%;
    padding: 10px;
    font-size: 14px;
    border: 1px solid #ddd;
    border-radius: 4px;
    box-sizing: border-box;
  }

  .modal input:focus {
    outline: none;
    border-color: #4FD1C5; /* 포커스 색상 */
    box-shadow: 0 0 4px rgba(79, 209, 197, 0.5); /* 부드러운 강조 */
  }

  /* 버튼 스타일 */
  .modal button {
    width: 30%;
    padding: 10px;
    font-size: 14px;
    font-weight: bold;
    color: white;
    background-color: #4FD1C5; /* 밝은 초록 계열 */
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.2s ease;
  }

  .modal button:hover {
    background-color: #3ba69c; /* 살짝 어두운 초록 */
  }

  /* 탈퇴 텍스트 */
  /* .modal > div:last-child p {
    margin-top: 12px;
    font-size: 12px;
    color: #ff6666;
    text-align: center;
    cursor: pointer;
    transition: color 0.2s ease;
  }

  .modal > div:last-child p:hover {
    color: #e60000;
  } */

</style>