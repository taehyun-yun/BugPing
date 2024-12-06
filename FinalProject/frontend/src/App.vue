<template>
   <!-- <MainHeader v-if="header" />
   <MainSidebar v-if="sidebar" /> -->
   <RouterView />
 </template>
 
 <script setup>
//  import { computed } from 'vue';
//  import MainHeader from './components/MainHeader.vue';
//  import MainSidebar from './components/MainSidebar.vue';
//  import { useRoute } from 'vue-router';
 
//  const route = useRoute();
 
//  const header = computed(() => route.meta.header === true);
//  const sidebar = computed(() => route.meta.sidebar === true);
 </script>
 
 <style scoped>
   body {
      margin: 0;
      padding: 0;
   }
 </style>
 