<template>
    <form ref="regform">
        <div v-show="isshow">
            <SU3 @update="updateParent('SU3',$event)"></SU3>
        </div>
    <div v-if="usertype=='employer'" class="employer-signupcontainer">
        <button v-show="isshow" type="button" class="signup-button" @click="showEvent">다음으로</button>
        <div v-show="!isshow">
            <SU4 @update="updateParent('SU4',$event)"></SU4>
            <br>
            <SU5 @update="updateParent('SU5',$event)"></SU5>
        </div>
        <br>
        <button v-show="!isshow" type="button" class="signup-button" @click="showEvent">이전으로</button>
    </div>
    </form>
    <button v-show="!isshow || usertype == 'employee'" type="button" class="signup-button" @click="submitData">회원 가입</button>
</template>
<script setup>
import { useAuthStore } from '@/stores/authStore';
import SU3 from './SU3.vue';
import SU4 from './SU4.vue';
import SU5 from './SU5.vue';
import { onMounted, reactive, ref } from 'vue';
import router from '@/router';
import axios from 'axios';
import { axiosAddress } from '@/stores/axiosAddress';

const usertype = useAuthStore().userType;
onMounted (()=>{
    //새로 고침했을 경우 usertype이 날라가버리므로 새로 시작하게함
    if(usertype ==''){
        router.push({name : 'su1'})
    }
})
const regform = ref(null);
const isshow = ref(true);
const showEvent = () => {
    isshow.value = !isshow.value;
}
// 자식 데이터 가져온다면 담을 곳
const childData = reactive({
    SU3 : {},
    SU4 : {},
    SU5 : {},
});
// 자식 데이터 가져와서 담기
const updateParent = (component , data) =>{
    childData[component] = data;
}
// 자식 데이터 합치고 보내기
const submitData = async() => {
    let isvalid = regform.value.reportValidity();
    if(isvalid){
        // 자식 데이터 합치기
        const mergedData = { ...childData.SU3, ...childData.SU4, ...childData.SU5, role : usertype };
        // JSON으로 되어있다.. formdata로 바꾸자. requestBody는 한개만 받아올 수 있어서 user와 company 두개로 분리하기 힘들다...
        const formdata = new FormData();
        const entries = Object.entries(mergedData);
        entries.forEach(([key, value])=> formdata.append(key,value));
        //확인용
        alert(entries.map(([key, value]) => `${key}: ${value}`).join('\n'));
        // 보내기
        await axios.post(axiosAddress+"/userRegister",formdata,{withCredentials: true})
        .then((res)=>{
            router.push({name : 'home'})
        })
        .catch((err)=>{
            alert(err.response.data);
        })
    }else{
        alert("필수 항목을 전부 입력해주세요.");
    }
};
</script>
<style scoped>
    form{
        width: 100%;
    }
    .employer-signupcontainer{
        width: 100%;
    }
    .signup-button {
        background-color: #4FD1C5;
        color: #ffffff;
        border: none;
        padding: 15px;
        font-size: 1rem;
        border-radius: 8px;
        cursor: pointer;
        transition: background-color 0.3s ease;
        width: 100%;
        margin-top: 20px;
        margin-bottom: 20px;
    }

    .signup-button:hover {
        background-color: lightseagreen;
    }
</style>