<template>
  <div class="main-wrapper">
    <div class="content-wrapper">
      <div class="notice-main">
        <!-- 탭 섹션 -->
        <div class="tabs">
          <!-- 전체 게시물 탭 -->
          <!-- activeTab이 'all'일 경우 active 클래스 추가 -->
          <!-- 클릭 시 setActiveTab 메소드 호출 -->
          <button
            class="tab"
            :class="{ active: activeTab === 'all' }"
            @click="setActiveTab('all')"
          >
            전체 게시물
          </button>
          <!-- 내 게시물 탭 -->
          <!-- activeTab이 'my'일 경우 active 클래스 추가 -->
          <!-- 클릭 시 setActiveTab 메소드 호출 -->
          <button
            class="tab"
            :class="{ active: activeTab === 'my' }"
            @click="setActiveTab('my')"
          >
            내 게시물
          </button>
        </div>

        <!-- 카테고리 및 공지 작성 버튼 섹션 -->
        <div class="categories-container">
          <!-- 카테고리 버튼들 -->
          <div class="categories">
            <!-- 공지 카테고리 버튼 -->
            <!-- activeCategory이 '공지'일 경우 active 클래스 추가 -->
            <!-- 클릭 시 setActiveCategory 메소드 호출 -->
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
            <!-- 매뉴얼 카테고리 버튼 -->
            <!-- activeCategory이 '매뉴얼'일 경우 active 클래스 추가 -->
            <!-- 클릭 시 setActiveCategory 메소드 호출 -->
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
            <!-- 특이사항 카테고리 버튼 -->
            <!-- activeCategory이 '특이사항'일 경우 active 클래스 추가 -->
            <!-- 클릭 시 setActiveCategory 메소드 호출 -->
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

        <!-- 공지사항 목록 테이블 -->
        <table class="board-table">
          <thead>
            <tr>
              <th>
                <div class="checkbox-action">
                  <!-- 삭제 버튼 -->
                  <button @click="deleteSelected" class="delete-button">
                    삭제
                  </button>
                  <!-- 전체 선택 체크박스 -->
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
            <!-- 공지사항 목록을 반복하여 표시 -->
            <tr
              v-for="(item, index) in filteredItems"
              :key="item.noticeId"
              @click="goToDetail(item.noticeId)"
              class="clickable-row"
            >
              <td class="checkbox-cell">
                <input
                  type="checkbox"
                  v-model="selectedItems"
                  :value="item.noticeId"
                  @click.stop
                />
              </td>
              <!-- 공지사항 번호 (페이지에 따라 계산) -->
              <td>{{ (currentPage - 1) * itemsPerPage + index + 1 }}</td>
              <!-- 공지사항 제목 -->
              <td>{{ item.title }}</td>
              <!-- 작성자 이름 (work.user.name) -->
              <td>{{ item.work.user.name }}</td>
              <!-- 보기 권한 -->
              <td>{{ item.viewers }}</td>
              <!-- 작성 날짜 형식 변경 후 표시 -->
              <td>{{ formatDate(item.createdAt) }}</td>
              <!-- 상태 레이블 표시 -->
              <td>{{ getStatusLabel(item.status) }}</td>
            </tr>
            <!-- 공지사항이 없을 경우 표시 -->
            <tr v-if="filteredItems.length === 0">
              <td colspan="7">게시물이 없습니다.</td>
            </tr>
          </tbody>
        </table>

        <!-- 페이징 섹션 -->
        <div class="pagination">
          <!-- 이전 페이지 버튼 -->
          <button @click="prevPage" :disabled="currentPage === 1">&lt;</button>
          <!-- 페이지 번호 버튼 -->
          <span
            v-for="page in totalPages"
            :key="page"
            :class="{ active: currentPage === page }"
            @click="goToPage(page)"
          >
            {{ page }}
          </span>
          <!-- 다음 페이지 버튼 -->
          <button @click="nextPage" :disabled="currentPage === totalPages">
            &gt;
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// Vue.js Composition API 사용
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import axios from "axios"; // Axios를 사용하여 API 호출

const goToDetail = (noticeId) => {
  console.log("Navigating to notice detail with ID:", noticeId); // 디버깅 로그
  router.push({ name: "noticedetail", params: { id: noticeId } });
};

const router = useRouter();

// 활성화된 탭 상태 ('all' 또는 'my')
const activeTab = ref("all");

