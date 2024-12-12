<template>
    <div class="input-group">
        <img src="/src/assets/Loginimg/map-location-dot-solid.svg">
        <!-- <input type="text" id="postcode" placeholder="우편번호" readonly required> -->
        <input type="text" id="roadAddress" class="input-field" style="width: calc(100% - 220px);" placeholder="도로명주소" v-model="localdata.address" readonly>
        <!-- <input type="text" id="jibunAddress" class="input-field" style="width: calc(100% - 220px);" placeholder="지번주소" readonly> -->
        <button type="button" @click="execDaumPostcode()" style="width: 150px">주소 등록</button>
    </div>
    <div class="input-group">
        <input type="text" id="detailAddress" class="input-field" placeholder="상세주소" v-model="localdata.detailAddress" required >
    </div>
</template>
<script setup>
import { onMounted, reactive, watch } from 'vue';

function loadDaumPostAPI (){
    return new Promise((resolve)=>{
        const script = document.createElement("script");
        script.src = "//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
        script.onload = resolve;
        document.head.appendChild(script);``
    })
}

onMounted (()=>{
    loadDaumPostAPI();
})
const execDaumPostcode = () => {
    new daum.Postcode({
        oncomplete: function(data) {
            // document.getElementById('postcode').value = data.zonecode;
            // document.getElementById("roadAddress").value = data.roadAddress;
            localdata.address = data.roadAddress;
            // document.getElementById("jibunAddress").value = data.jibunAddress;
        }
    }).open();
}
const localdata = reactive({
    address : '',
    detailAddress : '',
})
const emit = defineEmits(['update']);
watch (()=> localdata,(newData) => emit('update',newData), {deep : true});
</script>
<style scoped >
    .input-group {
        display: flex;
        align-items: center;
        border: 1px solid silver;
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
        text-align: center;
        font-size: 20px;
    }
    button {
        background-color: #4FD1C5;
        color: #ffffff;
        border: none;
        padding: 5px;
        font-size: 1rem;
        border-radius: 8px;
        cursor: pointer;
        transition: background-color 0.3s ease;
    }

    button:hover {
        background-color: lightseagreen;
    }
</style>