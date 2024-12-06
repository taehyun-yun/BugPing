<template>
  <div class="main-wrapper">
    <div class="content-wrapper">
      <div class="notice-main">
        <!-- Tabs -->
        <div class="tabs">
          <button
            class="tab"
            :class="{ active: activeTab === 'all' }"
            @click="setActiveTab('all')"
          >
            전체 게시물
          </button>
          <button
            class="tab"
            :class="{ active: activeTab === 'my' }"
            @click="setActiveTab('my')"
          >
            내 게시물
          </button>
        </div>

        <!-- Categories -->
        <div class="categories-container">
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
          <!-- 공지 작성하기 버튼 -->
          <button class="create-notice-button" @click="createNotice">
            공지 작성하기
          </button>
        </div>

        <!-- Board Table -->
        <table class="board-table">
          <thead>
            <tr>
              <th>
                <div class="checkbox-action">
                  <button @click="deleteSelected" class="delete-button">
                    삭제
                  </button>
                  <input type="checkbox" @click="toggleAll" />
                </div>
              </th>
              <th>No.</th>
              <th>제목</th>
              <th>작성자</th>
              <th>보기권한</th>
              <th>작성 날짜</th>
              <th>상태</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in filteredItems" :key="item.id">
              <td class="checkbox-cell">
                <input type="checkbox" v-model="selectedItems" :value="item.id" />
              </td>
              <td>{{ index + 1 }}</td>
              <td>{{ item.title }}</td>
              <td>{{ item.writer }}</td>
              <td>{{ item.viewers }}</td>
              <td>{{ item.date }}</td>
              <td>{{ item.status }}</td>
            </tr>
          </tbody>
        </table>

        <!-- Pagination -->
        <div class="pagination">
          <button @click="prevPage" :disabled="currentPage === 1">&lt;</button>
          <span
            v-for="page in totalPages"
            :key="page"
            :class="{ active: currentPage === page }"
            @click="goToPage(page)"
            >{{ page }}</span
          >
          <button @click="nextPage" :disabled="currentPage === totalPages">
            &gt;
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const activeTab = ref('all');
const activeCategory = ref('공지');
const items = ref([
  {
    id: 1,
    title: '공지사항',
    writer: '강정현',
    viewers: '1명',
    date: '2024.11.28(목) 14:15',
    status: '',
  },
]);
const currentPage = ref(1);
const itemsPerPage = ref(5);
const selectedItems = ref([]);

const filteredItems = computed(() => {
  return items.value.slice(
    (currentPage.value - 1) * itemsPerPage.value,
    currentPage.value * itemsPerPage.value
  );
});

const totalPages = computed(() => {
  return Math.ceil(items.value.length / itemsPerPage.value);
});

const setActiveTab = (tab) => {
  activeTab.value = tab;
};

const setActiveCategory = (category) => {
  activeCategory.value = category;
};

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
};

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
};

const goToPage = (page) => {
  currentPage.value = page;
};

const toggleAll = (event) => {
  selectedItems.value = event.target.checked
    ? items.value.map((item) => item.id)
    : [];
};

const deleteSelected = () => {
  items.value = items.value.filter(
    (item) => !selectedItems.value.includes(item.id)
  );
  selectedItems.value = [];
};

const createNotice = () => {
  alert('공지 작성 페이지로 이동합니다.');
  router.push({ path: '/noticecreate' });
};
</script>

<style>
.main-wrapper {
  display: flex;
}

.content-wrapper {
  flex-grow: 1;
  padding: 20px;
}

.notice-main {
  max-width: 1400px; /* 최대 너비 설정 */
  margin: 0 auto; /* 콘텐츠를 중앙으로 정렬 */
  padding: 20px;
}

/* Tabs */
.tabs {
  display: flex;
  margin-bottom: 10px;
}

/* Categories Container */
.categories-container {
  display: flex;
  justify-content: space-between; /* 왼쪽: 카테고리, 오른쪽: 공지 작성 버튼 */
  align-items: center; /* 버튼들이 수직 중앙 정렬 */
  margin-bottom: 20px;
}

/* Categories */
.categories {
  display: flex;
  flex-direction: row; /* 가로 정렬로 변경 */
  gap: 10px; /* 버튼 간 간격 추가 */
}

/* Categories */
.categories .category {
  display: flex; /* 이미지와 텍스트를 나란히 배치 */
  justify-content: center; /* 수평 정렬 */
  gap: 8px; /* 이미지와 텍스트 간 간격 */
  padding: 10px 20px; /* 버튼 크기 조정 */
  font-size: 16px; /* 텍스트 크기 */
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #f9f9f9; /* 기본 배경색 */
  cursor: pointer;
  flex-shrink: 0; /* 동일한 크기 유지 */
  width: 260px; /* 버튼의 고정 너비 */
  height: 50px; /* 버튼의 고정 높이 */
  transition: all 0.2s ease; /* 호버 효과 */
}

/* 아이콘 이미지 */
.category img.category-icon {
  width: 20px; /* 아이콘 크기 */
  height: 20px; /* 아이콘 크기 */
  object-fit: contain; /* 이미지가 잘리지 않도록 설정 */
}

.categories .category:hover {
  background-color: #e0e0e0; /* 호버 시 배경색 변경 */
}

.categories .category.active {
  background-color: #007bff; /* 활성화된 버튼 색상 */
  color: #fff;
}

.action-buttons {
  display: flex;
  margin-bottom: 10px;
}

.tab,
.category,
.delete-button,
.create-button {
  margin-right: 10px;
  padding: 5px 10px;
  border: 1px solid #ddd;
  cursor: pointer;
}

.delete-button {
  background-color: #f5f5f5;
  border: 1px solid #ddd;
  padding: 5px 8px;
  font-size: 12px;
  border-radius: 4px;
}

.tab.active,
.category.active,
.delete-button:hover,
.create-button:hover {
  background-color: #007bff;
  color: #fff;
}

/* 공지 작성하기 버튼 컨테이너 */
.create-notice-container {
  display: flex;
  justify-content: flex-end; /* 오른쪽 정렬 */
  margin-bottom: 20px; /* 버튼 아래 여백 */
}

/* 공지 작성하기 버튼 */
.create-notice-button {
  padding: 10px 20px; /* 버튼 내부 여백 */
  font-size: 16px; /* 버튼 글씨 크기 */
  border: none; /* 테두리 제거 */
  border-radius: 4px; /* 둥근 모서리 */
  background-color: #007bff; /* 기본 배경색 */
  color: white; /* 글씨 색 */
  cursor: pointer;
  transition: all 0.3s ease; /* 부드러운 호버 효과 */
}

/* 호버 효과 */
.create-notice-button:hover {
  background-color: #0056b3; /* 호버 시 배경색 */
}

/* Checkboxes alignment */
.checkbox-action {
  display: flex;
  align-items: center;
  gap: 10px;
}

.checkbox-cell {
  display: flex;
  justify-content: center;
  align-items: center;
}

input[type="checkbox"] {
  width: 16px;
  height: 16px;
  margin: 0;
  vertical-align: middle;
}

.board-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
  border: none;
}

.board-table th,
.board-table td {
  padding: 10px;
  text-align: center;
  border-bottom: 1px solid #e0e0e0;
}

.board-table th {
  background-color: #f9f9f9;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 10px;
}

.pagination span {
  margin: 0 5px;
  cursor: pointer;
}

.pagination span.active {
  font-weight: bold;
}
</style>
