import { createRouter, createWebHistory } from 'vue-router'
import CalculatorPage from '../views/CalculatorPage.vue';
import NoticePage from '../views/notice/NoticePage.vue';
import NoticeCreate from '../views/notice/NoticeCreate.vue';
import WorkerCommuting from '../views/commute/WorkerCommuting.vue';
import NoticeDetail from "@/views/notice/NoticeDetail.vue";
import NoticeMain from "../views/notice/NoticeMain.vue";

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
      path: '/notice/create',
      name: 'noticeCretate',
      component: NoticeCreate
    },
    {
      path: '/commute',
      name: 'WorkerCommuting',
      component: WorkerCommuting
    },
  ],
});

export default router;
