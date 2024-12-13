<template>
    <div class="find-container">
        <div class="input-group">
        <input type="email" class="input-field" maxlength="20" v-model="inputEmail">
        <button @click="sendcode">인증번호발송</button>
        </div>
        <div class="input-group">
        <input type="text" class="input-field" maxlength="20" v-model="inputCode">
        <button @click="checkCode">확인</button>
        </div>
    </div>
    <br>{{ inputEmail }}
    <br>{{ currentCooltime }}
    <br>{{ isCooltime }}
</template>
<script setup>
import { axiosAddress } from '@/stores/axiosAddress';
import axios from 'axios';
import { ref } from 'vue';
    const inputEmail = ref('');
    const inputEmailSaved = ref('');
    const isCooltime = ref(false);
    const currentCooltime = ref(0);
    const sendcode = async() =>{
        if(currentCooltime.value == 0 ){
            //쿨타임 초기화
            isCooltime.value =  true;
            currentCooltime.value = 5;
            //currentCooltime.value = 180;
            inputEmailSaved.value = inputEmail.value;
            //메세지 전송
            await axios.post(axiosAddress+"/sendCode",{ inputEmail : inputEmailSaved.value},{ withCredentials : true })
            .then((res)=>{
                alert(res.data);
            })
            //쿨타임
            const cooldown = setInterval(()=>{currentCooltime.value -= 1;},1000);
            setTimeout(()=>{
            isCooltime.value = false;
            currentCooltime.value = 0;
            clearInterval(cooldown);
            }, 5 * 1000);
            //}, 3 * 60 * 1000);
        }
    }
    const inputCode = ref(''); 
    const checkCode = async() =>{
        axios.post(axiosAddress+"/checkCode",{ inputEmail : inputEmailSaved.value ,inputCode : inputCode.value},{withCredentials: true})
        .then((res)=>{
            alert(res.data);
        })
        .catch((err)=>{
            alert(err);
        })
    }
</script>
<style scoped>
    .find-container{
        width: 100%;
    }
    .input-group {
        display: flex;
        align-items: center;
        justify-content: center;
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
        width: calc(100% - 210px);
        text-align: center;
        font-size: 20px;
    }
    button {
        width: 200px;
        display: inline-block;
        background-color: #4FD1C5;
        color: #ffffff;
        border: none;
        padding: 15px;
        font-size: 1rem;
        border-radius: 8px;
        cursor: pointer;
        transition: background-color 0.3s ease;
    }
    button:hover {
        background-color: lightseagreen;
    }
</style>