import { createRouter, createWebHistory } from 'vue-router'
import CalculatorPage from '../views/CalculatorPage.vue';
import Contract from '../views/employment/AdministratorContract.vue';
import Commute from '../views/commute/WorkerCommuting.vue';
import LoginView from '../views/auth/LoginView.vue';
import NoticeMain from '@/views/notice/NoticeMain.vue';
import NoticeDetail from '@/views/notice/NoticeDetail.vue';
import NoticeCreate from '@/views/notice/NoticeCreate.vue';
import Schedule from '@/views/Schedule.vue';
import axios from 'axios';
import { axiosAddress } from '@/stores/axiosAddress';
import SignUpView from '@/views/auth/SignUpView.vue';
import SU1 from '@/components/auth/SU1.vue';
import SU2 from '@/components/auth/SU2.vue';
import SU3 from '@/components/auth/SU3.vue';
import Employer from '@/views/Employer.vue';
import Employee from '@/views/Employee.vue';


const router = createRouter({
history: createWebHistory(import.meta.env.BASE_URL),
routes: [
    {
      path: '/calculator',
      name: 'CalculatorPage',
      component: CalculatorPage,
      meta : { header : true, sidebar : true, requiresAuth: false, roles : ["employer"], title: '지급내역',},
    },
    {
        path: "/noticemain",
        name: "notice",
        component: NoticeMain,
        meta : { header : true, sidebar : true, requiresAuth: false, title: '알림',},
    },
    {
        path: "/noticedetail",
        name: "noticedetail",
        component: NoticeDetail,
        meta : { header : true, sidebar : true, requiresAuth: false, title: '알림',},
    },
    {
        path: '/noticecreate',
        name: 'noticeCretate',
        component: NoticeCreate,
        meta : { header : true, sidebar : true, requiresAuth: false, title: '알림',},
    },
    {
        path: '/commute',
        name: 'commute',
        component: Commute,
        meta : { header : true, sidebar : true, requiresAuth: false, title: '근태',},
    },
    {
        path: '/schedule',
        name: 'schedule',
        component: Schedule,
        meta : { header : true, sidebar : true, requiresAuth: false, title: '스케쥴',},
    },
    {
        path: '/contract',
        name: 'contract',
        component: Contract,
        meta : { header : true, sidebar : true, requiresAuth: false, title: '계약',},
    },
    { path: '/login', name: 'login', component: LoginView, meta : {title: '로그인'} },
    { path: '/signup', name : 'signup', component : SignUpView, meta : {title: '회원가입'},
      children : [
        {path: 'su1', name : 'su1', component : SU1,},
        {path: 'su2', name : 'su2', component : SU2,},
        {path: 'su3', name : 'su3', component : SU3,},
      ],
      },
      { path : '/employer', name : 'employer', component : Employer, meta : {header : true, sidebar : true, requiresAuth: false, roles : ["employer"],
        children : [
          //이 안에 넣으시면 됩니다.
        ]
      }},
      { path : '/employee', name : 'employee', component : Employee, meta : {header : true, sidebar : true, requiresAuth: false, roles : ["employee"],
        children : [
          //이 안에 넣으시면 됩니다.
        ]
      }}
    // ↓↓예시↓↓ 인증이 필요한 페이지는 뒤에 meta: {requiresAuth: true } 넣어주면 됩니다. ↓↓예시↓↓
    //{ path: '/protected', name: 'Protected', component: ProtectedPage, meta: { header : true, sidebar : true, requiresAuth: true, roles: ['employer'], } }
    //{ path: '/unprotected', name: 'UnProtected', component: UnProtectedPage, }
  ],
});

const getRole = async() =>{
  let roles;
  await axios.get(axiosAddress+"/findrole",{withCredentials: true})
  .then((res)=>{
  roles = res.data.roles.map((role) => role.replace('ROLE_', ''));
  });
  if(roles[0] == 'ANONYMOUS'){
    return [false,roles];
  }
  return [true,roles];
}

// 전역 가드 설정
router.beforeEach(async(to, from, next) => {
  //부모 meta 상속하기
  if(to.matched.length>0){
    const mergeMeta = to.matched.reduce((meta, record) =>{
      return { ...meta , ...(record.meta ?? {} )};
    },{});
    to.meta = mergeMeta;
  }

  if (!to?.meta?.requiresAuth) {
    return next(); // 바로 통과
  }
  //로그인이 필요할 때
  let auth = await getRole();
  alert(auth[0]);
  if(to?.meta?.requiresAuth && auth[0]){
    return next();
  }
  //역할까지 분리해야한다면
  const userRoles = auth[1];
  alert(userRoles);
  const routeRoles = to?.meta?.roles || [];
  const hasRequireRole = routeRoles.length === 0 || routeRoles.some((role)=>userRoles.includes(role))
  if(hasRequireRole){
    next();
  } else {
    next('/login');
  }
});
router.afterEach((to) => {
  const defaultTitle = '운영의 달인';
  document.title = to.meta.title || defaultTitle;
});

export default router