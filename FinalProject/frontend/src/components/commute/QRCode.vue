<template>
    <div v-show="companyId" class="qrcode-container">
        <img :src="`${axiosAddress}/makeQR?companyId=${companyId}`" alt="QR Code">
    </div>
</template>
<script setup>
import { axiosAddress } from '@/stores/axiosAddress';
import { useUserStore } from '@/stores/userStore';
import { computed } from 'vue';
const userStore = useUserStore();
const companyId = computed(()=>userStore.company.companyId);
</script>
<style>
    .qrcode-container{
        width: 100%;
        text-align: center;
    }
</style>