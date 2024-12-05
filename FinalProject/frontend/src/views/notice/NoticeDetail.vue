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
        <span>참고 올리고...</span>
      </div>
      <div class="info">
        <div class="profile">
          <img
            src="@/assets/noticeimg/profile.png"
            alt="프로필 아이콘"
            class="profile-img"
          />
          <span>캐치티니핑</span>
        </div>
        <div class="date-info">
          <span>작성일 2024.11.28(목) 14:15</span>
          <div class="divider"></div>
          <span>수정일 2024.11.28(목) 14:15</span>
        </div>
      </div>
    </div>

    <!-- 내용 -->
    <div class="content">
      <p>내용이 들어갈 영역입니다...</p>
    </div>

    <!-- 버튼 -->
    <div class="actions">
      <button class="action-button">수정하기</button>
      <button class="action-button">삭제</button>
    </div>
  </div>
</template>

<script setup>
// 필요한 경우 스크립트를 추가하세요
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
  font-size: 18px;
  font-weight: bold;
  color: #333333;
  margin-bottom: 10px;
}

.title .icon {
  width: 20px;
  height: 20px;
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
  height: 300px; /* 내용 영역의 고정 높이 */
  border-radius: 8px;
  margin-bottom: 20px;
  padding: 20px;
  font-size: 14px;
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
