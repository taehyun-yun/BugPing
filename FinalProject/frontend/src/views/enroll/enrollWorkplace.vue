<template>
    <div>
        <table>
            <thead>
                <tr>
                    <td>
                        사업체명
                    </td>
                    <td>
                        주소
                    </td>
                    <td>
                        사업자번호
                    </td>
                    <td>
                        근무자 등록번호
                    </td>
                </tr>
            </thead>
            <tbody>
                <tr v-for="company in companies">
                    <td>
                        {{ company.cname }}
                    </td>
                    <td>
                        {{ company.address + " " + company.detailAddress }}
                    </td>
                    <td>
                        {{ company.cnum }}
                    </td>
                    <td>
                        {{ company.companyCode }}
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    <Teleport to="body">
        <div class="modal-overlay" @click.self="showModalChange" v-show="showModal">
            <div class="modal">
                <form ref="regform">
                    <SU4 @update="updateParent('SU4',$event)"></SU4>
                    <SU5 @update="updateParent('SU5',$event)"></SU5>
                    <button style="width: 100%;" @click="submitData">변경</button>
                </form>
            </div>
        </div>
    </Teleport>
</template>
<script setup>
import SU4 from '@/components/auth/SU4.vue';
import SU5 from '@/components/auth/SU5.vue';
import { axiosAddress } from '@/stores/axiosAddress';
import axios from 'axios';
import { onMounted, reactive, ref } from 'vue';
const companies = reactive([]);
// const getData = async()=>{
//     try{
//         const res = await axios.get(`${axiosAddress}/employer/findOwnCompany`, {withCredentials : true});
//         companies.splice( 0, companies.length, ...res.data);
//     } catch(err) {
//         alert(err);
//     }
// }
// onMounted(()=>{
//     getData();
// })

// 자식 데이터 가져온다면 담을 곳
const childData = reactive({
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
        const mergedData = { ...childData.SU4, ...childData.SU5 };
        // JSON으로 되어있다.. formdata로 바꾸자. requestBody는 한개만 받아올 수 있어서 user와 company 두개로 분리하기 힘들다...
        const formdata = new FormData();
        const entries = Object.entries(mergedData);
        entries.forEach(([key, value])=> formdata.append(key,value));
        //확인용
        alert(entries.map(([key, value]) => `${key}: ${value}`).join('\n'));
        // 보내기
        await axios.post(axiosAddress+"/companyRegister",formdata,{withCredentials: true})
        .then((res)=>{
            alert(res.data);
            push({name : ''})
        })
        .catch((err)=>{
            alert(err.response);
        })
    }else{
        alert("필수 항목을 전부 입력해주세요.");
    }
};
const showModal = ref(true);
const showModalChange = () =>{
    showModal.value = !showModal.value;
}
const closeModal = () => {
    showModal.value = false;
}

</script>
<style scoped>
    .modal{
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        padding: 15px;
        border: 1px, solid, #4FD1C5;
        background-color: #ffffff;
        border-radius: 20px;
        width: 600px;
        height: 500px;

        /* Flexbox로 세로와 가로 중앙 정렬 */
        display: flex;
        flex-direction: column; /* 세로 방향으로 정렬 */
        justify-content: center; /* 세로 축 중앙 정렬 */
        align-items: center; /* 가로 축 중앙 정렬 */
    }
    .modal-overlay {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 999;
    }
    form{
        width: 90%;
        height: 90%;
        padding: 30px;
        position: relative;
    }
</style>