// 활성화된 카테고리 상태 ('공지', '매뉴얼', '특이사항')
const activeCategory = ref("공지");

// 공지사항 목록을 저장하는 배열
const items = ref([]);

// 현재 페이지 번호 (초기값 1)
const currentPage = ref(1);

// 한 페이지에 표시할 공지사항 개수
const itemsPerPage = ref(5);

// 선택된 공지사항 ID 목록 (삭제 시 사용)
const selectedItems = ref([]);

/**
 * 공지사항을 백엔드에서 가져오는 함수입니다.
 * activeTab과 activeCategory에 따라 다른 엔드포인트를 호출합니다.
 */
const fetchNotices = async () => {
  try {
    let response;
    if (activeTab.value === "all") {
      // '전체 게시물' 탭인 경우
      if (activeCategory.value === "전체") {
        // '전체' 카테고리 선택 시 모든 공지사항 조회
        response = await axios.get("http://localhost:8707/notice/list");
      } else {
        // 특정 카테고리 선택 시 해당 타입의 공지사항 조회
        // 카테고리 이름을 타입 값으로 매핑
        const typeMap = {
          공지: "NOTICE",
          매뉴얼: "MANUAL",
          특이사항: "SPECIAL",
        };
        response = await axios.get("http://localhost:8707/notice/list/type", {
          params: { type: typeMap[activeCategory.value] },
        });
      }
    } else if (activeTab.value === "my") {
      // '내 게시물' 탭인 경우
      // 현재 예제에서는 모든 공지사항을 가져오지만,
      // 실제로는 로그인된 사용자의 work_id를 사용하여 필터링해야 합니다.
      const currentUserWorkId = 1; // 예시: 현재 사용자의 work_id (실제 로그인 로직에 따라 변경)
      response = await axios.get("http://localhost:8707/notice/list");
      // 모든 공지사항 중에서 작성자가 현재 사용자와 일치하는 것만 필터링
      items.value = response.data.filter(
        (notice) => notice.work.workId === currentUserWorkId
      );
      // 페이지를 1로 초기화
      currentPage.value = 1;
      return; // 이후 items.value는 이미 설정되었으므로 함수 종료
    }
    // 받아온 데이터를 items 배열에 저장
    items.value = response.data;
    // 페이지를 1로 초기화
    currentPage.value = 1;
  } catch (error) {
    console.error("공지사항을 가져오는 중 오류 발생:", error);
  }
};

/**
 * 페이징 처리를 위한 계산된 속성
 * 현재 페이지에 표시할 공지사항 목록을 반환합니다.
 */
const filteredItems = computed(() => {
  return items.value.slice(
    (currentPage.value - 1) * itemsPerPage.value,
    currentPage.value * itemsPerPage.value
  );
});

/**
 * 전체 페이지 수를 계산하는 계산된 속성
 */
const totalPages = computed(() => {
  return Math.ceil(items.value.length / itemsPerPage.value);
});

/**
 * 활성화된 탭을 설정하고 공지사항을 다시 가져오는 함수
 * @param tab 선택한 탭 값 ('all' 또는 'my')
 */
const setActiveTab = (tab) => {
  activeTab.value = tab;
  fetchNotices();
};

/**
 * 활성화된 카테고리를 설정하고 공지사항을 다시 가져오는 함수
 * @param category 선택한 카테고리 값 ('공지', '매뉴얼', '특이사항')
 */
const setActiveCategory = (category) => {
  activeCategory.value = category;
  fetchNotices();
};

/**
 * 이전 페이지로 이동하는 함수
 */
const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
};

/**
 * 다음 페이지로 이동하는 함수
 */
const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
};

/**
 * 특정 페이지로 이동하는 함수
 * @param page 이동할 페이지 번호
 */
const goToPage = (page) => {
  currentPage.value = page;
};

/**
 * 모든 체크박스를 토글하는 함수
 * @param event 체크박스 이벤트
 */
const toggleAll = (event) => {
  selectedItems.value = event.target.checked
    ? items.value.map((item) => item.noticeId) // 모든 공지사항 ID를 선택
    : []; // 선택 해제
};

/**
 * 선택된 공지사항을 삭제하는 함수
 */
