import { createRouter, createWebHistory } from "vue-router";
import CalculatorPage from "../views/CalculatorPage.vue";
import NoticeMain from "../views/notice/NoticeMain.vue";
import NoticeDetail from "@/views/notice/NoticeDetail.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/cal",
      name: "cal",
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
  ],
});

export default router;
