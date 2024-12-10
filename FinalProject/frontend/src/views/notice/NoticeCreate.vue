<template>
  <div class="page-container">
    <div class="main-content">
      <!-- 공지사항 작성 페이지 UI -->
      <div class="notice-page">
        <div class="category-main">
          <h1>카테고리</h1>
        </div>

        <!-- 카테고리 선택 -->
        <div class="category-section">
          <div
            v-for="category in categories"
            :key="category.id"
            class="category-card"
            :class="{ selected: selectedCategory === category.name }"
            @click="selectCategory(category.name)"
          >
            <img
              v-if="category.icon"
              :src="category.icon"
              alt="카테고리 아이콘"
              class="category-icon"
            />
            <div class="category-text">{{ category.name }}</div>
          </div>
        </div>

        <!-- 제목 입력 -->
        <div class="input-section">
          <label class="input-label" for="title"
            >제목<span class="required">*</span></label
          >
          <input
            type="text"
            id="title"
            v-model="title"
            placeholder="입력하세요"
            class="input-field"
          />
        </div>

        <!-- 내용 입력 -->
        <div class="input-section">
          <label class="input-label" for="content"
            >내용<span class="required">*</span></label
          >
          <textarea
            id="content"
            v-model="content"
            placeholder="입력하세요"
            class="textarea-field"
          ></textarea>
        </div>

        <!-- 이미지 및 파일 첨부 -->
        <div class="attachment-section">
          <div class="attachment-wrapper">
            <!-- 이미지 첨부 -->
            <div class="attachment-item">
              <div class="attachment-header">
                <img
                  src="@/assets/noticeimg/image.png"
                  alt="공지 아이콘"
                  class="attachment-img"
                />
                <img
                  v-if="attachments[0].icon"
                  :src="attachments[0].icon"
                  alt="이미지 첨부 아이콘"
                  class="attachment-icon"
                />
                <div class="attachment-text">이미지 첨부</div>
              </div>
              <div class="attachment-card">
                <img
                  src="@/assets/noticeimg/add.png"
                  alt="공지 아이콘"
                  class="upload-icon"
                />
              </div>
            </div>

            <!-- 구분선 -->
            <div class="divider-vertical"></div>

            <!-- 파일 첨부 -->
            <div class="attachment-item">
              <div class="attachment-header">
                <img
                  src="@/assets/noticeimg/file.png"
                  alt="공지 아이콘"
                  class="attachment-img"
                />
                <img
                  v-if="attachments[1].icon"
                  :src="attachments[1].icon"
                  alt="파일 첨부 아이콘"
                  class="attachment-icon"
                />
                <div class="attachment-text">파일 첨부</div>
              </div>
              <div class="attachment-card">
                <img
                  src="@/assets/noticeimg/add.png"
                  alt="공지 아이콘"
                  class="upload-icon"
                />
              </div>
              <div v-if="attachments[1].info" class="attachment-info">
                {{ attachments[1].info }}
              </div>
            </div>
          </div>
        </div>

        <!-- 버튼 섹션 -->
        <div class="button-section">
          <button @click="cancelNotice" class="cancel-button">취소</button>
          <button @click="submitNotice" class="submit-button">작성완료</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";
import megaphoneIcon from "@/assets/noticeimg/megaphone.png";
import checklistIcon from "@/assets/noticeimg/checklist.png";
import questionMarkIcon from "@/assets/noticeimg/question-mark.png";

const router = useRouter();

// 카테고리와 첨부 파일 데이터
const categories = ref([
  { id: 1, name: "공지", icon: megaphoneIcon },
  { id: 2, name: "매뉴얼", icon: checklistIcon },
  { id: 3, name: "특이사항", icon: questionMarkIcon },
]);

const attachments = ref([
  { id: 1, label: "이미지 첨부", icon: "" },
  {
    id: 2,
    label: "파일 첨부",
    icon: "",
    info: "(문서 파일만 업로드 가능, 파일당 최대 50MB)",
  },
]);

// 필수 입력 항목에 대한 ref 상태 관리
const title = ref("");
const content = ref("");

// 카테고리 선택 상태
const selectedCategory = ref(categories.value[0].name); // 기본값: 첫 번째 카테고리 이름

// 예시로 현재 사용자의 workId를 하드코딩 (실제 프로젝트에서는 인증을 통해 동적으로 가져와야 함)
const workId = ref(1);

// 카테고리 선택 함수
const selectCategory = (categoryName) => {
  selectedCategory.value = categoryName; // 선택한 카테고리 이름으로 업데이트
};

