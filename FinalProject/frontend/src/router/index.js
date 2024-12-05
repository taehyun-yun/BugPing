import { createRouter, createWebHistory } from 'vue-router'
import CalculatorPage from '../views/CalculatorPage.vue';
import NoticePage from '../views/notice/NoticePage.vue';
import NoticeCreate from '../views/notice/NoticeCreate.vue';
import WorkerCommuting from '../views/commute/WorkerCommuting.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/cal',
      name: 'cal',
      component: CalculatorPage,
    },
    {
      path: '/notice/no',
      name: 'notice',
      component: NoticePage
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
})

export default router