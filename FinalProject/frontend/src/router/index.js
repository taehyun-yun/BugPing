import { createRouter, createWebHistory } from "vue-router";
import CalculatorPage from "../views/CalculatorPage.vue";
import Contract from "../views/employment/AdministratorContract.vue";
import Commute from "../views/commute/WorkerCommuting.vue";
import LoginView from "../views/auth/LoginView.vue";
import NoticeMain from "@/views/notice/NoticeMain.vue";
import NoticeDetail from "@/views/notice/NoticeDetail.vue";
import NoticeCreate from "@/views/notice/NoticeCreate.vue";
import Schedule from "@/views/Schedule.vue";
import axios from "axios";
import { axiosAddress } from "@/stores/axiosAddress";
import NoticeEdit from "@/views/notice/NoticeEdit.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/calculator",
      name: "CalculatorPage",
      component: CalculatorPage,
      meta: {
        header: true,
        sidebar: true,
        requiresAuth: false,
        title: "지급내역",
      },
    },
    {
      path: "/noticemain",
      name: "notice",
      component: NoticeMain,
      meta: { header: true, sidebar: true, requiresAuth: false, title: "알림" },
    },
    {
      path: "/noticedetail/:id",
      name: "noticedetail",
      component: NoticeDetail,
      meta: {
        header: true,
        sidebar: true,
        requiresAuth: false,
        title: "공지상세",
      },
    },
    {
      path: "/noticecreate",
      name: "noticeCreate",
      component: NoticeCreate,
      meta: { header: true, sidebar: true, requiresAuth: false, title: "알림" },
    },
    {
      path: "/noticeedit/:id",
      name: "noticeEdit",
      component: NoticeEdit,
      meta: {
        header: true,
        sidebar: true,
        requiresAuth: false,
        title: "공지 수정",
      },
    },
    {
      path: "/commute",
      name: "commute",
      component: Commute,
      meta: { header: true, sidebar: true, requiresAuth: false, title: "근태" },
    },
    {
      path: "/schedule",
      name: "schedule",
      component: Schedule,
      meta: {
        header: true,
        sidebar: true,
        requiresAuth: false,
        title: "스케쥴",
      },
    },
    {
      path: "/contract",
      name: "contract",
      component: Contract,
      meta: { header: true, sidebar: true, requiresAuth: false, title: "계약" },
    },
    {
      path: "/login",
      name: "login",
      component: LoginView,
      meta: {
        header: false,
        sidebar: false,
        requiresAuth: false,
        title: "로그인",
      },
    },
    // ↓↓예시↓↓ 인증이 필요한 페이지는 뒤에 meta: {requiresAuth: true } 넣어주면 됩니다. ↓↓예시↓↓
    //{ path: '/protected', name: 'Protected', component: ProtectedPage, meta: {requiresAuth: true, roles: ['employer'], } }
  ],
});
// HttpOnly 쿠키는 읽어올 수 없다
// function getCookie(name) {
//   const value = `; ${document.cookie}`;
//   const parts = value.split(`; ${name}=`);
//   if (parts.length === 2) return parts.pop().split(';').shift();
//   return null;
// }
const getRole = () => {
  axios
    .get(axiosAddress + "/findrole", { withCredentials: true })
    .then((res) => {
      alert(res.data);
    });
};

// 전역 가드 설정
router.beforeEach((to, from, next) => {
  //1 로그인이 필요하지 않다 또는 로그인이 되어있다
  //2 토큰의 시간이 현재 시간보다 많다
  //3 역할이 필요하지 않다 또는 역할을 만족한다
  //전부다 통과해야 로그인되게 짠다.
  if (!to?.meta?.requiresAuth) {
    return next(); // 바로 통과
  }
  getRole;
  const token = getCookie("jwtToken");
  alert(token);
  if (token) {
    const decodedtoken = jwtDecode(token);
    if (decodedtoken.exp * 1000 > Date.now()) {
      const userRoles = decodedtoken.role.split(",");
      const routeRoles = to?.meta?.roles || [];
      const hasRequireRole =
        routeRoles.length === 0 ||
        routeRoles.some((role) => userRoles.includes(role));
      if (hasRequireRole) {
        next();
      }
    }
  }
  next("/login");
});
router.afterEach((to) => {
  const defaultTitle = "운영의 달인";
  document.title = to.meta.title || defaultTitle;
});

export default router;
