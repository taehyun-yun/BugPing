<template>
    <div class="login-title">로그인</div>
    <form class="login-form">
    <div class="input-group">
        <img src="/src/assets/Loginimg/user.svg" alt="User Icon">
        <input type="text" class="input-field" placeholder="아이디" v-model="userId" @keyup.enter="login">
    </div>
    <div class="input-group">
        <img src="/src/assets/Loginimg/lock.svg" alt="Password Icon">
        <input type="password" class="input-field" placeholder="비밀번호" v-model="password" @keyup.enter="login">
        <img src="/src/assets/Loginimg/eye-slash.svg">
    </div>
    <button type="button" class="login-button" @click="login">로그인</button>
    </form>
    <div class="login-links">
    <p>아이디/비밀번호 찾기</p>
    <p>회원가입</p>
    </div>
    <button type="button" @click="getRole">확인용</button>
</template>
<script setup>
import axios from 'axios';
import { ref } from 'vue';
import { axiosAddress } from '@/stores/axiosAddress';
    const userId = ref('');
    const password = ref('');
    const login = () =>{
        axios
        .post(axiosAddress+"/login",{
            "userId" : userId.value,
            "password" : password.value
        },{
            withCredentials: true
        })
        .then((res)=>{
            alert(res.data+"페이지로 이동하는 로직 짜셈");
        })
        .catch((err)=>{
            alert(err.response.data);
        })
    }
    const getRole = () =>{
  axios.get(axiosAddress+"/findrole",{withCredentials: true})
  .then((res)=>{
    const roles = res.data.roles.map((role) => role.replace('ROLE_', ''));
    alert(roles);
  })
}
</script>
<style scoped>
    .login-title {
        font-size: 2rem;
        margin-bottom: 20px;
        color: #4FD1C5;
        margin-top: 150px;
        margin-bottom: 150px;
    }

    .login-form {
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
        background-color: #f9f9f9;
    }

    .input-group img {
        width: 20px;
        height: 20px;
        margin-right: 10px;
    }

    .input-field {
        border: none;
        outline: none;
        background: none;
        width: 100%;
        font-size: 1rem;
    }

    .login-button {
        background-color: #4FD1C5;
        color: #ffffff;
        border: none;
        padding: 15px;
        font-size: 1rem;
        border-radius: 8px;
        cursor: pointer;
        transition: background-color 0.3s ease;
    }

    .login-button:hover {
        background-color: lightseagreen;
    }
</style>