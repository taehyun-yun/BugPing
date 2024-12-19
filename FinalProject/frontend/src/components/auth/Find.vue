<template>
    <div class="find-container">
        <!-- <div class="select-box"><button class="selected">아이디 찾기</button><button>비밀번호 찾기</button></div> -->
        <p style="color: #4fd1c5; padding-left: 20px; margin-bottom: 20px;">본인인증으로 등록한 이메일을 입력해주세요.</p>
        <div class="input-group" style="border-bottom: 1px solid silver;">
            <input type="email" class="input-field" maxlength="20" v-model="inputEmail" placeholder="aaaaa@bbbb.ccc">
            <button class="send-button" @click="sendCode">
                <img v-show="wait" style="margin : 0 ;"src="/src/assets/Loginimg/Dual Ring.svg">
                <div v-show="!wait">{{sendButtonMsg}}</div>
            </button>
        </div>
        <div class="input-group">
            <input type="text" class="input-field" maxlength="20" v-model="inputCode">
            <button class="send-button" @click="checkCode">확인</button>
        </div>
    </div>
    <div class="result-container" v-show="afterValid">
        <table>
            <thead>
                <tr>
                    <th>아이디</th>
                    <th>등록일자</th>
                    <th>비밀번호 변경</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="account in idList">
                    <td>{{ account[0] }}</td>
                    <td>{{ account[1] }}</td>
                    <td><button @click="showchangePwModal(account[0])">선택</button></td>
                </tr>
            </tbody>
        </table>
    </div>
    <Teleport to = "body">
        <div class="modal-overlay" v-show="showChangeModal" @click.self="closeModal">
            <div class="change-modal">
                <p>아이디 {{ selectedId }}에 재설정할 비밀번호를 입력해주세요.</p>
                <br>
                <div class="input-group">
                    <img src="/src/assets/Loginimg/lock.svg">
                    <input class="input-field" type="password" v-model="newpassword">
                    <button style="width: 100px;" @click="changePw">변경</button>
                </div>
            </div>
        </div>
    </Teleport>
</template>
<script setup>
import { axiosAddress } from '@/stores/axiosAddress';
import axios from 'axios';
import { watch, ref, reactive } from 'vue';
const inputEmail = ref('');
const inputEmailSaved = ref('');
const isCooltime = ref(false);
const currentCooltime = ref(0);

const sendCode = async() =>{
    if(currentCooltime.value == 0 ){
        //쿨타임 초기화
        isCooltime.value =  true;
        currentCooltime.value = 10;
        //현재 입력한 이메일을 저장
        inputEmailSaved.value = inputEmail.value;
        //로딩이미지 보이기 설정
        wait.value = true;
        //메세지 전송
        await axios.post(axiosAddress+"/sendCode",{ inputEmail : inputEmailSaved.value},{ withCredentials : true })
        .then((res)=>{
            alert(res.data);
        })
        //로딩이미지 안보이기 설정
        wait.value = false;
        //쿨타임
        const cooldown = setInterval(()=>{currentCooltime.value -= 1;},1000);
        setTimeout(()=>{
        isCooltime.value = false;
        currentCooltime.value = 0;
        clearInterval(cooldown);
        }, 1 * 10 * 1000);
    }
}
const inputCode = ref(''); 
const checkCode = async() =>{
    axios.post(axiosAddress+"/checkCode",{ inputEmail : inputEmailSaved.value ,inputCode : inputCode.value},{withCredentials: true})
    .then((res)=>{
        if(res.data){
            alert(inputEmailSaved.value + "인증되었습니다.")
            findId();
        } else {
            alert("인증번호가 일치하지 않습니다. 다시 발급 받아주세요.");
        }
    })
}
const sendButtonMsg = ref('인증번호 발송');
const wait = ref(false);
watch(currentCooltime,(newValue)=>{
    if(newValue != 0){
        sendButtonMsg.value = newValue;
    } else {
        sendButtonMsg.value = '인증번호 발송';
    }
},{ deep : true})
const idList = reactive([]);
const afterValid = ref(false);//이메일 인증되면 true.
const findId = async () =>{
    await axios.post(axiosAddress+"/findId",{inputEmail : inputEmailSaved.value},{withCredentials:true})
    .then((res)=>{
        idList.splice(0,idList.length);
        try{
            const userdata = res.data;
            userdata.forEach(user =>{
            idList.push([user.userId ,user.regDate]);
            afterValid.value = true;
            });
        } catch {
            alert("해당 이메일로 인증한 유저를 찾을 수 없습니다.")
        }
    })
}
const showChangeModal = ref(false);
const selectedId = ref('');
const newpassword = ref('');
const showchangePwModal = (accountId) =>{
    showChangeModal.value = true;
    selectedId.value = accountId;
}
const closeModal = () => {
    showChangeModal.value = false;
}
const changePw = () =>{
    axios.post(axiosAddress+'/setNewPassword',{ userId : selectedId.value, newPassword : newpassword.value},{withCredentials : true})
    .then((res)=>{
        alert("변경되었습니다.");
        newpassword.value = "";
        closeModal();
    })
}

</script>
<style scoped>
    .find-container{
        width: 100%;
    }
    .input-group {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 10px;
        background-color: #f9f9f9;
    }
    .input-group img{
        width: 20px;
        height: 20px;
        margin-left: 30px;
        margin-right: 10px;
    }
    .input-field {
        border: none;
        outline: none;
        background: none;
        width: calc(100% - 210px);
        text-align: center;
        font-size: 20px;
    }
    /* .select-box{
        width: 100%;
        height: 50px;
    }
    .select-box button{
        width: 50%;
        border: none;
        padding: 15px;
        border-radius: 0;
    } */
    .selected{
        background-color: #4FD1C5;
        color: #ffffff;
    }

    button {
        width: 200px;
        display: inline-block;
        background-color: #4FD1C5;
        color: #ffffff;
        border: none;
        padding: 15px;
        font-size: 1rem;
        border-radius: 8px;
        cursor: pointer;
        transition: background-color 0.3s ease;
    }
    button:hover {
        background-color: lightseagreen;
    }
    .result-container {
    width: 100%;
    margin-top: 20px;
    display: flex;
    justify-content: center;
    align-items: center;
    }
    th{
        width: 200px;
        color: #ffffff;
        background-color: #4FD1C5;
        padding: 15px;
        font-size: 1rem;
    }
    td{
        text-align: center;
        width: 200px;
        font-size: 1rem;
        height: 30px;
        vertical-align: middle;
    }
    td button{
        width: 60%;
        padding: 0;
    }
    .change-modal{
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
</style>