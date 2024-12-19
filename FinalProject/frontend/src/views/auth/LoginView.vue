<template>
    <div class="login-container">
        <div class="left-section">
            <div class="loginform">
            <LoginForm></LoginForm>
            </div>
        </div>
        <div class="right-section">
            <img src="/src/assets/Loginimg/logo.png" alt="로고" draggable="false" class="logo">
        </div>
    </div>
</template>

<script setup>
import LoginForm from '@/components/auth/LoginForm.vue';
</script>

<style scoped>
.login-container{
    display: flex;
    height: 100vh;
}
.left-section,
.right-section{
    height: 100%;
    display: flex;
    flex-direction: column;
}
.left-section{
    width: 400px;
}
.right-section{
    width: calc(100% - 400px);
    background-color: #4FD1C5;
    position: relative;
}
.right-section img{
    position:absolute;
    top:50%;
    left:50%;
    transform: translate(-50%, -50%);
}
.loginform{
    padding-left :50px;
    padding-right :50px;
    z-index: 2;
}
.logo{
    width: 250px;
    height: 250px;
    z-index: 1;
}
</style>
