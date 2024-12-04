import { createRouter, createWebHistory } from "vue-router";
import notice from "../views/notice.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/notice",
      name: "notice",
      component: notice,
    },
  ],
});

export default router;
