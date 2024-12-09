<template>
    <form class="register-form">
        <div class="input-group">
            <img src="/src/assets/Loginimg/user.svg">
            <input type="text" class="input-field" placeholder="아이디">
        </div>
        <div class="input-group">
            <img src="/src/assets/Loginimg/lock.svg">
            <input type="password" class="input-field" placeholder="비밀번호">
        </div>
        <div class="input-group">
            <img src="/src/assets/Loginimg/address.svg">
            <input type="text" class="input-field" placeholder="이름">
        </div>
        <div class="input-group">
            <img src="/src/assets/Loginimg/tablet.svg">
            <input type="tel" class="input-field" placeholder="전화번호">
        </div>
        <div class="input-group">
            <img src="/src/assets/Loginimg/calendar.svg">
            <input type="text" class="input-field" placeholder="생년월일">
        </div>
        <div class="input-group">
            <img src="/src/assets/Loginimg/address.svg">
            <input type="radio" name="gender" value="M" id="M">
            <label for="M" class="radiolabel">남성</label>
            <input type="radio" name="gender" value="F" id="F">
            <label for="F" class="radiolabel">여성</label>
        </div>
        <br>
        <button v-if="usertype=='employee'" type="button" class="signup-button">회원 가입</button>
        <button v-if="usertype=='employer'" type="button" class="signup-button" @click="gonext('su3')">다음으로</button>
    </form>
    {{ usertype }}
</template>
<script setup>
import router from '@/router';
import { useAuthStore } from '@/stores/authStore';

const usertype = useAuthStore().userType;
const gonext = () => {
    router.push({name : 'su3'})
}

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