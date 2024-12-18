<template>
        <MainHeader @show-sidebar="showSidebar($event)"></MainHeader>
        <MainSidebar v-show="sidebar"></MainSidebar>
    <div class="total">
        <div class="routerview-container" :class="{ 'exist-sidebar' : sidebar }">
            <RouterView :key="$route.fullPath"></RouterView>
        </div>
    </div>
</template>
<script setup>
import MainHeader from '@/components/MainHeader.vue';
import MainSidebar from '@/components/MainSidebar.vue';
import { ref } from 'vue';
import { useRoute } from 'vue-router';
const route = useRoute();
const mainheader = ref(route?.meta?.header === true);
const sidebar = ref(route?.meta?.sidebar === true);
// alert("사이드바를 표시하나요? "+sidebar.value);
const showSidebar = (menucontroll) =>{
    sidebar.value = menucontroll;
}
</script>
<style scoped>
    .total {
        width: 100vw;
        display: flex;
        justify-content: center;
        margin-top: 70px;
    }
    .routerview-container{
        width: 100%;
        display: inline-block;
        margin : 0 auto;
        white-space: nowrap;
    }
    .exist-sidebar{
        min-width: 1000px;
        max-width: 1400px;
        padding-left : 300px;
    }
</style>