const deleteSelected = async () => {
  if (selectedItems.value.length === 0) {
    alert("삭제할 항목을 선택해주세요.");
    return;
  }
  if (confirm("선택한 공지사항을 삭제하시겠습니까?")) {
    try {
      await axios.delete("http://localhost:8707/notice/delete", {
        data: selectedItems.value, // 삭제할 공지사항 ID 목록을 전송
      });
      alert("선택한 공지사항이 삭제되었습니다.");
      // 공지사항 목록을 다시 가져옵니다.
      fetchNotices();
    } catch (error) {
      console.error("공지사항 삭제 중 오류 발생:", error);
      alert("공지사항 삭제 중 오류가 발생했습니다.");
    }
  }
};

/**
 * 공지사항 작성 페이지로 이동하는 함수
 */
const createNotice = () => {
  router.push({ path: "/noticecreate" });
};

/**
 * 컴포넌트가 마운트될 때 공지사항을 가져옵니다.
 */
onMounted(() => {
  fetchNotices();
});

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
 * 상태 코드를 한글 레이블로 변환하는 함수
 * @param status 상태 코드 (VISIBLE, DRAFT, WITHDRAWN)
 * @return 상태 레이블
 */
const getStatusLabel = (status) => {
  const statusMap = {
    VISIBLE: "전체보기",
    DRAFT: "임시저장",
    WITHDRAWN: "회수",
  };
  return statusMap[status] || status;
};
</script>

<style>
/* 전체 레이아웃 스타일 */
.main-wrapper {
  display: flex;
}

.content-wrapper {
  flex-grow: 1;
  padding: 20px;
}

.notice-main {
  max-width: 1400px; /* 최대 너비 설정 */
  margin: 0 auto; /* 중앙 정렬 */
  padding: 20px;
}

/* 탭 스타일 */
.tabs {
  display: flex;
  margin-bottom: 10px;
}

.tab {
  margin-right: 10px;
  padding: 10px 20px;
  border: 1px solid #ddd;
  background-color: #f9f9f9;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.tab:hover {
  background-color: #e0e0e0;
}

.tab.active {
  background-color: #007bff;
  color: white;
}

/* 카테고리 및 공지 작성 버튼 스타일 */
.categories-container {
  display: flex;
  justify-content: space-between; /* 왼쪽: 카테고리, 오른쪽: 공지 작성 버튼 */
  align-items: center; /* 수직 중앙 정렬 */
  margin-bottom: 20px;
}

.categories {
  display: flex;
  gap: 10px; /* 버튼 간 간격 */
}

.category {
  display: flex;
  align-items: center; /* 이미지와 텍스트 수직 정렬 */
  gap: 8px; /* 이미지와 텍스트 간 간격 */
  padding: 10px 20px;
  font-size: 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #f9f9f9;
  cursor: pointer;
  width: 260px; /* 고정 너비 */
  height: 50px; /* 고정 높이 */
  transition: background-color 0.2s;
}

.category:hover {
  background-color: #e0e0e0;
}

.category.active {
  background-color: #007bff;
  color: white;
}

.category-icon {
  width: 20px; /* 아이콘 너비 */
  height: 20px; /* 아이콘 높이 */
  object-fit: contain; /* 이미지가 잘리지 않도록 설정 */
}

/* 공지 작성 버튼 스타일 */
.create-notice-button {
  padding: 10px 20px;
  font-size: 16px;
  border: none; /* 테두리 제거 */
  border-radius: 4px;
  background-color: #007bff;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s;
}

.create-notice-button:hover {
  background-color: #0056b3;
}

/* 테이블 스타일 */
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

/* 체크박스 및 삭제 버튼 정렬 */
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

.delete-button {
  background-color: #f5f5f5;
  border: 1px solid #ddd;
  padding: 5px 8px;
  font-size: 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.delete-button:hover {
  background-color: #007bff;
  color: white;
}
.clickable-row {
  cursor: pointer;
}

.clickable-row:hover {
  background-color: #f0f8ff; /* 하이라이트 색상 */
}

/* 페이징 스타일 */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 10px;
}

.pagination button {
  padding: 5px 10px;
  margin: 0 5px;
  border: 1px solid #ddd;
  background-color: #f9f9f9;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.pagination button:hover:not(:disabled) {
  background-color: #e0e0e0;
}

.pagination button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.pagination span {
  margin: 0 5px;
  cursor: pointer;
  padding: 5px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.pagination span:hover {
  background-color: #e0e0e0;
}

.pagination span.active {
  background-color: #007bff;
  color: white;
  font-weight: bold;
}
</style>