// 작성 취소 버튼 클릭 시 동작
const cancelNotice = () => {
  // 모든 입력 필드를 초기화
  title.value = "";
  content.value = "";
  selectedCategory.value = "공지"; // 기본 카테고리로 초기화
  console.log("작성 취소되었습니다.");
};

// 작성 완료 버튼 클릭 시 동작
const submitNotice = async () => {
  if (!title.value || !content.value) {
    alert("제목과 내용을 모두 입력해주세요.");
    return;
  }

  const typeMap = {
    공지: "NOTICE",
    매뉴얼: "MANUAL",
    특이사항: "SPECIAL",
  };

  const noticeData = {
    title: title.value,
    content: content.value,
    work: {
      workId: workId.value,
    },
    type: typeMap[selectedCategory.value], // 선택된 카테고리를 type으로 설정
    // 필요한 다른 필드들을 추가할 수 있습니다.
  };

  try {
    const response = await axios.post(
      "http://localhost:8707/notice/create",
      noticeData
    );
    console.log("작성된 공지사항:", response.data);
    alert("공지사항이 작성되었습니다.");
    // 작성 후 공지사항 목록 페이지로 이동
    router.push({ path: "/noticemain" });
  } catch (error) {
    console.error("공지사항 작성 중 오류 발생:", error);
    alert("공지사항 작성 중 오류가 발생했습니다.");
  }
};
</script>

<style scoped>
.page-container {
  display: flex;
}

.main-content {
  flex: 1;
  padding: 20px;
}

.notice-page {
  max-width: 1100px;
  margin: 0 auto;
}

.category-section {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
}

.category-card {
  display: flex;
  align-items: center;
  justify-content: center; /* 가운데 정렬 */
  background: #f5f5f5;
  padding: 10px 20px;
  border-radius: 10px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  width: 20%; /* 가로로 넓히기 위해 퍼센트 단위 사용 */
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
}

.category-icon {
  width: 24px;
  height: 24px;
  margin-right: 10px;
}

.category-text {
  font-size: 18px;
  color: #000;
}

.category-card.selected {
  border-color: #007bff; /* 선택된 카테고리 강조 */
  background-color: #e7f3ff;
}

.input-section {
  margin-bottom: 20px;
  height: 80%;
}

.input-label {
  display: block;
  font-size: 16px;
  margin-bottom: 8px;
}

.required {
  color: #ff0000;
}

.input-field {
  width: 100%;
  height: 40px;
  padding: 10px;
  border-radius: 5px;
  border: 1px solid #c3c3c3;
}

.textarea-field {
  width: 100%;
  height: 370px;
  padding: 10px;
  border-radius: 5px;
  border: 1px solid #c3c3c3;
}

.attachment-section {
  margin-bottom: 30px;
}

.attachment-wrapper {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 20px;
}

.attachment-item {
  text-align: left; /* 왼쪽 정렬 */
}

.attachment-header {
  display: flex;
  align-items: center;
  margin-bottom: 5px; /* 아이콘과 텍스트 위쪽 간격 */
}

.attachment-icon,
.attachment-img {
  width: 20px;
  height: 20px;
  margin-right: 10px; /* 아이콘과 텍스트 사이 간격 */
  vertical-align: middle; /* 텍스트와 아이콘을 중앙 정렬 */
}

.attachment-text {
  font-size: 14px;
  font-weight: bold;
  line-height: 20px; /* 텍스트와 아이콘의 높이를 맞추기 위해 line-height 설정 */
  display: flex;
  align-items: center; /* 텍스트를 세로 중앙에 위치 */
}

.attachment-card {
  background: #f5f5f5;
  padding: 10px;
  border-radius: 10px;
  width: 200px;
  height: 38px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  position: relative;
}

.attachment-info {
  font-size: 10px;
  color: #656565;
  margin-top: 8px; /* 첨부 상자 아래 간격 설정 */
  margin-left: 5px; /* 첨부 상자와의 약간의 좌측 여백 */
}

.divider-vertical {
  width: 100%;
  height: 1px;
  background-color: #e0e0e0;
  margin: 10px 0;
}

.button-section {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}

.cancel-button,
.submit-button {
  padding: 10px 20px;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
}

.cancel-button {
  background-color: #ffffff;
  color: #000;
  border: 1px solid #c3c3c3;
}

.submit-button {
  background-color: #3399fe;
  color: #ffffff;
}

.category-main {
  padding: 20px 10px;
}

.category-main h1 {
  font-size: 28px;
  font-weight: bold;
  margin: 20px 0;
}
</style>
