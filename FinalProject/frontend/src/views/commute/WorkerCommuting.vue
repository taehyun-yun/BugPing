<template>
  <div class="container">
    <div class="main-content">
      <WorkerProfile />
      <WorkerWidget />
    </div>
  </div>
</template>

<script>
import MainSidebar from '@/components/MainSidebar.vue';
import MainHeader from '@/components/MainHeader.vue';
import WorkerProfile from '@/components/commute/WorkerProfile.vue';
import WorkerWidget from '@/components/commute/WorkerWidget.vue';

export default {
  name: "WorkerCommuting",
  components: {
    MainSidebar,
    MainHeader,
    WorkerProfile,
    WorkerWidget,
  },
};
</script>
