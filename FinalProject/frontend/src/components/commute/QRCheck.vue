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
        //스케쥴 요일과 시간 선택, workChange 중 시간 안 지난거 버튼으로 뜨게하고 그거 누르면 attendance 테이블에 넣는 작업 해야함.
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