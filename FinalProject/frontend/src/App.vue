<template>
   <div class="app">
      <RouterView :key="$route.fullPath"/>
   </div>
</template>
<script setup>
import axios from 'axios';
axios.defaults.withCredentials=true; // 쿠키전달 - 권한확인을 위해 필요
</script>
<style scoped>
   .app{
      min-width: 100vw; /* 플렉스 컨테이너로 설정 */
      min-height: 100vh; /* 화면 전체 높이 설정 */
   }
</style>