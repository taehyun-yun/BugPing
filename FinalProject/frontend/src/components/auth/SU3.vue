<template>
    <form class="register-form">
        <div class="input-group">
            <img src="/src/assets/Loginimg/user.svg">
            <input type="text" class="input-field" placeholder="사업체명">
        </div>
        <div class="input-group">
            <img src="/src/assets/Loginimg/store-solid.svg">
            <input type="text" class="input-field" placeholder="사업체명">
        </div>
        <SU_address></SU_address>
        <div class="input-group">
            <img src="/src/assets/Loginimg/user.svg">
            <input type="tel" class="input-field" placeholder="대표번호">
        </div>
        <button v-if="usertype=='employee'" type="button" class="signup-button">회원 가입</button>
    </form>
    {{ usertype }}
</template>
<script setup>
import { useAuthStore } from '@/stores/authStore';
import SU_address from './SU_address.vue';

const usertype = useAuthStore().userType;
</script>
<style scoped>
    .register-form {
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
    }
    .signup-button {
        background-color: #4FD1C5;
        color: #ffffff;
        border: none;
        padding: 15px;
        font-size: 1rem;
        border-radius: 8px;
        cursor: pointer;
        transition: background-color 0.3s ease;
    }

    .signup-button:hover {
        background-color: lightseagreen;
    }
</style>