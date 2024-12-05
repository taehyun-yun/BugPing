import { createRouter, createWebHistory } from 'vue-router'
import CalculatorPage from '../views/CalculatorPage.vue';
import NoticePage from '../views/notice/NoticePage.vue';
import Schedule from '../views/Schedule.vue';

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
      path: '/schedule',
      name: 'schedule',
      component: Schedule
    }
  ],
})

export default router