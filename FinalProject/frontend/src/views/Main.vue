<template>
    <MainHeader @show-sidebar="showSidebar($event)"></MainHeader>
    <MainSidebar v-show="sidebar" class="main-sidebar"></MainSidebar>
    <div class="total">
        <div 
            class="routerview-container" 
            :class="{ 'exist-sidebar': sidebar }">
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
const sidebar = ref(route?.meta?.sidebar === true);

const showSidebar = (menucontroll) => {
    sidebar.value = menucontroll;
};
</script>

<style scoped>
.total {
    width: 100vw;
    display: flex;
    justify-content: center;
    margin-top: 50px; /* 헤더 높이 */
    position: relative; /* 사이드바 위치를 정렬하기 위해 */
}

.main-sidebar {
    width: 300px;
    height: calc(100vh - 50px); /* 헤더를 제외한 높이 */
    position: absolute;
    top: 50px; /* 헤더 아래로 */
    left: 0;
    background-color: #f5f5f5; /* 사이드바 배경색 */
    box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
}

.routerview-container {
    display: inline-block;
    margin: 0 auto;
    white-space: nowrap;
    padding-left: 10px;
    padding-right: 10px;
    transition: margin-left 0.3s ease; /* 부드러운 애니메이션 */
}

.exist-sidebar {
    margin-left: 300px; /* 사이드바 크기만큼 왼쪽 여백 */
    width: calc(100% - 300px); /* 사이드바를 제외한 너비 */
    max-width: 1400px;
}

.routerview-container:not(.exist-sidebar) {
    width: 100%;
}
</style>