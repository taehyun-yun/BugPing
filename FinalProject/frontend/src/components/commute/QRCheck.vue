<template>
</template>
<script setup>
import router from '@/router';
import { axiosAddress } from '@/stores/axiosAddress';
import axios from 'axios';
import { computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const companyId = computed(()=>route.query.companyId);

const commuteCheck = async() =>{
    try{
        const res = await axios.get(`${axiosAddress}/commuteCheck?companyId=${companyId.value}`)
        alert(res.data)
    } catch (err) {
        alert(err.response.data);
        router.push("/login");
    }
    
}
onMounted(()=>{
    commuteCheck();
})
</script>
<style>
</style>