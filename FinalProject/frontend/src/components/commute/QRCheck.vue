<template>
    <div class="schedule-container">
    <h1 class="title">오늘의 스케줄</h1>
    <div class="button-group">
        <button
        @click="showToday"
        :class="{ active: showWhat }"
        >
        오늘 일정
        </button>
        <button
        @click="showWhole"
        :class="{ active: !showWhat }"
        >
        전체 일정
        </button>
    </div>
    <div class="schedule-list">
        <button
        v-for="schedule in schedules"
        :key="schedule.scheduleId"
        type="button"
        class="schedule-button"
        @click="commuteCheck(schedule.scheduleId,'scheduleId')"
        >
        <div class="schedule-info">
            <span class="company-name">회사이름: {{ schedule.contract.work.company.cname }}</span>
            <span class="period">
            기간: {{ dateForm(schedule.contract.contractStart.split("T")[0]) }} ~
            {{ dateForm(schedule.contract.contractEnd.split("T")[0]) }}
            </span>
            <span class="weekday">{{ weekday[schedule.day] }}요일</span>
            <span class="time">출근시간: {{ schedule.officialStart }}</span>
            <span class="time">퇴근시간: {{ schedule.officialEnd }}</span>
        </div>
        </button>
    </div>
    <div class="schedule-list">
        <button
        v-for="workChange in workChanges"
        :key="workChange.workChangeId"
        type="button"
        class="schedule-button"
        @click="commuteCheck(workChange.workChangeId,'workChangeId')"
        >
        <div class="schedule-info">
            <span>{{ workChange.inOut=="IN" ? "추가근무" : "휴가"}}</span>
            <span class="company-name">회사이름: {{ workChange.schedule.contract.work.company.cname }}</span>
            <span class="weekday">{{ dateForm(workChange.changeStartTime.split("T")[0]) }}</span>
            <span class="time">출근시간: {{ workChange.changeStartTime.split("T")[1] }}</span>
            <span class="time">퇴근시간: {{ workChange.changeEndTime.split("T")[1] }}</span>
        </div>
        </button>
    </div>
        <p class="message">{{ message }}</p>
    </div>
</template>

<script setup>
import router from '@/router';
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';
import { axiosAddress } from '@/stores/axiosAddress';

const route = useRoute();
const companyId = computed(() => route.query.companyId);
//스케쥴
const schedules = ref([]);
const schedulesToday = ref([]);
const schedulesWhole = ref([]);
//변경
const workChanges = ref([]);
const workChangeToday = ref([]);
const workChangeWhole = ref([]);
//메세지
const message = computed(()=> schedules.value.length === 0 && workChanges.value.length===0 ? "스케쥴이 없습니다." : "체크할 일정을 선택해주세요.");
//요일
const weekday = ["","월","화","수","목","금","토","일"]; //DB에 월 = 1, 일 = 7
const now = ref(new Date());
const todayDate = computed(() => now.value.toISOString().split("T")[0]);
const yesterdayDate = computed(() => {
  const yesterday = new Date(now.value); // 현재 날짜 복사
  yesterday.setDate(yesterday.getDate() - 1); // 하루 빼기
  return yesterday.toISOString().split("T")[0]; // ISO 형식에서 날짜 부분만 추출
});
//시계
setInterval(()=>{now.value = new Date()},1000);
const today = computed(()=>now.value.getDay()===0 ? 7 : now.value.getDay());
const yesterday = computed(()=>now.value.getDay() === 1 ? 7 : now.value.getDay() - 1);

