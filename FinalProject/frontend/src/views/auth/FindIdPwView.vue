<template>
    <div class="signup-container">
        <div class="top">
            <img class="logo" src="/src/assets/Loginimg/instancelogo2.png" @click="golink" draggable="false">
        </div>
        <div class="bottom"></div>
    </div>
    <div class="signup-modal">
        <p class="modal-title">아이디/비밀번호 찾기</p>
        <div class="modal-content">
            <RouterView/>
        </div>
    </div>
</template>
<script setup>
import router from '@/router';
const golink = () =>{
    router.push({name : 'login'});
}
</script>
<style scoped>
    .signup-container{
        position: relative;
        width: 100vw;
        height: 100vh;
    }
    .top{
        background-color: #4FD1C5;
        width: 100%;
        height: 50%;
    }
    .logo{
        position: absolute;
        top: 50px;
        left: 50px;
        width: 200px;
        height: 150px;
        cursor: pointer;
    }
    .bottom{
        width: 100%;
        height: 50%;
    }
    .signup-modal{
        border: 1px solid #e2e8f0;
        border-top-left-radius: 50px;
        border-top-right-radius: 50px;
        background-color: white;
        width: 800px;
        min-height: 800px;
        position: absolute;
        top: 25%;
        left : 50%;
        transform: translateX(-50%);
        padding: 100px;
        padding-bottom: 0;
    }
    @media(max-width : 650px){
        .signup-modal{
            transform: translateX(0%);
            left: 0px;
            padding: 30px;
            width: 650px;
        }
    }
    .modal-title{
        color: #4FD1C5;
        font-size: 20px;
        line-height: 30px;
        margin-bottom: 60px;
    }
    .modal-content{
        min-height: 500px;
        justify-content: center;
        align-items: center;
    }
</style>