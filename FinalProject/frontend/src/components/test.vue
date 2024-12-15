<template>
    <button @click="click">날 클릭해라</button>
</template>
<script setup>
import { axiosAddress } from '@/stores/axiosAddress';
import axios from 'axios';

const click = () => {
    axios
    .get(`${axiosAddress}/api/authtest`,{withCredentials : true})
    .then((res)=>{
        alert(res.data.getName);
        res.data.getAuthorities.forEach(auth=>{alert(auth.authority)});
        alert(res.data.getCredentials);
        alert(res.data.getDetails);
        alert(res.data.getPrincipal);
        alert(res.data.getClass);
    })
}
</script>
<style scoped>
</style>