const commuteList = async () => {
    try {
        const res = await axios.get(`${axiosAddress}/commuteList?companyId=${companyId.value}`);
        schedulesWhole.value = res.data.schedules || [];
        workChangeWhole.value = res.data.workChanges || [];
        //6시 이전에는 어제꺼도 찍을 수 있게
        if(now.value.getHours()<6){
            //workChange 시간으로 가져옴
            const workChangeTodayAll = workChangeWhole.value.filter(workChange => workChange.changeDate == todayDate.value || workChange.changeDate == yesterdayDate.value);
            //IN은 처리
            workChangeToday.value = workChangeTodayAll.filter(workChange => workChange.inOut == "IN");
            //OUT은 filter처리해서 companyId를 저장. 시간은 위에서 분리했으므로 companyId만 해도 됨
            const workChangeTodayOutScheduleId = workChangeTodayAll.filter(workChange => workChange.inOut == "OUT").map(workChange => workChange.schedule.contract.work.company.companyId);
            //위에 배열 안에 포함되어있으면 해당 날짜구간에 OUT인 company이다.
            schedulesToday.value =  schedulesWhole.value.filter(schedule => !workChangeTodayOutScheduleId.includes(schedule.contract.work.company.companyId) && (schedule.day == today.value || schedule.day == yesterday.value));
        } else {
        //위에꺼에서 yesterdayDate만 빼고 똑같음
            const workChangeTodayAll = workChangeWhole.value.filter(workChange => workChange.changeDate == todayDate.value);
            workChangeToday.value = workChangeTodayAll.filter(workChange => workChange.inOut == "IN");
            const workChangeTodayOutScheduleId = workChangeTodayAll.filter(workChange => workChange.inOut == "OUT").map(workChange => workChange.schedule.contract.work.company.companyId);
            schedulesToday.value =  schedulesWhole.value.filter(schedule => !workChangeTodayOutScheduleId.includes(schedule.contract.work.company.companyId) && schedule.day == today.value);
        }
    } catch (err) {        
        if (err.response.status === 401) {
            alert(err.response.data.msg);
            return router.push('/login');
        }
    }
};
const dateForm = (date) =>{
    return date.replace(/-/,"년 ").replace(/-/,"월 ") + "일"
}

onMounted(async() => {
    await commuteList();
    showToday();
});
const showToday = () =>{
    schedules.value = schedulesToday.value;
    workChanges.value = workChangeToday.value;
    showWhat.value = true;
}
const showWhole = () =>{
    schedules.value = schedulesWhole.value;
    workChanges.value = workChangeWhole.value;
    showWhat.value = false;
}
const commuteCheck = async(id,type) =>{
    const payload = type=="scheduleId" ? { scheduleId : id, type : type } : { workChangeId : id, type : type };
    try{
        const res = await axios.post(`${axiosAddress}/commuteCheck`,payload,{withCredentials : true});
        // 출근하자.
        if(res.data.step==1){
            // 출근처리되었습니다
            return alert(res.data.msg);
        }
        // 퇴근하자. 이미 출근찍었으면 출근 못함.
        if( res.data.step==2){
            if(confirm(res.data.msg)){
                const res2 = await axios.post(`${axiosAddress}/leaveCheck`, res.data.attendance , {withCredentials : true});
                return alert(`퇴근처리되었습니다.\n${now.value.toISOString("en-CA").split("T")[0]}\n${now.value.toLocaleTimeString()}`);
            }
        }
        // 이미 퇴근함.
        if(res.data.step==3){
            return alert(res.data.msg);
        }
    } catch (err) {
        alert(err);
    }
}
const showWhat = ref(true);
</script>
<style scoped>
.schedule-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;
}

.title {
    font-size: 24px;
    font-weight: bold;
    color: #333;
    margin-bottom: 20px;
}

.button-group {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
}

.button-group button {
    background-color: #e2e8f0;
    color: #333;
    border: none;
    border-radius: 8px;
    padding: 10px 20px;
    font-size: 16px;
    cursor: pointer;
    transition: background-color 0.3s, color 0.3s;
}

.button-group button.active {
    background-color: #4FD1C5;
    color: white;
}

.schedule-list {
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 10px;
    margin-top: 10px;
    margin-bottom: 10px;
}

.schedule-button {
    background-color: #4FD1C5;
    color: white;
    border: none;
    border-radius: 8px;
    padding: 15px;
    font-size: 16px;
    text-align: left;
    cursor: pointer;
    transition: background-color 0.3s;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.schedule-button:hover {
    background-color: #38b2ac;
}

.schedule-info {
    display: flex;
    flex-direction: column;
    gap: 5px;
}

.company-name,
.period,
.weekday,
.time {
    font-size: 14px;
}

.message {
    margin-top: 20px;
    font-size: 16px;
    color: #555;
}
</style>