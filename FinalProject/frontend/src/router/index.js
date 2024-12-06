import { createRouter, createWebHistory } from 'vue-router'
import CalculatorPage from '../views/CalculatorPage.vue';
//import Schedule from '../views/Schedule.vue';
import NoticeCreate from '../views/notice/NoticeCreate.vue';
import WorkerCommuting from '../views/commute/WorkerCommuting.vue';
import NoticeDetail from "../views/notice/NoticeDetail.vue";
import NoticeMain from "../views/notice/NoticeMain.vue";
import contract from '../views/employment/AdministratorContract.vue';
import LoginView from '../views/auth/LoginView.vue';
import jwt from 'jsonwebtoken';

const router = createRouter({
history: createWebHistory(import.meta.env.BASE_URL),
routes: [
    {
        path: '/cal',
        name: 'cal',
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
        path: '/notice/create',
        name: 'noticeCretate',
        component: NoticeCreate
    },
    {
        path: '/commute',
        name: 'WorkerCommuting',
        component: WorkerCommuting
    },
    // { 
    //     path: '/schedule',
    //     name: 'schedule',
    //     component: Schedule
    // },
    {
        path: '/contract',
        name: 'contract',
        component: contract
    },
    { path: '/login', name: 'login', component: LoginView, meta : { sidebar : false, requiresAuth: false, title: '로그인',} },
    // ↓↓예시↓↓ 인증이 필요한 페이지는 뒤에 meta: {requiresAuth: true } 넣어주면 됩니다. ↓↓예시↓↓
    //{ path: '/protected', name: 'Protected', component: ProtectedPage, meta: {requiresAuth: true, roles: ['employer'], } }
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
//1 로그인이 필요하지 않다 또는 로그인이 되어있다
//2 토큰의 시간이 현재 시간보다 많다
//3 역할이 필요하지 않다 또는 역할을 만족한다
//전부다 통과해야 로그인되게 짠다.
  const token = getCookie('jwtToken');
  if (!to.meta?.requiresAuth) {
    return next(); // 바로 통과
  }
  if(token){
    const decodedtoken = jwt.decode(token);
    if(decodedtoken.exp*1000 > Date.now()){
      const userRoles = decodedtoken.role.split(',');
      const routeRoles = to?.meta.roles || [];
      const compareRoleResult = routeRoles == [] ||routeRoles.some((role)=>userRoles.includes(role))
      if(compareRoleResult){
        next();
      }
    }
  }
  next('/login');
});
router.afterEach((to) => {
  const defaultTitle = '운영의 달인';
  document.title = to.meta.title || defaultTitle;
});

export default router
