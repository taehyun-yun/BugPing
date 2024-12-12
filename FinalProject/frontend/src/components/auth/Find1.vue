<template>
    <div class="signup-button-container">
        {{ currentCooltime }} <br>
        {{ isCooltime }}
        <button @click="sendcooltime">인증번호발송</button>
    </div>
</template>
<script setup>
import { axiosAddress } from '@/stores/axiosAddress';
import axios from 'axios';
import { ref } from 'vue';

    const isCooltime = ref(false);
    const currentCooltime = ref(0);
    const sendcooltime = () =>{
        if(currentCooltime.value == 0 ){
            isCooltime.value =  true;
            currentCooltime.value = 5;
            //currentCooltime.value = 180;
            const cooldown = setInterval(()=>{currentCooltime.value -= 1;},1000);
            setTimeout(()=>{
            isCooltime.value = false;
            currentCooltime.value = 0;
            clearInterval(cooldown);
            sendcode();
            }, 5 * 1000);
            //}, 3 * 60 * 1000);
        }
    }
    const sendcode = () =>{
        axios.post(axiosAddress+"/sendcode",null,{ withCredentials : true })
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