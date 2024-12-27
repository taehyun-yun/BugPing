<template>
    <div class="login-title">로그인</div>
    <form class="login-form">
    <div class="input-group">
        <img src="/src/assets/Loginimg/user.svg" alt="User Icon">
        <input type="text" class="input-field" placeholder="아이디" v-model="userId" @keyup.enter="login">
    </div>
    <div class="input-group">
        <img src="/src/assets/Loginimg/lock.svg" alt="Password Icon">
        <input :type="showInputPw?'text':'password'" class="input-field" placeholder="비밀번호" v-model="password" @keyup.enter="login">
        <img :src="showInputPw?'/src/assets/Loginimg/eye-solid.svg':'/src/assets/Loginimg/eye-slash.svg'" @click="changeType">
    </div>
    <button type="button" class="login-button" @click="login">로그인</button>
    </form>
    <div class="login-links">
    <p @click="golink('find')">아이디/비밀번호 찾기</p>
    <p @click="golink('su1')">회원가입</p>
    </div>
</template>
<script setup>
import axios from 'axios';
import { ref } from 'vue';
import { axiosAddress } from '@/stores/axiosAddress';
import router from '@/router';
import { useUserStore } from '@/stores/userStore';
    const userId = ref('');
    const password = ref('');
    const showInputPw = ref(false);
    const changeType = () => {
        showInputPw.value = !showInputPw.value;
    }

    const login = async() =>{
        try{
            //로그인 유효성 체크
            const res = await axios.post(`${axiosAddress}/login`,{
            "userId" : userId.value,
            "password" : password.value
            },{ withCredentials: true })
            // alert("권한 : "+res.data.roles+"입니다.");
            //피니아 저장
            const userStore = useUserStore();
            userStore.setUserId(res.data.userId);
            userStore.setEmail(res.data.email);
            userStore.setRoles(res.data.roles.split(","));
            const companyRes = await axios.get(`${axiosAddress}/api/getHeaderCompanyList`,{withCredentials : true})
            userStore.setCompany(companyRes.data[0]);
            userStore.setCompanies(companyRes.data);
            router.push({ name : "home"});
            // window.location.reload();
        } catch (err){
            if(err?.response?.data.msg){
                alert(err.response.data.msg);
            }
            router.push({ name : "home"});
        }
    }
    const golink = (e) =>{
        router.push({name: e});
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
        width: calc(100% - 60px);
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
    .login-links{
        padding-top: 30px;
    }
    .login-links p{
        display: inline-block;
        cursor: pointer;
        margin-left: 10px;
        margin-right: 10px;
    }
</style>