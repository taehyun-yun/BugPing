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
            <!-- 업로드된 파일 목록 -->
            <ul v-if="uploadedFiles.length > 0" class="uploaded-files">
              <li v-for="(file, index) in uploadedFiles" :key="index">
                {{ file.name }}
              </li>
            </ul>
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
    activeCategory.value = notice.value.type;
    existingFiles.value = notice.value.files || [];
    // 이미지 미리보기 설정 (가정: 첫 번째 이미지가 메인 이미지)
    const imageFile = existingFiles.value.find((file) =>
      file.fileType.startsWith("image/")
    );
    if (imageFile) {
      previewImage.value = `http://localhost:8707/notice/files/${imageFile.filePath}`;
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
      `http://localhost:8707/notice/update/${noticeId}`,
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
  notice.value.type = category;
};

/**
 * 파일 업로드 창 열기
 */
const triggerFileUpload = (type) => {
  if (type === "image") {
    document.querySelector('input[type="file"][accept="image/*"]').click();
  } else if (type === "file") {
    document.querySelector('input[type="file"]:not([accept])').click();
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
  max-width: 200px;
  max-height: 200px;
  border-radius: 5px;
}

/* 첨부 파일 목록 */
.uploaded-files {
  list-style: none;
  padding: 0;
  margin-top: 10px;
}

.uploaded-files li {
  background: #f5f5f5;
  padding: 5px 10px;
  border-radius: 5px;
  margin-bottom: 5px;
}
</style>
