<template>
   <div class="app">
      <div v-if="header" class="header">
      <MainHeader></MainHeader>
      </div>
      <div class="appbody">
         <div v-if="sidebar" class="sidebar-container">
            <MainSidebar></MainSidebar>
         </div>
         <div class="routerview-container">
            <RouterView />
         </div>
      </div>
   </div>
</template>

<script setup>
import { computed } from 'vue';
import MainHeader from './components/MainHeader.vue';
import MainSidebar from './components/MainSidebar.vue';
import { useRoute } from 'vue-router';
const route = useRoute();

const header = computed(()=>route.meta.header === true)

const sidebar = computed(()=>route.meta.sidebar === true)
</script>

<style scoped>
   .app{
      text-wrap: nowrap;
   }
   .appbody{
      display: flex;
   }
   .header{
      padding : 15px;
      display: inline-block;
   }
   .sidebar-container{
      width :20%;
      height: 100%;
      display: inline-block;
   }
   .routerview-container{
      width : 100%;
      display: inline-block;
   }
</style>