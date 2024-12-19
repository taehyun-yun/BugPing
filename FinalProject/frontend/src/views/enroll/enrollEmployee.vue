<template>
<div class="wrap-workplace">
    <div class="search-bar-container">
        <div class="search-bar">
            <input class="input-field" v-model="inputCompanyCode">
            <img src="/src/assets/enroll/magnifying-glass-solid.svg" class="search-button" @click="getInfo">
        </div>
    </div>
    <div class="companyInfo">
        <table class="work-table">
            <thead>
                <tr class="work-thead">
                    <th>근무지</th>
                    <th>입사일</th>
                    <th>퇴사일</th>
                    <th>전화번호</th>
                    <th>주소</th>
                </tr>
            </thead>
            <tbody>
                <template v-for="(work, index) in myCompanies" :key="work.id">
                    <tr @click="showThisCompanyContract(index)">
                        <td class="work-td">{{ work.company.cname }}</td>
                        <td>{{ work.hireDate }}</td>
                        <td>{{ work.resignDate || '근무중' }}</td>
                        <td>{{ work.company.ctel }}</td>
                        <td>{{ work.company.address + " " + work.company.detailAddress }}</td>
                    </tr>
                    <tr v-if="work.contracts.length>0" v-show="showContract[index]">
                        <td colspan="5">
                            <table class="contract-table">
                                <thead>
                                    <tr>
                                        <th>계약 시작</th>
                                        <th>계약 종료</th>
                                        <th>시급</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="contract in work.contracts" :key="contract.id">
                                        <td>{{ contract.contractStart.split("T")[0] }}</td>
                                        <td>{{ contract.contractEnd.split("T")[0] }}</td>
                                        <td> ￦ {{ contract.hourlyWage }}</td>
                                    </tr>
                                </tbody>
                            </table>
                        </td>
                    </tr>
                </template>
            </tbody>
        </table>
    </div>
    <Teleport to="body">
        <div class="modal-overlay" v-show="showChangeModal" @click.self="closeModal">
            <div class="modal">
                <div class="title">
                    <img class="icon" src="/src/assets/enroll/briefcase-solid.svg"> {{ companyInfo.cname }}
                </div>
                <div class="content">
                    <div><img class="icon" src="/src/assets/enroll/phone-solid.svg">{{ companyInfo.ctel }}</div>
                    <div><img class="icon" src="/src/assets/enroll/person-walking-solid.svg">{{ companyInfo.address }}</div>
                    <div><button @click="enroll">등록하기</button></div>
                </div>
            </div>
        </div>
    </Teleport>
