<template>
    <div class="input-group">
        <img src="/src/assets/Loginimg/map-location-dot-solid.svg">
        <input type="text" id="sample4_postcode" placeholder="우편번호" readonly>
        <input type="button" @click="sample4_execDaumPostcode()" value="우편번호 찾기"><br>
        <input type="text" id="sample4_roadAddress" placeholder="도로명주소" readonly><br>
        <input type="text" id="sample4_jibunAddress" placeholder="지번주소" readonly>
        <input type="text" id="sample4_detailAddress" placeholder="상세주소">
    </div>
</template>
<script setup>
import { onMounted } from 'vue';

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
const sample4_execDaumPostcode = () => {
    new daum.Postcode({
        oncomplete: function(data) {
            document.getElementById('sample4_postcode').value = data.zonecode;
            document.getElementById("sample4_roadAddress").value = data.roadAddress;
            document.getElementById("sample4_jibunAddress").value = data.jibunAddress;
        }
    }).open();
}
</script>
<style scoped >
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
</style>