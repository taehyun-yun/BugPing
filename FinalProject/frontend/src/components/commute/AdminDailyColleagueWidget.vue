<template>
    <div class="attendance-list-wrapper">
       <div class="attendance-list">
       <div 
           v-for="(record, index) in attendanceList" 
           :key="index"
           class="attendance-item"
       >
           <div class="user-profile">
           <img 
               :src="record.image || '/placeholder-avatar.png'" 
               :alt="`${record.name}의 프로필`"
               class="profile-image"
           />
           </div>
           <div class="attendance-details">
           <span class="user-name">{{ record.name }}</span>
           <p class="attendance-message">{{ record.message }}</p>
           </div>
           <button 
           @click="handleReply(record.name)"
           class="reply-button"
           >
           REPLY
           </button>
       </div>
       </div>
   </div>
</template>

<script setup>
import { ref } from 'vue'

const attendanceList = ref([
 {
   name: 'John Doe',
   message: 'Active - In office',
   image: 'https://via.placeholder.com/150'
 },
 {
   name: 'Jane Smith',
   message: 'Remote - Working from home',
   image: 'https://via.placeholder.com/150'
 },
 {
   name: 'Emily Johnson',
   message: 'On leave - Back next week',
   image: 'https://via.placeholder.com/150'
 }
]);

function handleReply(name) {
 alert(`Replying to ${name}`);
}
</script>

<style scoped>
.attendance-list-wrapper {
 overflow: hidden;
}

.attendance-list {
 height: 100%;
 overflow-x: hidden; /* 강제로 좌우 스크롤 방지 */
 overflow-y: auto;
 padding-top: 40px;
}

.attendance-item {
 display: flex;
 align-items: center;
 gap: 1rem;
 padding: 1rem;
 background: #f8f9fa;
 border-radius: 0.75rem;
 margin-bottom: 1rem;
}

.attendance-item:last-child {
  margin-bottom: 0;
}

.user-profile {
 flex-shrink: 0;
}

.profile-image {
 width: 40px;
 height: 40px;
 border-radius: 50%;
 object-fit: cover;
}

.attendance-details {
 flex: 1;
}

.user-name {
 font-weight: 600;
 color: #2d3748;
 margin-bottom: 0.25rem;
 display: block;
}

.attendance-message {
 color: #718096;
 font-size: 0.875rem;
 margin: 0;
}

.reply-button {
 padding: 0.5rem 1rem;
 color: #4fd1c5;
 font-weight: 600;
 font-size: 0.75rem;
 border: none;
 background: transparent;
 cursor: pointer;
 transition: color 0.2s;
}

.reply-button:hover {
 color: #38b2ac;
}

.attendance-list::-webkit-scrollbar {
 width: 6px;
}

.attendance-list::-webkit-scrollbar-track {
 background: #f1f1f1;
 border-radius: 3px;
}

.attendance-list::-webkit-scrollbar-thumb {
 background: #cbd5e0;
 border-radius: 3px;
}

.attendance-list::-webkit-scrollbar-thumb:hover {
 background: #a0aec0;
}
</style>
