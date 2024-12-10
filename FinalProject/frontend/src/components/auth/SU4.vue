<template>
<div class="register-box">
    <div class="input-group">
        <img src="/src/assets/Loginimg/user.svg">
        <input type="text" class="input-field" placeholder="사업체명" v-model="localdata.cname" required>
    </div>
    <div class="input-group">
        <img src="/src/assets/Loginimg/store-solid.svg">
        <div class="licence-number-container">
        <p>사업자 등록번호</p>
        <input type="number" class="input-field licence-number" maxlength="3" minlength="3" v-model="licence.l1" @input="nextinput($event,3)" required>
        <input type="number" class="input-field licence-number" maxlength="2" minlength="2" v-model="licence.l2" @input="nextinput($event,2)" required>
        <input type="number" class="input-field licence-number" maxlength="5" minlength="5" v-model="licence.l3" @input="nextinput($event,5)" required>
        </div>
    </div>
    <div class="input-group">
        <img src="/src/assets/Loginimg/user.svg">
        <input type="tel" class="input-field" placeholder="대표번호" v-model="localdata.ctel"required>
    </div>
</div>
</template>
<script setup>
import { computed, reactive, watch, } from 'vue';

const licence = reactive({
    l1 : '',
    l2 : '',
    l3 : '',
})

const cnum = computed(()=>{
    return licence.l1+'-'+licence.l2+'-'+licence.l3
})
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
//cnum바뀌면 local.cnum도 변경
watch(cnum,(newValue) => {
    localdata.cnum = newValue;
})
//부모에게 보내기
const localdata = reactive({
    cname : '',
    cnum : '',
    ctel : '',
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
    .licence-number-container{
        display: inline-flex;
        width: calc(100% - 60px);
        justify-content: space-evenly;
    }
    .licence-number-container p{
        color : silver;
        margin : 0;
        width: 130px;
    }
    .licence-number{
        border : solid 1px silver;
        width: 80px;
        padding-left: 10px;
        padding-right: 10px;
        text-align: center;
        border-radius: 5px;
        background-color: white;
    }
    /* Chrome, Safari, Edge, Opera */
    input::-webkit-outer-spin-button,
    input::-webkit-inner-spin-button {
    -webkit-appearance: none;
    margin: 0;
    }
</style>