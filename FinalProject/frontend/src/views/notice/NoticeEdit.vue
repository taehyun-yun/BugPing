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
              <div class="attachment-text">이미지 첨부</div>
            </div>
            <div class="attachment-card1" @click="triggerFileUpload('image')">
              <img
                src="@/assets/noticeimg/add.png"
                alt="공지 아이콘"
                class="upload-icon"
              />
            </div>
            <!-- 숨겨진 이미지 입력창 -->
            <input
              ref="imageInput"
              type="file"
              accept="image/*"
              class="file-input-hidden"
              @change="handleImageUpload"
            />
            <!-- 이미지 미리보기 -->
            <div v-if="previewImage" class="image-preview">
              <img :src="previewImage" alt="미리보기" />
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
              <div class="attachment-text">파일 첨부</div>
            </div>
            <div class="attachment-card2" @click="triggerFileUpload('file')">
              <img
                src="@/assets/noticeimg/add.png"
                alt="공지 아이콘"
                class="upload-icon"
              />
            </div>
            <!-- 숨겨진 파일 입력창 -->
            <input
              ref="fileInput"
              type="file"
              multiple
              class="file-input-hidden"
              @change="handleFileUpload"
            />
            <!-- 기존 첨부 파일 -->
            <div v-if="existingFiles.length > 0" class="existing-files">
              <h3>기존 첨부 파일</h3>
              <ul>
                <li v-for="file in existingFiles" :key="file.fileId">
                  <a
                    :href="`${axiosAddress}/notice/files/${file.filePath}`"
                    target="_blank"
                  >
                    {{ file.filePath }}
                  </a>
                  <button
                    type="button"
                    @click="removeExistingFile(file.fileId)"
                  >
                    삭제
                  </button>
                </li>
              </ul>
            </div>
            <!-- 새로 첨부한 파일 목록 -->
            <div v-if="uploadedFiles.length > 0" class="uploaded-files-section">
              <h3>새로 첨부한 파일</h3>
              <ul class="uploaded-files">
                <li v-for="(file, index) in uploadedFiles" :key="index">
                  {{ file.name }}
                </li>
              </ul>
            </div>
          </div>
        </div>
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
import { axiosAddress } from "@/stores/axiosAddress";

const route = useRoute();
const router = useRouter();

// 공지사항 데이터를 저장하는 ref
const notice = ref({
  title: "",
  content: "",
  type: "공지", // 기본 카테고리
  files: [], // 기존 파일 목록
});

// 현재 파일들을 저장할 ref
const existingFiles = ref([]);

// 활성화된 카테고리
const activeCategory = ref("공지");

// 업로드된 파일 목록
const uploadedFiles = ref([]);

// 이미지 미리보기
const previewImage = ref("");

// 파일 input refs
const imageInput = ref(null);
const fileInput = ref(null);

// Removed file IDs
const removedFileIds = ref([]);

/**
 * 공지사항 데이터를 로드하는 함수
 */
