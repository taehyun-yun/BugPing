<template>
   <div class="app">
      <RouterView :key="$route.fullPath"/>
   </div>
</template>
<script setup>
import axios from 'axios';

axios.defaults.withCredentials = true;
</script>
<style>
   .app{
      min-width: 100vw; /* 플렉스 컨테이너로 설정 */
      min-height: 100vh; /* 화면 전체 높이 설정 */
   }
   @font-face {
    font-family: 'NPSfontBold';
    src: url('https://fastly.jsdelivr.net/gh/projectnoonnu/noonfonts_2310@1.0/NPSfontBold.woff2') format('woff2');
    font-weight: 700;
    font-style: normal;
   }
   body{
      font-family: 'NPSfontBold';
   }
   input{
      font-family: 'NPSfontBold';
   }
   button{
      font-family: 'NPSfontBold';
   }
</style>