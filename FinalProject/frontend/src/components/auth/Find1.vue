<template>
    <div class="signup-button-container">
        <input type="email" maxlength="20" v-model="userEmail">
        <br>{{ userEmail }}
        <br>{{ currentCooltime }}
        <br>{{ isCooltime }}
        <button @click="sendcooltime">인증번호발송</button>
    </div>
</template>
<script setup>
import { axiosAddress } from '@/stores/axiosAddress';
import axios from 'axios';
import { ref } from 'vue';
    const userEmail = ref('');
    const isCooltime = ref(false);
    const currentCooltime = ref(0);
    const sendcooltime = async() =>{
        if(currentCooltime.value == 0 ){
            isCooltime.value =  true;
            currentCooltime.value = 5;
            //currentCooltime.value = 180;
            await sendcode();
            const cooldown = setInterval(()=>{currentCooltime.value -= 1;},1000);
            setTimeout(()=>{
            isCooltime.value = false;
            currentCooltime.value = 0;
            clearInterval(cooldown);
            }, 5 * 1000);
            //}, 3 * 60 * 1000);
        }
    }
    const sendcode = async() =>{
        await axios.post(axiosAddress+"/sendCode",{ userEmail : userEmail.value},{ withCredentials : true })
        .then((res)=>{
            alert(res.data);
        })
    }
</script>
<style scoped>
    .signup-button-container{
        width: 100%;
    }
    button {
        width: 100%;
        display: block;
        margin-top: 30px;
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