const loadNotice = async () => {
  const noticeId = route.params.id; // URL에서 공지사항 ID 가져오기
  try {
    const response = await axios.get(
      `${axiosAddress}/notice/${noticeId}`,
      { withCredentials: true }
    );
    notice.value = response.data;

    // 백엔드에서 보내는 type 값에 따라 프론트엔드 카테고리 설정
    const typeMapReverse = {
      NOTICE: "공지",
      MANUAL: "매뉴얼",
      SPECIAL: "특이사항",
    };
    activeCategory.value = typeMapReverse[notice.value.type] || "공지";

    existingFiles.value = notice.value.files || [];

    // 이미지 미리보기 설정 (가정: 첫 번째 이미지가 메인 이미지)
    const imageFile = existingFiles.value.find((file) =>
      file.fileType.startsWith("image/")
    );
    if (imageFile) {
      (previewImage.value = `${axiosAddress}/notice/files/${imageFile.filePath}`),
        { withCredentials: true };
    }
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

  const typeMap = {
    공지: "NOTICE",
    매뉴얼: "MANUAL",
    특이사항: "SPECIAL",
  };

  const formData = new FormData();
  formData.append("title", notice.value.title);
  formData.append("content", notice.value.content);
  formData.append("type", typeMap[activeCategory.value]);

  // 삭제된 파일 ID 포함
  if (removedFileIds.value.length > 0) {
    formData.append("removedFileIds", JSON.stringify(removedFileIds.value));
  }

  // 이미지 파일 추가
  if (imageInput.value && imageInput.value.files[0]) {
    formData.append("image", imageInput.value.files[0]);
  }

  // 일반 파일 추가
  if (fileInput.value && fileInput.value.files.length > 0) {
    for (let i = 0; i < fileInput.value.files.length; i++) {
      formData.append("files", fileInput.value.files[i]);
    }
  }

  try {
    const response = await axios.put(
      `${axiosAddress}/notice/update/${noticeId}`,
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      }
    );
    alert("공지사항이 성공적으로 저장되었습니다.");
    router.push({ name: "noticedetail", params: { id: noticeId } });
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
  // 필요 시 notice.type 업데이트
  // notice.value.type = category; // 만약 'type'을 유지하고 싶다면
};

/**
 * 파일 업로드 창 열기
 */
const triggerFileUpload = (type) => {
  if (type === "image") {
    imageInput.value.click();
  } else if (type === "file") {
    fileInput.value.click();
  }
};

/**
 * 이미지 업로드 처리
 */
const handleImageUpload = (event) => {
  const file = event.target.files[0];
  if (file && file.type.startsWith("image/")) {
    const reader = new FileReader();
    reader.onload = () => {
      previewImage.value = reader.result;
    };
    reader.readAsDataURL(file);
    alert("이미지가 선택되었습니다: " + file.name);
  } else {
    alert("유효한 이미지 파일을 선택해주세요.");
  }
};

/**
 * 파일 업로드 처리
 */
const handleFileUpload = (event) => {
  const files = event.target.files;
  if (files.length > 0) {
    uploadedFiles.value = Array.from(files);
    alert(`${files.length}개의 파일이 선택되었습니다.`);
  } else {
    alert("유효한 파일을 선택해주세요.");
  }
};

/**
 * 편집 취소 함수
 */
const cancelEdit = () => {
  const noticeId = route.params.id; // URL에서 공지사항 ID 가져오기
  router.push({ name: "noticedetail", params: { id: noticeId } }); // 상세 페이지로 이동
};

/**
 * 기존 파일 제거 함수
 */
const removeExistingFile = (fileId) => {
  removedFileIds.value.push(fileId);
  existingFiles.value = existingFiles.value.filter(
    (file) => file.fileId !== fileId
  );
};

onMounted(() => {
  loadNotice();
});
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

/* 이미지 미리보기 */
.image-preview {
  margin-top: 10px;
}

.image-preview img {
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

/* 이미지, 파일 첨부 */

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

.attachment-img {
  width: 25px;
  height: 25px;
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

.attachment-card1 {
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

.attachment-card2 {
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

.attachment-card1:hover,
.attachment-card2:hover {
  background: #e7e7e7;
}

/* 숨겨진 파일 입력창 스타일 */
.file-input-hidden {
  display: none;
}

/* 기존 첨부 파일 */
.existing-files {
  margin-top: 10px;
}

.existing-files h3 {
  margin-bottom: 10px;
}

.existing-files ul {
  list-style: none;
  padding: 0;
}

.existing-files li {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
}

.existing-files a {
  margin-right: 10px;
  text-decoration: none;
  color: #007bff;
}

.existing-files button {
  padding: 2px 6px;
  font-size: 12px;
  background-color: #dc3545;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

/* 새로 첨부한 파일 목록 */
.uploaded-files-section {
  margin-top: 10px;
}

.uploaded-files-section h3 {
  margin-bottom: 10px;
}

.uploaded-files {
  list-style: none;
  padding: 0;
}

.uploaded-files li {
  background: #f5f5f5;
  padding: 5px 10px;
  border-radius: 5px;
  margin-bottom: 5px;
}
</style>
