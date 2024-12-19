// src/stores/contracts.js

// Pinia에서 defineStore 함수를 가져옵니다. Pinia는 Vue.js 애플리케이션에서 상태 관리를 도와주는 도구입니다.
import { defineStore } from "pinia";

// Axios 라이브러리를 가져옵니다. Axios는 서버와 통신할 때 사용되는 도구입니다.
import axios from "axios";

// "contracts"라는 이름의 Pinia 스토어를 정의합니다.
export const useContractsStore = defineStore("contracts", {
  // state는 스토어의 상태를 정의하는 부분입니다.
  state: () => ({
    contracts: [],    // 계약 목록을 저장하는 배열입니다.
    loading: false,   // 데이터 로딩 상태를 나타내는 불리언 값입니다.
    error: null,      // 오류 메시지를 저장하는 변수입니다.
  }),
  
  // actions는 상태를 변경하거나 비동기 작업을 수행하는 함수들을 모아놓은 부분입니다.
  actions: {
    // fetchContracts는 서버에서 계약 목록을 가져오는 함수입니다.
    async fetchContracts() {
      this.loading = true;   // 데이터를 불러오는 중임을 나타냅니다.
      this.error = null;     // 이전 오류 메시지를 초기화합니다.
      try {
        // 서버의 기본 URL을 가져옵니다. 환경 변수에서 설정된 값을 사용합니다.
        const baseUrl = import.meta.env.VITE_API_URL;
        
        // 서버에 GET 요청을 보내서 계약 목록을 가져옵니다.
        const response = await axios.get(`${baseUrl}/api/contracts`);
        
        // 모든 계약에 대해 스케줄 정보를 추가로 가져옵니다.
        const contractsWithSchedules = await Promise.all(
          response.data.map(async (contract) => {
            try {
              // 각 계약의 스케줄 정보를 가져오는 요청을 보냅니다.
              const schedulesResponse = await axios.get(`${baseUrl}/api/contracts/${contract.contractId}/schedules`);
              return {
                ...contract,                    // 기존 계약 정보를 그대로 가져옵니다.
                schedules: schedulesResponse.data, // 가져온 스케줄 정보를 추가합니다.
                expanded: false,                // 계약이 펼쳐졌는지 여부를 나타냅니다.
              };
            } catch (error) {
              // 스케줄 정보를 가져오지 못했을 때의 처리입니다.
              console.error(`스케줄 정보를 가져오는 데 실패했습니다: 계약 ID ${contract.contractId}`, error);
              return {
                ...contract,    // 기존 계약 정보를 그대로 가져옵니다.
                schedules: [],  // 스케줄 정보는 빈 배열로 설정합니다.
                expanded: false,// 계약이 펼쳐졌는지 여부를 나타냅니다.
              };
            }
          })
        );
        
        // 계약 목록에 스케줄 정보를 추가한 데이터를 저장합니다.
        this.contracts = contractsWithSchedules;
      } catch (err) {
        // 계약 목록을 가져오는 데 실패했을 때의 처리입니다.
        this.error = "계약 목록을 불러오는 데 실패했습니다.";
        console.error(err);
      } finally {
        this.loading = false; // 데이터 로딩이 끝났음을 나타냅니다.
      }
    },

    // addContract는 새로운 계약을 추가하는 함수입니다.
    async addContract(newContract) {
      this.loading = true;   // 계약을 추가하는 중임을 나타냅니다.
      this.error = null;     // 이전 오류 메시지를 초기화합니다.
      try {
        const baseUrl = import.meta.env.VITE_API_URL;
        
        // 서버에 POST 요청을 보내서 새로운 계약을 추가합니다.
        const response = await axios.post(`${baseUrl}/api/contracts`, newContract);
        const addedContract = response.data; // 추가된 계약 정보를 가져옵니다.

        // 새로운 계약에 스케줄 목록을 빈 배열로 설정하고, 'expanded' 상태를 false로 설정합니다.
        addedContract.schedules = [];
        addedContract.expanded = false;

        // 계약 목록에 새로운 계약을 추가합니다.
        this.contracts.push(addedContract);
      } catch (err) {
        // 계약 추가에 실패했을 때의 처리입니다.
        this.error = "계약 추가에 실패했습니다.";
        console.error(err);
      } finally {
        this.loading = false; // 계약 추가 작업이 끝났음을 나타냅니다.
      }
    },

    // deleteContract는 특정 계약을 삭제하는 함수입니다.
    async deleteContract(contractId) {
      this.loading = true;   // 계약을 삭제하는 중임을 나타냅니다.
      this.error = null;     // 이전 오류 메시지를 초기화합니다.
      try {
        const baseUrl = import.meta.env.VITE_API_URL;
        
        // 서버에 DELETE 요청을 보내서 계약을 삭제합니다.
        await axios.delete(`${baseUrl}/api/contracts/${contractId}`);

        // 계약 목록에서 해당 계약의 인덱스를 찾습니다.
        const contractIndex = this.contracts.findIndex(contract => contract.contractId === contractId);
        if (contractIndex !== -1) {
          // 계약을 목록에서 제거합니다.
          this.contracts.splice(contractIndex, 1);
        }
      } catch (err) {
        // 계약 삭제에 실패했을 때의 처리입니다.
        this.error = "계약 삭제에 실패했습니다.";
        console.error(err);
      } finally {
        this.loading = false; // 계약 삭제 작업이 끝났음을 나타냅니다.
      }
    },

    // updateContract는 특정 계약을 업데이트하는 함수입니다.
    async updateContract(contractId, updatedData) {
      this.loading = true;   // 데이터를 업데이트하는 중임을 나타냅니다.
      this.error = null;     // 이전 오류 메시지를 초기화합니다.
      try {
        const baseUrl = import.meta.env.VITE_API_URL;
        
        // 서버에 PUT 요청을 보내서 계약 정보를 업데이트합니다.
        const response = await axios.put(`${baseUrl}/api/contracts/${contractId}`, updatedData);
        const updatedContract = response.data; // 업데이트된 계약 정보를 가져옵니다.

        // 업데이트된 계약에 스케줄 정보가 없는 경우, 다시 스케줄 정보를 가져옵니다.
        if (!updatedContract.schedules) {
          try {
            const schedulesResponse = await axios.get(`${baseUrl}/api/contracts/${contractId}/schedules`);
            updatedContract.schedules = schedulesResponse.data;
          } catch (error) {
            // 스케줄 정보를 가져오지 못했을 때의 처리입니다.
            console.error(`스케줄 정보를 가져오는 데 실패했습니다: 계약 ID ${contractId}`, error);
            updatedContract.schedules = [];
          }
        }

        // 계약 목록에서 해당 계약의 인덱스를 찾습니다.
        const index = this.contracts.findIndex((contract) => contract.contractId === contractId);
        if (index !== -1) {
          // 계약 정보를 업데이트합니다. 기존의 'expanded' 상태는 유지합니다.
          this.contracts[index] = {
            ...updatedContract,
            schedules: updatedContract.schedules || [],
            expanded: this.contracts[index].expanded,
          };
        }
      } catch (err) {
        // 계약 업데이트에 실패했을 때의 처리입니다.
        this.error = "계약 업데이트에 실패했습니다.";
        console.error('Failed to update contract:', err);
      } finally {
        this.loading = false; // 데이터 업데이트가 끝났음을 나타냅니다.
      }
    },

    // addSchedule은 새로운 스케줄을 추가하는 함수입니다.
    async addSchedule(contractId, newSchedule) {
      this.loading = true;   // 스케줄을 추가하는 중임을 나타냅니다.
      this.error = null;     // 이전 오류 메시지를 초기화합니다.
      try {
        const baseUrl = import.meta.env.VITE_API_URL;
        
        // 서버에 POST 요청을 보내서 새로운 스케줄을 추가합니다.
        const response = await axios.post(`${baseUrl}/api/schedules`, newSchedule);
        const addedSchedule = response.data; // 추가된 스케줄 정보를 가져옵니다.

        // 계약 목록에서 해당 계약을 찾습니다.
        const contractIndex = this.contracts.findIndex(contract => contract.contractId === contractId);
        if (contractIndex !== -1) {
          // 해당 계약의 스케줄 목록에 새로운 스케줄을 추가합니다.
          this.contracts[contractIndex].schedules.push(addedSchedule);
        }
      } catch (err) {
        // 스케줄 추가에 실패했을 때의 처리입니다.
        this.error = "스케줄 추가에 실패했습니다.";
        console.error(err);
      } finally {
        this.loading = false; // 스케줄 추가 작업이 끝났음을 나타냅니다.
      }
    },

    // editSchedule은 기존의 스케줄을 수정하는 함수입니다.
    async editSchedule(contractId, scheduleId, updatedSchedule) {
      // 디버깅을 위해 수정된 스케줄 데이터를 콘솔에 출력합니다.
      console.log("pinia editSchedule updatedSchedule:", JSON.stringify(updatedSchedule, null, 2));
      
      this.loading = true;   // 스케줄을 수정하는 중임을 나타냅니다.
      this.error = null;     // 이전 오류 메시지를 초기화합니다.
      try {
        const baseUrl = import.meta.env.VITE_API_URL;
        
        // 서버에 PUT 요청을 보내서 스케줄 정보를 업데이트합니다.
        const response = await axios.put(`${baseUrl}/api/schedules/${scheduleId}`, updatedSchedule);
        const updatedScheduleData = response.data; // 업데이트된 스케줄 정보를 가져옵니다.

        // 디버깅을 위해 업데이트된 스케줄 데이터를 콘솔에 출력합니다.
        console.log("updatedScheduleData:", JSON.stringify(updatedScheduleData, null, 2));

        // 계약 목록에서 해당 계약을 찾습니다.
        const contractIndex = this.contracts.findIndex(contract => contract.contractId === contractId);
        if (contractIndex !== -1) {
          // 해당 계약의 스케줄 목록에서 수정할 스케줄을 찾습니다.
          const scheduleIndex = this.contracts[contractIndex].schedules.findIndex(schedule => schedule.scheduleId === scheduleId);
          if (scheduleIndex !== -1) {
            // 스케줄 정보를 업데이트합니다.
            this.contracts[contractIndex].schedules[scheduleIndex] = updatedScheduleData;
          }
        }
      } catch (err) {
        // 스케줄 수정에 실패했을 때의 처리입니다.
        this.error = "스케줄 수정에 실패했습니다.";
        console.error('Failed to edit schedule:', err);
      } finally {
        this.loading = false; // 스케줄 수정 작업이 끝났음을 나타냅니다.
      }
    },

    // deleteSchedule은 기존의 스케줄을 삭제하는 함수입니다.
    async deleteSchedule(contractId, scheduleId) {
      this.loading = true;   // 스케줄을 삭제하는 중임을 나타냅니다.
      this.error = null;     // 이전 오류 메시지를 초기화합니다.
      try {
        const baseUrl = import.meta.env.VITE_API_URL;
        
        // 서버에 DELETE 요청을 보내서 스케줄을 삭제합니다.
        await axios.delete(`${baseUrl}/api/schedules/${scheduleId}`);

        // 계약 목록에서 해당 계약을 찾습니다.
        const contractIndex = this.contracts.findIndex(contract => contract.contractId === contractId);
        if (contractIndex !== -1) {
          // 해당 계약의 스케줄 목록에서 삭제할 스케줄을 찾습니다.
          const scheduleIndex = this.contracts[contractIndex].schedules.findIndex(schedule => schedule.scheduleId === scheduleId);
          if (scheduleIndex !== -1) {
            // 스케줄을 목록에서 제거합니다.
            this.contracts[contractIndex].schedules.splice(scheduleIndex, 1);
          }
        }
      } catch (err) {
        // 스케줄 삭제에 실패했을 때의 처리입니다.
        this.error = "스케줄 삭제에 실패했습니다.";
        console.error(err);
      } finally {
        this.loading = false; // 스케줄 삭제 작업이 끝났음을 나타냅니다.
      }
    },

    
  },
});