</div>
</template>
<script setup>
import { axiosAddress } from '@/stores/axiosAddress';
import { useUserStore } from '@/stores/userStore';
import axios from 'axios';
import { computed, onMounted, reactive, ref } from 'vue';
//검색
const inputCompanyCode =  ref('');
const inputCompanyCodeCut = computed(()=>{
    return inputCompanyCode.value.trim();
})
const companyInfo = reactive({
    companyId : '',
    cname : '',
    ctel : '',
    address : '',
    
})
const getInfo = async() => {
    await axios.get(`${axiosAddress}/employee/getCompanyInfo?code=${inputCompanyCodeCut.value}`,{ withCredentials : true})
    .then((res)=>{
        companyInfo.companyId = res.data.companyId;
        companyInfo.cname = res.data.cname;
        companyInfo.ctel = res.data.ctel;
        companyInfo.address = res.data.address + " " + res.data.detailAddress;
    })
    showChangeModal.value = true;
}
//등록
const enroll = async() =>{
    try{
        const res = await axios.post(`${axiosAddress}/employee/enroll`,{ companyId : companyInfo.companyId, },{withCredentials : true})
        alert(res.data);
        const companyRes = await axios.get(`${axiosAddress}/api/getHeaderCompanyList`,{withCredentials : true})
        const userStore = useUserStore()
        userStore.setCompanies(companyRes.data);
        userStore.setCompany({ companyId : companyInfo.companyId, cname : companyInfo.cname});
    } catch (err) {
        alert(err.response.data);
    }
}
//모달창 여닫기
const showChangeModal = ref(false);
const closeModal = () => {
    showChangeModal.value = false;
}
//과거근무계약기록
const myCompanies = reactive([]);
const showContract = reactive([]);
const getMyAllContract = ()=> {
    axios
    .get(`${axiosAddress}/employee/getMyAllContract`,{withCredentials : true})
    .then((res)=>{
        myCompanies.splice(0,myCompanies.length, ...res.data.work);
        const myContracts = [...res.data.contract];
        myCompanies.forEach(work =>{
            work.contracts = myContracts.filter(contract =>
                contract.work.company.companyId === work.company.companyId
            )
        })
        showContract = [];
        for(i in myCompanies){
            showContract.push(false);
        }
    });
};
const showThisCompanyContract = (number) =>{
    showContract[number] = !showContract[number];
}
onMounted(()=>{
    getMyAllContract();
})
</script>
<style scoped>
    .wrap-workplace{
        width: 100%;
        display: flex;
        flex-direction: column; /* 세로로 배치 */
        justify-content: center; /*가로 중앙에 배치*/
        align-items: center; /*세로 중앙에 배치*/
    }
    .search-bar-container{
        display: flex;
        justify-content: center;
        align-items: center;
        width: 40%;
        background-color: #f9f9f9;
        padding: 10px;
    }
    .search-bar{
        display: flex;
        width: calc(100% - 10px);
        justify-content: center;
        align-items: center;
        border: 1px solid #4FD1C5;
    }
    .input-field{
        width: 100%;
        height: 40px;
        font-size: 22px;
        text-align: center;
        border: none;
    }
    .search-button{
        width: 40px;
        height: 40px;
        background-color: white;
    }
    .modal{
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        padding: 15px;
        border: 1px, solid, #4FD1C5;
        background-color: #ffffff;
        border-radius: 20px;
        width: 400px;
        height: 200px;

        display: flex;
        flex-direction: column; /* 세로 방향으로 정렬 */
        justify-content: center; /* 세로 축 중앙 정렬 */
        align-items: center; /* 가로 축 중앙 정렬 */
        animation: slideUp 0.3s ease;
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
        animation: fadeIn 0.3s ease;
    }
    .modal .title {
    display: flex;
    align-items: center;
    font-size: 20px;
    font-weight: bold;
    color: #333;
    margin-bottom: 10px;
    }
    .icon{
        margin-right: 10px;
        width: 30px;
        height: 30px;
    }
    .content div{
        margin-top: 10px;
        margin-bottom: 10px;
        display: flex;
        align-items: center;
    }
    .content button{
        width: 100%;
        border : 0;
        background-color: #4FD1C5;
        font-size: 16px;
        color: #f9f9f9;
    }
    .content button:hover{
        background-color: lightseagreen;
    }
    /* 애니메이션 */
    @keyframes fadeIn {
        from {
            background-color: rgba(0, 0, 0, 0);
        }
        to {
            background-color: rgba(0, 0, 0, 0.6);
        }
    }

    @keyframes slideUp {
        from {
            transform: translate(-50%, 60%);
            opacity: 0;
        }
        to {
            transform: translate(-50%, -50%);
            opacity: 1;
        }
    }
    .work-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 20px;
    font-size: 16px;
    color: #333;
    }

    .work-table th,
    .work-table td {
        padding: 10px 15px;
        text-align: left;
        border-bottom: 1px solid #ddd;
    }

    .work-table thead th {
        background-color: #f1f1f1;
        font-weight: bold;
        font-size: 18px;
    }

    .work-table tbody tr:hover {
        background-color: #f9f9f9;
    }

    .contract-table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 10px;
        font-size: 14px;
        color: #555;
    }

    .contract-table th,
    .contract-table td {
        padding: 8px 12px;
        text-align: center;
        border-bottom: 1px solid #ddd;
    }

    .contract-table thead th {
        background-color: #e7f7f9;
        font-weight: bold;
        font-size: 16px;
    }

    .contract-table tbody tr:hover {
        background-color: #f5f5f5;
    }

    .work-thead {
        border-bottom: 2px solid #4FD1C5;
    }

    .work-td {
        font-weight: bold;
        color: #333;
    }

    /* 전체적인 테이블을 감싸는 영역 */
    .companyInfo {
        width: 90%;
        margin-top: 20px;
        box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
        border-radius: 10px;
        overflow: hidden; /* 테두리 밖 요소 제거 */
        background-color: #fff;
        padding: 20px;
    }
</style>