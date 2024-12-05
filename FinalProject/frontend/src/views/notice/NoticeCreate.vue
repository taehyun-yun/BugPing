<template>
    <div class="page-container">
      <!-- 사이드바와 헤더 컴포넌트 -->
      <MainSidebar />
      <div class="main-content">
        <MainHeader />
        
        <!-- 공지사항 작성 페이지 UI -->
        <div class="notice-page">
          <div class="category-main">
            <h1>카테고리</h1>
          </div>  
          
          <!-- 카테고리 선택 -->
          <div class="category-section">
            <div v-for="category in categories" :key="category.id" class="category-card">
              <img v-if="category.icon" :src="category.icon" alt="카테고리 아이콘" class="category-icon" />
              <div class="category-text">{{ category.name }}</div>
            </div>
          </div>
  
          <!-- 제목 입력 -->
          <div class="input-section">
            <label class="input-label" for="title">타이틀<span class="required">*</span></label>
            <input type="text" id="title" v-model="title" placeholder="입력하세요" class="input-field" />
          </div>
  
          <!-- 내용 입력 -->
          <div class="input-section">
            <label class="input-label" for="content">내용<span class="required">*</span></label>
            <textarea id="content" v-model="content" placeholder="입력하세요" class="textarea-field"></textarea>
          </div>
  
          <!-- 이미지 및 파일 첨부 -->
          <div class="attachment-section">
            <div v-for="attachment in attachments" :key="attachment.id" class="attachment-card">
              <img v-if="attachment.icon" :src="attachment.icon" alt="첨부 아이콘" class="attachment-icon" />
              <div class="attachment-text">{{ attachment.label }}</div>
              <div v-if="attachment.info" class="attachment-info">{{ attachment.info }}</div>
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
  import MainSidebar from '@/components/MainSidebar.vue';
  import MainHeader from '@/components/MainHeader.vue';
  import { ref } from 'vue';
  
  // 카테고리와 첨부 파일 데이터
  const categories = ref([
    { id: 1, name: '공지', icon: '' },
    { id: 2, name: '매뉴얼', icon: '' },
    { id: 3, name: '특이사항', icon: '' },
  ]);
  
  const attachments = ref([
    { id: 1, label: '이미지 첨부', icon: '' },
    { id: 2, label: '파일 첨부', icon: '', info: '(문서 파일만 업로드 가능, 파일당 최대 50MB)' },
  ]);
  
  // 필수 입력 항목에 대한 ref 상태 관리
  const title = ref('');
  const content = ref('');
  
  // 작성 취소 버튼 클릭 시 동작
  const cancelNotice = () => {
    // 모든 입력 필드를 초기화
    title.value = '';
    content.value = '';
    console.log('작성 취소되었습니다.');
  };
  
  // 작성 완료 버튼 클릭 시 동작
  const submitNotice = () => {
    // 입력된 데이터로 작성 완료 처리 (예: 백엔드로 데이터 전송)
    console.log('작성된 제목:', title.value);
    console.log('작성된 내용:', content.value);
    alert('공지사항이 작성되었습니다.');
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
    max-width: 1200px;
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
    background: #f5f5f5;
    padding: 10px 20px;
    border-radius: 10px;
    cursor: pointer;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
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
  
  .input-section {
    margin-bottom: 20px;
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
    height: 20px;
    padding: 10px;
    border-radius: 5px;
    border: 1px solid #c3c3c3;
  }
  
  .textarea-field {
    width: 100%;
    height: 450px;
    padding: 10px;
    border-radius: 5px;
    border: 1px solid #c3c3c3;
  }
  
  .attachment-section {
    display: flex;
    gap: 20px;
    margin-bottom: 30px;
  }
  
  .attachment-card {
    background: #f5f5f5;
    padding: 20px;
    border-radius: 10px;
    text-align: center;
    position: relative;
    width: 200px;
  }
  
  .attachment-icon {
    width: 30px;
    height: 30px;
  }
  
  .attachment-text {
    margin-top: 10px;
    font-size: 16px;
    color: #000;
  }
  
  .attachment-info {
    font-size: 10px;
    color: #656565;
    margin-top: 5px;
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
    padding: 20px 10px; /* 위아래 패딩을 20px로 설정하여 적당한 간격 확보 */
  }
  
  .category-main h1 {
    font-size: 28px; /* 텍스트 크기 키우기 */
    font-weight: bold; /* 볼드 처리 */
    margin: 20px 0; /* 위아래 20px의 마진으로 적당한 간격 설정 */
  }
  </style>
  