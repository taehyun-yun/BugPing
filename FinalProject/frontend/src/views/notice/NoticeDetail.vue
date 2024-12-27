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
          <span>{{ notice.work?.userName || "작성자 없음" }}</span>
          <!-- 작성자 이름 표시 -->
        </div>
        <div class="date-info">
          <span>작성일 {{ formatDate(notice.createdAt) }}</span>
          <!-- 작성일 표시 -->
          <div class="divider"></div>
          <span v-if="notice.updatedAt">
            수정일 {{ formatDate(notice.updatedAt) }}
          </span>
          <!-- 수정일 표시 -->
        </div>
      </div>
    </div>

    <!-- 내용 -->
    <div class="content">
      <p>{{ notice.content }}</p>
      <!-- 공지사항 내용 표시 -->
    </div>

    <!-- 파일 및 이미지 표시 -->
    <div class="attachments" v-if="notice.files && notice.files.length > 0">
      <h2>첨부 파일</h2>
      <ul>
        <li
          v-for="file in notice.files"
          :key="file.fileId"
          class="attachment-item"
        >
          <!-- 이미지 미리보기 -->
          <img
            :src="`${axios}/notice/files/${file.filePath}`"
            alt="첨부 이미지"
            class="preview-image"
            width="800"
            height="500"
          />
          <!-- 이미지 원본 보기 -->
          <a
            :href="`${axiosAddress}/notice/files/${file.filePath}`"
            target="_blank"
            class="view-link"
          >
            원본 보기
          </a>
        </li>
      </ul>
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
import { axiosAddress } from "@/stores/axiosAddress";

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
  files: [], // 파일 리스트 추가
});

/**
 * 특정 ID의 공지사항을 백엔드에서 가져오는 함수
 */
const fetchNotice = async () => {
  const noticeId = route.params.id;
  console.log("Received notice ID:", noticeId);

  try {
    const response = await axios.get(`${axiosAddress}/notice/${noticeId}`);
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
      await axios.delete(`${axiosAddress}/notice/delete`, {
        data: [notice.value.noticeId],
      });
      alert("공지사항이 삭제되었습니다.");
      router.push({ name: "notice" });
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

  // 날짜
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");

  // 시간
  const hours = date.getHours();
  const minutes = String(date.getMinutes()).padStart(2, "0");
  const period = hours >= 12 ? "오후" : "오전";

  const formattedHours = hours % 12 || 12;

  return `${year}-${month}-${day} ${period} ${formattedHours}:${minutes}`;
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
  max-width: 900px; /* 최대 너비 설정 */
  margin: 0 auto; /* 좌우 가운데 정렬 */
  background-color: #ffffff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  box-shadow: 0px 10px 20px rgba(0, 0, 0, 0.1);
  margin-top: 40px;
  transform: translateX(-20px);
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
/* 첨부 파일 */
.attachments {
  margin-bottom: 20px;
}

.attachments h2 {
  font-size: 20px;
  margin-bottom: 10px;
}

.attachments ul {
  list-style: none;
  padding: 0;
}

.attachments li {
  margin-bottom: 5px;
}

.attachments a {
  color: #3399fe;
  text-decoration: none;
}

.attachments a:hover {
  text-decoration: underline;
}
</style>
