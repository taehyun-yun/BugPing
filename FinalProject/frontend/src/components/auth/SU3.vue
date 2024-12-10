<template>
    <div class="register-box">
        <div class="input-group">
            <img src="/src/assets/Loginimg/user.svg">
            <input type="text" class="input-field" placeholder="아이디" v-model="localdata.userId" required>
        </div>
        <div class="input-group">
            <img src="/src/assets/Loginimg/lock.svg">
            <input type="password" class="input-field" placeholder="비밀번호" v-model="localdata.password" required>
        </div>
        <div class="input-group">
            <img src="/src/assets/Loginimg/address.svg">
            <input type="text" class="input-field" placeholder="이름" v-model="localdata.name" required>
        </div>
        <div class="input-group">
            <img src="/src/assets/Loginimg/tablet.svg">
            <input type="tel" class="input-field" placeholder="전화번호" v-model="localdata.tel" required>
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
import { reactive, watch } from 'vue';

const localdata = reactive({
    userId : '',
    password : '',
    name : '',
    tel : '',
    birth : '',
    gender : '',
})
const emit = defineEmits(['update']);
watch (()=> localdata,(newData) => emit('update',newData), {deep : true});
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
</style>