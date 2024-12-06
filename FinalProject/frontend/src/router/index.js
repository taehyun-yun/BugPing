import { createRouter, createWebHistory } from 'vue-router'
import CalculatorPage from '../views/CalculatorPage.vue';
import NoticePage from '../views/notice/NoticePage.vue';
import Schedule from '../views/Schedule.vue';
import NoticeCreate from '../views/notice/NoticeCreate.vue';
import WorkerCommuting from '../views/commute/WorkerCommuting.vue';
import NoticeDetail from "../views/notice/NoticeDetail.vue";
import NoticeMain from "../views/notice/NoticeMain.vue";

import test from '../views/test.vue';
import contract from '../views/employment/AdministratorContract.vue';
import commute from '../views/commute/WorkerCommuting.vue';
import LoginView from '../views/auth/LoginView.vue';

const router = createRouter({
history: createWebHistory(import.meta.env.BASE_URL),
routes: [
    {
      path: '/calculator',
      name: 'CalculatorPage',
      component: CalculatorPage,
    },
    {
        path: "/noticemain",
        name: "notice",
        component: NoticeMain,
    },
    {
        path: "/noticedetail",
        name: "noticedetail",
        component: NoticeDetail,
    },
    {
        path: '/noticecreate',
        name: 'noticeCretate',
        component: NoticeCreate
    },
    {
        path: '/commute',
        name: 'WorkerCommuting',
        component: WorkerCommuting
    },
    {
        path: '/schedule',
        name: 'schedule',
        component: Schedule
    },
    {
        path: '/contract',
        name: 'contract',
        component: contract
    },
    { path: '/login', name: 'login', component: LoginView },
    // ↓↓예시↓↓ 인증이 필요한 페이지는 뒤에 meta: {requiresAuth: true } 넣어주면 됩니다. ↓↓예시↓↓
    //{ path: '/protected', name: 'Protected', component: ProtectedPage, meta: {requiresAuth: true } }
  ],
});

function getCookie(name) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(';').shift();
  return null;
}

// 전역 가드 설정
router.beforeEach((to, from, next) => {
  // 로그인 여부를 확인
  const token = getCookie('jwtToken');
  const isAuthenticated = !!token;

  if (to.matched.some(record => record.meta.requiresAuth) && !isAuthenticated) {
    // 인증이 필요한 페이지인데 로그인이 되어 있지 않은 경우 로그인 페이지로 이동
    next('/login');
  } else {
    // 그렇지 않다면 페이지 이동 허용
    next();
  }
});

export default router
