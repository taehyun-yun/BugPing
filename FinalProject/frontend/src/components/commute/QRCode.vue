<template>
    <img :src="`${axiosAddress}/makeQR?companyId=${companyId}`" alt="QR Code">
</template>
<script setup>
import { axiosAddress } from '@/stores/axiosAddress';
import { useUserStore } from '@/stores/userStore';
import { computed } from 'vue';
const userStore = useUserStore();
const companyId = computed(()=>userStore.company.companyId);
</script>
<style>
</style>