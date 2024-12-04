import { createRouter, createWebHistory } from 'vue-router'
import CalculatorPage from '../views/CalculatorPage.vue';
import NoticePage from '../views/notice/NoticePage.vue';
import test from '../views/test.vue';
import contract from '../views/employment/AdministratorContract.vue';
import commute from '../views/commute/WorkerCommuting.vue';

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
      path: '/test',
      name: 'test',
      component: test
    },
    {
      path: '/contract',
      name: 'contract',
      component: contract
    },
    {
      path: '/commute',
      name: 'commute',
      component: commute
    },
  ],
});

export default router
