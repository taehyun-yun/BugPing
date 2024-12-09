<template>
  <div class="notice-edit">
    <h1 class="category-header">카테고리</h1>

    <!-- 카테고리 버튼들 -->
    <div class="categories">
      <button
        class="category"
        :class="{ active: activeCategory === '공지' }"
        @click="setActiveCategory('공지')"
      >
        <img
          src="@/assets/noticeimg/megaphone.png"
          alt="공지 아이콘"
          class="category-icon"
        />
        공지
      </button>
      <button
        class="category"
        :class="{ active: activeCategory === '매뉴얼' }"
        @click="setActiveCategory('매뉴얼')"
      >
        <img
          src="@/assets/noticeimg/checklist.png"
          alt="매뉴얼 아이콘"
          class="category-icon"
        />
        매뉴얼
      </button>
      <button
        class="category"
        :class="{ active: activeCategory === '특이사항' }"
        @click="setActiveCategory('특이사항')"
      >
        <img
          src="@/assets/noticeimg/question-mark.png"
          alt="특이사항 아이콘"
          class="category-icon"
        />
        특이사항
      </button>
    </div>

    <!-- 공지사항 수정 폼 -->
    <form @submit.prevent="saveNotice" class="notice-form">
      <div class="form-group">
        <label for="title">제목<span class="required">*</span></label>
        <input
          type="text"
          id="title"
          v-model="notice.title"
          placeholder="입력하세요"
          required
        />
      </div>

      <div class="form-group">
        <label for="content">내용<span class="required">*</span></label>
        <textarea
          id="content"
          v-model="notice.content"
          placeholder="입력하세요"
          required
        ></textarea>
      </div>

      <!-- 저장, 취소 버튼 -->
      <div class="actions">
        <button type="submit" class="save-button">저장</button>
        <button type="button" class="cancel-button" @click="cancelEdit">
          취소
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";

const route = useRoute();
const router = useRouter();

// 공지사항 데이터를 저장하는 ref
const notice = ref({
  title: "",
  content: "",
  type: "공지", // 기본 카테고리
});

const activeCategory = ref("공지");

/**
 * 공지사항 데이터를 로드하는 함수
 */
const loadNotice = async () => {
  const noticeId = route.params.id; // URL에서 공지사항 ID 가져오기
  try {
    const response = await axios.get(
      `http://localhost:8707/notice/${noticeId}`
    );
    notice.value = response.data;
    activeCategory.value = response.data.type;
  } catch (error) {
    console.error("공지사항을 불러오는 중 오류 발생:", error);
    alert("공지사항을 불러오는 중 오류가 발생했습니다.");
    router.push({ name: "notice" }); // 오류 시 목록 페이지로 이동
  }
};

/**
 * 공지사항을 저장하는 함수
 */
const saveNotice = async () => {
  const noticeId = route.params.id;
  try {
    await axios.put(
      `http://localhost:8707/notice/update/${noticeId}`,
      notice.value
    );
    alert("공지사항이 성공적으로 저장되었습니다.");
    router.push({ name: "noticedetail", params: { id: noticeId } }); // 저장 후 상세 페이지로 이동
  } catch (error) {
    console.error("공지사항 저장 중 오류 발생:", error);
    alert("공지사항 저장 중 오류가 발생했습니다.");
  }
};

/**
 * 카테고리를 설정하는 함수
 */
const setActiveCategory = (category) => {
  activeCategory.value = category;
  notice.value.type = category;
};

onMounted(() => {
  loadNotice();
});

const cancelEdit = () => {
  const noticeId = route.params.id; // URL에서 공지사항 ID 가져오기
  router.push({ name: "noticedetail", params: { id: noticeId } }); // 상세 페이지로 이동
};
</script>

<style scoped>
.notice-edit {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.category-header {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
}

.categories {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.category {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #f9f9f9;
  cursor: pointer;
  transition: background-color 0.3s;
}

.category.active {
  background-color: #007bff;
  color: #fff;
}

.category-icon {
  width: 20px;
  height: 20px;
}

.notice-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

label {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
}

.required {
  color: red;
  margin-left: 4px;
}

input,
textarea {
  padding: 10px;
  font-size: 14px;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 100%;
  box-sizing: border-box;
}

textarea {
  resize: vertical;
  min-height: 100px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
}

.save-button {
  padding: 10px 20px;
  font-size: 16px;
  border: none;
  border-radius: 4px;
  background-color: #007bff;
  color: #fff;
  cursor: pointer;
  transition: background-color 0.3s;
}

.save-button:hover {
  background-color: #0056b3;
}

.cancel-button {
  padding: 10px 20px;
  font-size: 14px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #ffffff;
  color: #333333;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.cancel-button:hover {
  background-color: #f8f9fa;
  border-color: #cccccc;
}
</style>
