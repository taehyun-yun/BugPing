<template>
  <div class="main-wrapper">
    <div class="content-wrapper">
      <div class="notice-main">
        <!-- 탭 섹션 -->
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

        <!-- 카테고리 및 공지 작성 버튼 섹션 -->
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
          <button
            v-if="forEmployer"
            class="create-notice-button"
            @click="createNotice"
          >
            공지 작성하기
          </button>
        </div>

        <!-- 공지사항 목록 테이블 -->
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
              <td>{{ (currentPage - 1) * itemsPerPage + index + 1 }}</td>
              <td>{{ item.title }}</td>
              <td>{{ item.work?.userName || "작성자 없음" }}</td>
              <td>{{ item.viewers }}</td>
              <td>{{ formatDate(item.createdAt) }}</td>
              <td>{{ getStatusLabel(item.status) }}</td>
            </tr>
            <tr v-if="filteredItems.length === 0">
              <td colspan="7">게시물이 없습니다.</td>
            </tr>
          </tbody>
        </table>

        <!-- 페이징 섹션 -->
        <div class="pagination">
          <button @click="prevPage" :disabled="currentPage === 1">&lt;</button>
          <span
            v-for="page in totalPages"
            :key="page"
            :class="{ active: currentPage === page }"
            @click="goToPage(page)"
          >
            {{ page }}
          </span>
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
import { ref, computed, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import axios from "axios"; // Axios를 사용하여 API 호출
import { useUserStore } from "@/stores/userStore";
import { axiosAddress } from "@/stores/axiosAddress";

const router = useRouter();
const userStore = useUserStore(); // Pinia 스토어 호출

console.log("userStore 객체:", userStore); // userStore 전체 구조 확인
console.log("현재 workId:", userStore.workId); // workId를 명시적으로 확인
console.log("현재 userId:", userStore.userId); // userId가 있는지 확인
console.log("현재 컴퍼니 아이디 ", userStore.company.companyId);

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
 * activeTab과 activeCategory, 그리고 companyId에 따라 다른 엔드포인트를 호출합니다.
 */
const fetchNotices = async () => {
  try {
    const companyId = userStore.company.companyId; // 회사 ID 가져오기
    if (!companyId) {
      alert("회사 정보가 없습니다. 다시 로그인 해주세요.");
      return;
    }

    let response;
    if (activeTab.value === "all") {
      // '전체 게시물' 탭인 경우
      if (activeCategory.value === "전체") {
        // '전체' 카테고리 선택 시 모든 공지사항 조회
        response = await axios.get(`${axiosAddress}/notice/list`, {
          params: { type: activeCategory.value, companyId },
          withCredentials: true,
        });
      } else {
        // 특정 카테고리 선택 시 해당 타입의 공지사항 조회
        const typeMap = {
          공지: "NOTICE",
          매뉴얼: "MANUAL",
          특이사항: "SPECIAL",
        };
        response = await axios.get(`${axiosAddress}/notice/list/type`, {
          params: {
            type: typeMap[activeCategory.value],
            companyId,
          },
          withCredentials: true,
        });
      }
    } else if (activeTab.value === "my") {
      // '내 게시물' 탭인 경우
      const currentUserWorkId = userStore.userId; // 현재 사용자 ID 가져오기

      response = await axios.get(`${axiosAddress}/notice/list`, {
        params: { companyId },
        withCredentials: true,
      });

      // 작성자 필터링
      items.value = response.data.filter(
        (notice) => notice.work.userId === currentUserWorkId
      );
      currentPage.value = 1; // 페이지 초기화
      return;
    }

    // 받아온 데이터를 저장
    items.value = response.data;
    currentPage.value = 1; // 페이지 초기화
  } catch (error) {
    alert(error);
    console.error("공지사항을 가져오는 중 오류 발생:", error);
    alert("공지사항을 가져오는 중 오류가 발생했습니다.");
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
      await axios.delete(`${axiosAddress}/notice/delete`, {
        data: selectedItems.value, // 삭제할 공지사항 ID 목록을 전송
        params: { companyId: userStore.company.companyId }, // companyId 추가
        withCredentials: true,
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
const createNotice = async () => {
  const res = await axios.get(
    `${axiosAddress}/notice/getWorkIdToGoCreateNotice?companyId=${userStore.company.companyId}`,
    { withCredentials: true }
  );
  console.log("workId : " + res.data.workId);
  //alert(res.data.workId);

  const workId = res.data.workId;
  // 여기서 workId 찍히는거 확인.
  console.log("workId : " + res.data.workId);

  if (!workId) {
    alert("회사 정보가 없습니다. 다시 로그인해주세요.");
    return;
  }
  router.push({ name: "noticeCreate", params: { workId } });
};
// const createNotice = () => {
//   const companyId = userStore.company.companyId; // 실제 workId를 가져오는 로직
//   if (!companyId) {
//     alert("회사 정보가 없습니다. 다시 로그인해주세요.");
//     return;
//   }
//   router.push({ name: "noticeCreate", params: { companyId } });
// };

// 공지 작성 권한 확인
const forEmployer = userStore.roles.includes("employer");

// 컴포넌트가 마운트될 때 공지사항을 가져옵니다.
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

/**
 * 공지사항 상세 페이지로 이동하는 함수
 * @param noticeId 이동할 공지사항의 ID
 */
const goToDetail = (noticeId) => {
  console.log("Navigating to notice detail with ID:", noticeId); // 디버깅 로그
  router.push({ name: "noticedetail", params: { id: noticeId } });
};

/**
 * `userStore.company.companyId`가 변경될 때마다 공지사항을 다시 가져옵니다.
 */
watch(
  () => userStore.company.companyId,
  (newCompanyId, oldCompanyId) => {
    if (newCompanyId !== oldCompanyId) {
      fetchNotices();
    }
  }
);
</script>

<style scoped>
/* 전체 레이아웃 스타일 */
.main-wrapper {
  display: flex;
}

.content-wrapper {
  flex-grow: 1;
  padding: 0 100px;
}

.notice-main {
  max-width: 1200px; /* 최대 너비 설정 */
  width: 100%; /* 전체 너비로 확장 */
  margin: 0 auto; /* 중앙 정렬 */
  padding: 20px;
  box-sizing: border-box; /* padding 포함한 전체 너비 */
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
  max-width: 1200px; /* 원하는 최대 너비 설정 */
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
  justify-content: center; /* 수평 가운데 정렬 */
  align-items: center; /* 이미지와 텍스트 수직 정렬 */
  gap: 10px; /* 이미지와 텍스트 간 간격 */
  padding: 10px 20px;
  font-size: 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #f9f9f9;
  cursor: pointer;
  width: 200px; /* 고정 너비 */
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
  table-layout: fixed; /* 열 너비를 고정 */
  border-collapse: collapse;
  margin-top: 20px;
  border: none;
}

.board-table th,
.board-table td {
  padding: 5px;
  text-align: center;
  border-bottom: 1px solid #e0e0e0;
  vertical-align: middle;
  height: 50px;
}

.board-table th:first-child,
.board-table td:first-child {
  width: 80px; /* 체크박스 열의 너비를 좁게 설정 */
}

.board-table th {
  background-color: #f9f9f9;
}

/* 체크박스 및 삭제 버튼 정렬 */
.checkbox-action {
  display: flex;
  align-items: center;
  gap: 5px;
}

.checkbox-cell {
  display: flex;
  justify-content: flex-end; /* 오른쪽 정렬 */
  align-items: center;
}

.input[type="checkbox"] {
  justify-content: flex-end; /* 오른쪽 정렬 */
  width: 16px;
  height: 16px;
  margin: 0;
  vertical-align: middle;
}

.checkbox-action input[type="checkbox"] {
  margin-left: auto; /* 체크박스를 오른쪽 끝으로 밀어줌 */
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
