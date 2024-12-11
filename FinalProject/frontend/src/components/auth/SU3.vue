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
            <div class="tel-container">
                <p>전화번호</p>
                <input type="num" class="input-field divide-box" v-model="t.num1" minlength="3" @input="nextinput($event,3)" required>
                <input type="num" class="input-field divide-box" v-model="t.num2" minlength="4" @input="nextinput($event,4)" required>
                <input type="num" class="input-field divide-box" v-model="t.num3" minlength="4" @input="nextinput($event,4)" required>
            </div>
        </div>
        <div class="input-group">
            <img src="/src/assets/Loginimg/envelope-regular.svg">
            <input type="email" class="input-field" placeholder="아이디, 비밀번호 분실시 사용할 이메일(선택)" v-model="localdata.email">
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
import { computed, reactive, watch } from 'vue';
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
</style>