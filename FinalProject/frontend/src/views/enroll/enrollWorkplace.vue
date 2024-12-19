<template>
    <div class="workplace-wrap">
        <div class="add-button">
            <button @click="showModalChange">추가하기</button>
        </div>
        <div class="myOwnCompany">
        <table>
            <thead>
                <tr>
                    <th>
                        사업체명
                    </th>
                    <th>
                        주소
                    </th>
                    <th>
                        사업자번호
                    </th>
                    <th>
                        근무자 등록번호
                    </th>
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
    </div>
    <Teleport to="body">
        <div class="modal-overlay" @click.self="showModalChange" v-show.self="showModal">
            <div class="modal">
                <form ref="regform">
                    <SU4 @update="updateParent('SU4',$event)"></SU4>
                    <SU5 @update="updateParent('SU5',$event)"></SU5>
                    <button type="button" style="width: 100%;" @click="submitData">추가하기</button>
                </form>
            </div>
        </div>
    </Teleport>
</template>
<script setup>
import SU4 from '@/components/auth/SU4.vue';
import SU5 from '@/components/auth/SU5.vue';
import { axiosAddress } from '@/stores/axiosAddress';
import { useUserStore } from '@/stores/userStore';
import axios from 'axios';
import { onMounted, reactive, ref } from 'vue';
const companies = reactive([]);
const getData = async()=>{
    try{
        const res = await axios.get(`${axiosAddress}/employer/findOwnCompany`, {withCredentials : true});
        companies.splice( 0, companies.length, ...res.data.companies);
        const userStore = useUserStore();
        const companyRes = await axios.get(`${axiosAddress}/api/getHeaderCompanyList`,{withCredentials : true})
        userStore.setCompany(companyRes.data[0]);
        userStore.setCompanies(companyRes.data);
    } catch(err) {
        alert(err);
    }
}
onMounted(()=>{
    getData();
})

//새로운 사업장 저장
// regform 선언
const regform = ref(null);
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
        await axios.post(axiosAddress+"/employer/companyRegister",formdata,{withCredentials: true})
        .then((res)=>{
            alert(res.data);
            getData();
        })
        .catch((err)=>{
            alert(err);
        })
    }else{
        alert("필수 항목을 전부 입력해주세요.");
    }
};
const showModal = ref(false);
const showModalChange = () =>{
    showModal.value = !showModal.value;
}
const closeModal = () => {
    showModal.value = false;
}

</script>
<style scoped>
    .workplace-wrap {
        display: flex;
        flex-direction: column;
        align-items: center; /* 자식 요소를 수평 중앙 정렬 */
    }

    .myOwnCompany {
        width: 100%;
        display: flex;
        justify-content: center; /* 테이블을 수평 중앙 정렬 */
        margin-top: 20px; /* 추가 여백 */
    }

    table {
        width: 800px;
        max-width: 100%; /* 반응형 크기 조정 */
        border-collapse: collapse;
        background-color: #f9f9f9;
        border-radius: 8px;
        overflow: hidden;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    }

    table th, table td {
        padding: 12px 15px;
        text-align: left;
        border-bottom: 1px solid #ddd;
    }
    table th{
        text-align: center;
    }

    thead tr {
        background-color: #4FD1C5;
        color: white;
        font-weight: bold;
    }

    tbody tr:nth-child(odd) {
        background-color: #f5f5f5;
    }

    tbody tr:hover {
        background-color: #e8f8f5;
        cursor: pointer;
    }
    /* 추가하기 버튼 (테이블 위) */
    .add-button{
        position: relative;
        width: 800px;
        height: 100px;
    }
    .add-button button {
        position: absolute;
        bottom: 0;
        right: 0;
        padding: 10px 20px;
        background-color: #4FD1C5;
        color: white;
        font-size: 16px;
        font-weight: bold;
        border: none;
        border-radius: 8px;
        cursor: pointer;
        transition: background-color 0.3s ease, transform 0.2s ease;
    }
    .add-button button:hover {
        background-color: #38b2ac;
        /* transform: scale(1.05); */
    }
    /* 모달 내부 */
    .modal {
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        padding: 20px;
        background-color: #ffffff;
        border-radius: 16px;
        width: 600px;
        max-width: 90%;
        box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: space-between;
        gap: 20px;
    }

    .modal h2 {
        font-size: 24px;
        color: #333;
        margin-bottom: 10px;
    }

    .modal-overlay {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.6);
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
    /* 모달 버튼 */
    form button[type="button"] {
        display: block;
        margin: 20px auto 0;
        padding: 12px 20px;
        background-color: #4FD1C5;
        color: white;
        font-size: 16px;
        font-weight: bold;
        border: none;
        border-radius: 12px;
        cursor: pointer;
        width: 80%;
        text-align: center;
        transition: background-color 0.3s ease, box-shadow 0.2s ease;
    }

    form button[type="button"]:hover {
        background-color: #38b2ac;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
</style>