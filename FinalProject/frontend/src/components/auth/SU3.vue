<template>
    <div class="register-box">
        <div class="input-group">
            <img src="/src/assets/Loginimg/user.svg">
            <input type="text" class="input-field" placeholder="아이디" v-model="localdata.userId" required>
        </div>
        <div class="input-group">
            <img src="/src/assets/Loginimg/lock.svg">
            <input :type="showInputPw?'text':'password'" class="input-field" placeholder="비밀번호" v-model="localdata.password" required minlength="4">
            <img :src="showInputPw?'/src/assets/Loginimg/eye-solid.svg':'/src/assets/Loginimg/eye-slash.svg'" @click="changeType">
        </div>
        <div class="input-group">
            <img src="/src/assets/Loginimg/address.svg">
            <input type="text" class="input-field" placeholder="이름" v-model="localdata.name" required>
        </div>
        <div class="input-group">
            <img src="/src/assets/Loginimg/tablet.svg">
            <div class="tel-container">
                <p>휴대번호</p>
                <input type="num" class="input-field divide-box" v-model="t.num1" minlength="3" maxlength="3" @input="nextinput($event,3)" required>
                <input type="num" class="input-field divide-box" v-model="t.num2" minlength="4" maxlength="4" @input="nextinput($event,4)" required>
                <input type="num" class="input-field divide-box" v-model="t.num3" minlength="4" maxlength="4" @input="nextinput($event,4)" required>
            </div>
        </div>
        <div class="input-group">
            <img src="/src/assets/Loginimg/envelope-regular.svg">
            <div class="email-container">
                <input type="email" class="input-field" placeholder="계정 분실시 사용할 이메일(선택)" maxlength="20" v-model="inputEmail">
                <button type="button" @click="sendCode">
                    <img v-show="wait" style="margin : 0 ;"src="/src/assets/Loginimg/Dual Ring.svg">
                    <div v-show="!wait">{{sendButtonMsg}}</div>
                </button>
            </div>
        </div>
        <div class="input-group" v-show="useEmail">
            <img src="/src/assets/Loginimg/envelope-regular.svg">
            <div class="email-container">
                <input type="text" class="input-field" placeholder="인증번호" maxlength="20" v-model="inputCode">
                <button type="button" @click="checkCode">확인</button>
            </div>
        </div>
        <div class="input-group">
            <img src="/src/assets/Loginimg/calendar.svg">
            <input type="date" class="input-field" placeholder="생년월일" v-model="localdata.birth" required>
        </div>
        <div class="input-group">
            <img src="/src/assets/Loginimg/address.svg">
            <input type="radio" name="gender" value="M" id="M" v-model="localdata.gender">
            <label for="M" class="radiolabel">남성</label>
            <input type="radio" name="gender" value="F" id="F" v-model="localdata.gender">
            <label for="F" class="radiolabel">여성</label>
        </div>
        <br>
    </div>
</template>
<script setup>
import { axiosAddress } from '@/stores/axiosAddress';
import axios from 'axios';
import { computed, reactive, watch, ref } from 'vue';
//비밀번호 보이기 안보이기
const showInputPw = ref(false);
    const changeType = () => {
        showInputPw.value = !showInputPw.value;
    }
//이메일
const useEmail = ref(false);//이메일을 사용할지 말지에 따라 입력폼 등장.
const inputEmail = ref('');
const inputEmailSaved = ref('');
const isCooltime = ref(false);
const currentCooltime = ref(0);
const sendCode = async() =>{
    useEmail.value = true;
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
    axios.post(axiosAddress+"/checkCode",{ inputEmail : inputEmailSaved.value ,inputCode : inputCode.value},{withCredentials: true})
    .then((res)=>{
        if(res.data){
            alert(inputEmailSaved.value + "인증되었습니다.")
            localdata.email = inputEmailSaved.value;
        } else {
            alert("인증번호가 일치하지 않습니다. 다시 발급 받아주세요.");
            localdata.email = "";
        }
    })
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
//전화번호 input 3개 합치기
const t = reactive({
    num1 : '',
    num2 : '',
    num3 : '',
});
const tel = computed(()=>{
    return t.num1+"-"+t.num2+"-"+t.num3
});
watch(tel,(newValue)=>{localdata.tel = newValue});

const localdata = reactive({
    userId : '',
    password : '',
    name : '',
    tel : '',
    email : '',
    birth : '',
    gender : '',
});

const emit = defineEmits(['update']);
watch (()=> localdata,(newData) => emit('update',newData), {deep : true});
//다음 input으로 자동으로 넘어가게 하기
const nextinput = (e,num) =>{
    if(e.target.value.length >= num){
        const inputs = Array.from(document.querySelectorAll('.input-field'));
        const currentIndex = inputs.indexOf(e.target);
        if(currentIndex < inputs.length -1){
            inputs[currentIndex + 1 ].focus();
        }
    }
}
</script>
<style scoped>
    .register-box {
        width: 100%;
        display: flex;
        flex-direction: column;
        gap: 15px;
    }
    .input-group {
        display: flex;
        align-items: center;
        border: 1px solid silver;
        border-radius: 8px;
        padding: 10px;
        margin-top: 25px;
        background-color: #f9f9f9;
    }
    .input-group img{
        width: 20px;
        height: 20px;
        margin-left: 30px;
        margin-right: 10px;
    }
    .input-field {
        border: none;
        outline: none;
        background: none;
        width: calc(100% - 80px);
        text-align: center;
        font-size: 20px;
    }
    .input-group input[type="radio"]{
        display: none;
    }
    .radiolabel {
        cursor: pointer;
        width: calc(50% - 30px);
        text-align: center;
    }
    .input-group input[type="radio"]:checked + .radiolabel {
        color: #4FD1C5;
        font-weight: bold;
    }
    .tel-container{
        display: inline-flex;
        width: calc(100% - 60px);
        justify-content: space-evenly;
    }
    .tel-container p{
        color : silver;
        margin : 0;
        width: 120px;
    }
    .divide-box{
        border : solid 1px silver;
        width: 80px;
        padding-left: 10px;
        padding-right: 10px;
        text-align: center;
        border-radius: 5px;
        background-color: white;
    }
    button {
        width: 200px;
        display: inline-block;
        background-color: #4FD1C5;
        color: #ffffff;
        border: none;
        padding: 10px;
        font-size: 1rem;
        border-radius: 8px;
        cursor: pointer;
        transition: background-color 0.3s ease;
    }
    button:hover {
        background-color: lightseagreen;
    }
    .email-container{
        display: inline-flex;
        width: calc(100% - 60px);
        justify-content: space-evenly;
    }
    .email-container input{
        width: calc(100% - 200px);
    }
</style>