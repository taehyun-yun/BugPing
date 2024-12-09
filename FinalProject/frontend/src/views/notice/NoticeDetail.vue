<template>
  <div class="detail-container">
    <!-- 제목 및 작성자 정보 -->
    <div class="header">
      <div class="title">
        <img
          src="@/assets/noticeimg/megaphone.png"
          alt="공지 아이콘"
          class="icon"
        />
        <span>{{ notice.title }}</span>
        <!-- 공지사항 제목 표시 -->
      </div>
      <div class="info">
        <div class="profile">
          <img
            src="@/assets/noticeimg/profile.png"
            alt="프로필 아이콘"
            class="profile-img"
          />
          <span>{{ notice.work.user.name }}</span>
          <!-- 작성자 이름 표시 -->
        </div>
        <div class="date-info">
          <span>작성일 {{ formatDate(notice.createdAt) }}</span>
          <!-- 작성일 표시 -->
          <div class="divider"></div>
          <span v-if="notice.updatedAt"
            >수정일 {{ formatDate(notice.updatedAt) }}</span
          >
          <!-- 수정일 표시 -->
        </div>
      </div>
    </div>

    <!-- 내용 -->
    <div class="content">
      <p>{{ notice.content }}</p>
      <!-- 공지사항 내용 표시 -->
    </div>

    <!-- 버튼 -->
    <div class="actions">
      <button class="action-button" @click="editNotice">수정하기</button>
      <button class="action-button" @click="deleteNotice">삭제하기</button>
    </div>
  </div>
</template>

<script setup>
// Vue.js Composition API 사용
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";

const route = useRoute(); // 현재 라우트 정보 가져오기
const router = useRouter();

// 공지사항 데이터를 저장할 ref
const notice = ref({
  title: "",
  work: {
    user: {
      name: "",
    },
  },
  content: "",
  createdAt: "",
  updatedAt: null,
});

/**
 * 특정 ID의 공지사항을 백엔드에서 가져오는 함수
 */
const fetchNotice = async () => {
  const noticeId = route.params.id;
  console.log("Received notice ID:", noticeId); // 디버깅 로그

  try {
    const response = await axios.get(
      `http://localhost:8707/notice/${noticeId}`
    );
    notice.value = response.data;
  } catch (error) {
    console.error("Error fetching notice:", error);
    router.push({ name: "notice" });
  }
};

/**
 * 공지사항 수정 페이지로 이동하는 함수
 */
const editNotice = () => {
  router.push({ name: "noticeEdit", params: { id: route.params.id } });
};

/**
 * 공지사항 삭제 함수
 */
const deleteNotice = async () => {
  if (confirm("이 공지사항을 삭제하시겠습니까?")) {
    try {
      await axios.delete("http://localhost:8707/notice/delete", {
        data: [notice.value.noticeId], // 삭제할 공지사항 ID를 배열로 전송
      });
      alert("공지사항이 삭제되었습니다.");
      router.push({ name: "NoticeMain" }); // 삭제 후 공지 메인 페이지로 이동
    } catch (error) {
      console.error("공지사항 삭제 중 오류 발생:", error);
      alert("공지사항 삭제 중 오류가 발생했습니다.");
    }
  }
};

/**
 * 날짜 형식을 원하는 형식으로 변환하는 함수
 * @param dateString 변환할 날짜 문자열
 * @return 변환된 날짜 문자열
 */
const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  // 'ko-KR' 로케일과 옵션을 사용하여 날짜 형식 지정
  return date
    .toLocaleDateString("ko-KR", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
    })
    .replace(/\./g, "-");
};

/**
 * 컴포넌트가 마운트될 때 공지사항을 가져옵니다.
 */
onMounted(() => {
  fetchNotice();
});
</script>

<style scoped>
.detail-container {
  width: 80%;
  padding: 20px;
  background-color: #ffffff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  box-shadow: 0px 10px 20px rgba(0, 0, 0, 0.1);
  margin: auto;
}

/* 헤더 */
.header {
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;
}

.title {
  display: flex;
  align-items: center;
  font-size: 24px;
  font-weight: bold;
  color: #333333;
  margin-bottom: 10px;
}

.title .icon {
  width: 24px;
  height: 24px;
  margin-right: 10px;
}

.info {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #666666;
  align-items: center;
}

.profile {
  display: flex;
  align-items: center;
}

.profile .profile-img {
  width: 24px;
  height: 24px;
  margin-right: 8px; /* 이미지와 이름 사이의 간격 */
}

.date-info {
  display: flex;
  align-items: center;
}

.divider {
  width: 1px;
  height: 14px;
  background-color: #e0e0e0;
  margin: 0 10px;
}

/* 내용 */
.content {
  background-color: #f9f9f9;
  min-height: 300px; /* 내용 영역의 최소 높이 */
  border-radius: 8px;
  margin-bottom: 20px;
  padding: 20px;
  font-size: 16px;
  color: #333333;
}

/* 버튼 */
.actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.action-button {
  padding: 10px 20px;
  font-size: 14px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #ffffff;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-button:hover {
  background-color: #007bff;
  color: #ffffff;
  border-color: #007bff;
}
</style>
