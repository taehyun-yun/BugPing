<template>
  <div class="container">
    <div class="sidebar">
      <MainSidebar />
    </div>
    <div class="main-content">
      <MainHeader />
      <WorkerWidget />
      <WorkerCommuteTable />
    </div>
  </div>
</template>

<script>
import MainSidebar from '@/components/MainSidebar.vue';
import MainHeader from '@/components/MainHeader.vue';
import WorkerWidget from '@/components/commute/WorkerWidget.vue';
import WorkerCommuteTable from '@/components/commute/WorkerCommuteTable.vue';

export default {
  name: "WorkerCommuting",
  components: {
    MainSidebar,
    MainHeader,
    WorkerWidget,
    WorkerCommuteTable,
  },
};
</script>

<style scoped>
.container {
  display: flex;
}

.sidebar {
  width: 250px;
}

.main-content {
  flex-grow: 1;
  padding: 20px;
}
</style>
