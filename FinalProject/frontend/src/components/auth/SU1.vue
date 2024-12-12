<template>
    <div class="signup-button-container">
    <button @click="golink('su2','employee')">고용인 회원가입</button>
    <button @click="golink('su2','employer')">고용주 회원가입</button>
    </div>
</template>
<script setup>
import router from '@/router';
import { useAuthStore } from '@/stores/authStore';
const golink = (e,t) =>{
    const authStore = useAuthStore();
    authStore.setUserType(t);
    router.push({name: e});
} 
</script>
<style scoped>
    .signup-button-container{
        width: 100%;
        height: 100%;
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