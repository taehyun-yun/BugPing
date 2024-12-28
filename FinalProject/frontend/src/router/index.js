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
import SignUpView from "@/views/auth/SignUpView.vue";
import SU1 from "@/components/auth/SU1.vue";
import SU2 from "@/components/auth/SU2.vue";
import FindIdPwView from "@/views/auth/FindIdPwView.vue";
import Find from "@/components/auth/Find.vue";
import Main from "@/views/Main.vue";
import EnrollEmployee from "@/views/enroll/enrollEmployee.vue";
import EnrollWorkplace from "@/views/enroll/enrollWorkplace.vue";
import Home from "@/views/Home.vue";
import QRCheck from "@/components/commute/QRCheck.vue";
import AdministratorDailyCommuting from "@/views/commute/AdministratorDailyCommuting.vue";
import AdminDailyWidget from "@/components/commute/AdminDailyWidget.vue";
//import CommuteEmployee from "@/views/commute/CommuteEmployee.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    //빈깡통
    { path: "/", component: Main, meta: { requiresAuth: true } },
    //로그인
    {
      path: "/login",
      name: "login",
      component: LoginView,
      meta: { title: "로그인", onlyBeforeLogin: true },
    },
    {
      path: "/signup",
      name: "signup",
      component: SignUpView,
      meta: { title: "회원가입", onlyBeforeLogin: true },
      children: [
        { path: "su1", name: "su1", component: SU1 },
        { path: "su2", name: "su2", component: SU2 },
      ],
    },
    //아이디 비밀번호 찾기
    {
      path: "/findIdPw",
      name: "findIdPw",
      component: FindIdPwView,
      meta: { title: "아이디/비밀번호 찾기", onlyBeforeLogin: true },
      children: [{ path: "find", name: "find", component: Find }],
    },
    //근태 체크용
    {
      path: "/qrCheck",
      name: "qrCheck",
      component: QRCheck,
      meta: { title: "check" },
    },

    //근무자 전용
    {
      path: "/",
      component: Main,
      meta: {
        header: true,
        sidebar: true,
        requiresAuth: true,
        roles: ["employee"],
      },
      children: [
        {
          path: "enrollEmployee",
          name: "enrollEmployee",
          component: EnrollEmployee,
          meta: { title: "근무지 등록" },
        },
      ],
    },
    //고용자 전용
    {
      path: "/",
      component: Main,
      meta: {
        header: true,
        sidebar: true,
        requiresAuth: true,
        roles: ["employer"],
      },
      children: [
        {
          path: "calculator",
          name: "CalculatorPage",
          component: CalculatorPage,
          meta: { title: "지급내역" },
        },
        {
          path: "enrollWorkplace",
          name: "enrollWorkplace",
          component: EnrollWorkplace,
          meta: { title: "사업장 등록" },
        },
        //이 안에 넣으시면 됩니다.
      ],
    },
    // 공용 또는 미지정
    {
      path: "/",
      component: Main,
      meta: { header: true, sidebar: true, requiresAuth: true },
      children: [
        // { path: 'calculator', name: 'CalculatorPage', component: CalculatorPage, meta : { title : "지급내역", }, },
        { path: "home", name: "home", component: Home, meta: {} },
        {
          path: "noticemain",
          name: "notice",
          component: NoticeMain,
          meta: { title: "알림" },
        },
        {
          path: "noticedetail/:id",
          name: "noticedetail",
          component: NoticeDetail,
          meta: { title: "공지상세" },
        },
        {
          path: "noticecreate/:workId",
          name: "noticeCreate",
          component: NoticeCreate,
          meta: { title: "알림" },
        },
        {
          path: "noticeedit/:id",
          name: "noticeEdit",
          component: NoticeEdit,
          meta: { title: "공지 수정" },
        },
        {
          path: "commute",
          name: "commute",
          component: Commute,
          meta: { title: "근태" },
        },
        {
          path: "schedule",
          name: "schedule",
          component: Schedule,
          meta: { title: "스케쥴" },
        },
        {
          path: "contract",
          name: "contract",
          component: Contract,
          meta: { title: "계약" },
        },
        {
          path: "adminMainPage",
          name: "AdministratorDailyCommuting",
          component: AdministratorDailyCommuting,
          meta: { title: "근무자 메인 페이지" },
        },
        //이 안에 넣으시면 됩니다.
      ],
    },
    // ↓↓예시↓↓ 인증이 필요한 페이지는 뒤에 meta: {requiresAuth: true } 넣어주면 됩니다. ↓↓예시↓↓
    //{ path: '/protected', name: 'Protected', component: ProtectedPage, meta: { header : true, sidebar : true, requiresAuth: true, roles: ['employer'], } }
    //{ path: '/unprotected', name: 'UnProtected', component: UnProtectedPage, }
  ],
});

const getRole = async () => {
  let roles;
  await axios
    .get(axiosAddress + "/findrole", { withCredentials: true })
    .then((res) => {
      roles = res.data.roles.map((role) => role.replace("ROLE_", ""));
    });
  if (roles[0] == "ANONYMOUS") {
    return [false, roles]; //앞에 값은 로그인 되었는지 안되었는지를 구분, 뒤에 값은 역할 배열임
  }
  return [true, roles];
};

// 전역 가드 설정
router.beforeEach(async (to, from, next) => {
  if (!to.matched.length) {
    return next({ name: "home" }); // 기본 경로로 이동
  }
  //부모 meta 상속하기
  if (to.matched.length > 0) {
    const mergeMeta = to.matched.reduce((meta, record) => {
      return { ...meta, ...(record.meta ?? {}) };
    }, {});
    to.meta = mergeMeta;
  }
  //로그인하면 못가는 페이지처리
  if (to?.meta?.onlyBeforeLogin) {
    let auth = await getRole();
    return !auth[0] ? next() : next("/home");
  }
  //로그인이 필요없다면,
  if (!to?.meta?.requiresAuth) {
    return next(); // 바로 통과
  }
  //로그인이 필요하다면
  let auth = await getRole();
  //alert("로그인 하였나요? "+auth[0]);
  if (!auth[0]) {
    return next("/login");
  }
  //역할이 없다면
  if (!to?.meta?.roles) {
    return next();
  }
  //역할이 있다면
  const userRoles = auth[1];
  const routeRoles = to?.meta?.roles || [];
  //alert("아이디의 역할 "+userRoles+" vs 필요한 역할 "+routeRoles);
  const hasRequireRole =
    routeRoles.length === 0 ||
    routeRoles.some((role) => userRoles.includes(role));
  if (hasRequireRole) {
    return next();
  } else {
    //alert("역할이 맞지않음")
    return next("/login");
  }
});
router.afterEach((to) => {
  const defaultTitle = "운영의 달인";
  document.title = to.meta.title || defaultTitle;
});

export default router;
