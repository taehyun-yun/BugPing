<template>
    <div v-if="isOpen" class="modal-overlay" @click="closeModal">
        <div class="modal-content" @click.stop>
            <div class="modal-header">
                <h2 class="title">
                    계약 정보 수정
                    <button class="help-button">?</button>
                </h2>
            </div>

            <div class="modal-body">
                <section class="members-section">
                    <h3>편집 대상 구성원</h3>
                    <div class="member-item">
                        <div class="profile-image">
                            <img src="@/assets/AdminContractImg/placeholder.png" alt="프로필 이미지" />
                        </div>
                        <span class="member-name">{{ contract.work.user.name }}</span>
                    </div>
                </section>

                <section class="contract-details">
                    <div class="form-group">
                        <label for="hourlyWage">시급</label>
                        <input id="hourlyWage" v-model="editedContract.hourlyWage" type="number" min="0" step="100">
                    </div>
                    <div class="form-group">
                        <label for="contractStart">계약 시작일</label>
                        <input id="contractStart" v-model="editedContract.contractStart" type="date">
                    </div>
                    <div class="form-group">
                        <label for="contractEnd">계약 종료일</label>
                        <input id="contractEnd" v-model="editedContract.contractEnd" type="date">
                    </div>
                </section>

                <button class="add-button">
                    <span class="plus-icon">+</span>
                    추가
                </button>

                <section class="schedule-section">
                    <div class="schedule-header">
                        <span class="day">화</span>
                        <span class="divider">|</span>
                        <span class="type">근무</span>
                    </div>

                    <div class="schedule-details">
                        <div class="time-slot">
                            <span class="time-icon">🕐</span>
                            01:00 - 02:00
                        </div>
                        <div class="break-time">
                            <span class="break-icon">☕</span>
                            00:05
                        </div>
                        <div class="location">
                            <span class="location-icon">📍</span>
                            준호하우스
                        </div>
                        <div class="note">
                            <span class="note-icon">📝</span>
                            노트
                        </div>
                    </div>
                </section>

                <section class="weekdays-section">
                    <div class="weekdays-header">
                        월, 수, 목, 금, 토, 일
                        <span class="status">일정 없음</span>
                    </div>
                </section>
            </div>

            <div class="modal-footer">
                <button class="cancel-button" @click="closeModal">취소</button>
                <button class="save-button" @click="saveContract">저장</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch } from 'vue'

const props = defineProps({
    isOpen: {
        type: Boolean,
        default: false
    },
    contract: {
        type: Object,
        required: true
    }
})

const emit = defineEmits(['close', 'save'])

const editedContract = ref({
    hourlyWage: 0,
    contractStart: '',
    contractEnd: ''
})

watch(() => props.contract, (newContract) => {
    editedContract.value = {
        hourlyWage: newContract.hourlyWage,
        contractStart: newContract.contractStart,
        contractEnd: newContract.contractEnd
    }
}, { immediate: true })

const closeModal = () => {
    emit('close')
}

const saveContract = () => {
    emit('save', {
        ...props.contract,
        ...editedContract.value
    })
    closeModal()
}
</script>

<style scoped>
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
}

.modal-content {
    background: white;
    border-radius: 16px;
    width: 90%;
    max-width: 500px;
    padding: 24px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.modal-header {
    margin-bottom: 24px;
}

.title {
    font-size: 20px;
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 8px;
}

.help-button {
    width: 24px;
    height: 24px;
    border-radius: 50%;
    border: 1px solid #e2e8f0;
    background: none;
    color: #718096;
    cursor: pointer;
}

.members-section {
    margin-bottom: 20px;
}

.members-section h3 {
    font-size: 16px;
    color: #4a5568;
    margin-bottom: 12px;
}

.member-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px;
    background: #f7fafc;
    border-radius: 8px;
}

.profile-image {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    overflow: hidden;
    background: #e2e8f0;
}

.profile-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.member-name {
    font-size: 16px;
    color: #2d3748;
}

.contract-details {
    background: #f7fafc;
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 20px;
}

.form-group {
    margin-bottom: 16px;
}

.form-group:last-child {
    margin-bottom: 0;
}

.form-group label {
    display: block;
    margin-bottom: 8px;
    font-weight: 500;
    color: #4a5568;
}

.form-group input {
    width: 100%;
    padding: 8px 12px;
    border: 1px solid #e2e8f0;
    border-radius: 4px;
    font-size: 14px;
    background-color: white;
}

.form-group input:focus {
    outline: none;
    border-color: #3182ce;
    box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.5);
}

.add-button {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 16px;
    border: 2px solid #e2e8f0;
    border-radius: 8px;
    background: none;
    color: #4a5568;
    cursor: pointer;
    margin-bottom: 20px;
    width: 100%;
    justify-content: center;
    transition: all 0.2s;
}

.add-button:hover {
    background: #f7fafc;
}

.plus-icon {
    color: #3182ce;
    font-size: 18px;
}

.schedule-section {
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 20px;
}

.schedule-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 16px;
    color: #4a5568;
}

.schedule-details {
    display: grid;
    gap: 12px;
    color: #718096;
}

.weekdays-section {
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 24px;
}

.weekdays-header {
    color: #4a5568;
    display: flex;
    justify-content: space-between;
}

.status {
    color: #718096;
}

.modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
}

.cancel-button,
.save-button {
    padding: 8px 24px;
    border-radius: 8px;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s;
}

.cancel-button {
    border: 1px solid #e2e8f0;
    background: #f7fafc;
    color: #4a5568;
}

.save-button {
    border: none;
    background: #3182ce;
    color: white;
}

.cancel-button:hover {
    background: #edf2f7;
}

.save-button:hover {
    background: #2c5282;
}
</style>