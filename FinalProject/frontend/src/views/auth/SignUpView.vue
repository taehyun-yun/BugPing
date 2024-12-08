<template>
<div class="signup-container">
    <div class="top"></div>
    <div class="bottom"></div>
</div>
<div class="signup-modal">

</div>
</template>
<script setup>

</script>
<style scoped>
    .signup-container{
        display : flex;
        position: relative;
        width: 100vw;
        height: 100vh;
    }
    .top{
        background-color: #4FD1C5;
        display: block;
        width: 100%;
        height: 50%;
    }
    .bottom{
        display: block;
        width: 100%;
        height: 50%;
    }
    .signup-modal{
        border: 1px solid #e2e8f0;
        border-radius: 50px;
        background-color: white;
        width: 1000px;
        height: 600px;
        position: absolute;
        bottom: 0%;
        left : 50%;
        transform: translateX(-50%);
    }
</style>