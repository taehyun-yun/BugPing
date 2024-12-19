<template>
    <img src="">
</template>
<script setup>
import { axiosAddress } from '@/stores/axiosAddress';
import { useUserStore } from '@/stores/userStore';
import axios from 'axios';

    const userStore = useUserStore();
    axios
    .get( `${axiosAddress}/api/makeQR/${userStore.company.companyId}`,{withCredentials : true})
</script>
<style>
</style>