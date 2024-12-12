// src/stores/contracts.js
import { defineStore } from "pinia";
import axios from "axios";

export const useContractsStore = defineStore("contracts", {
  state: () => ({
    contracts: [],
    loading: false,
    error: null,
  }),
  actions: {
    async fetchContracts() {
      this.loading = true;
      this.error = null;
      try {
        const baseUrl = import.meta.env.VITE_API_URL;
        const response = await axios.get(`${baseUrl}/api/contracts`);
        const contractsWithSchedules = await Promise.all(
          response.data.map(async (contract) => {
            try {
              const schedulesResponse = await axios.get(`${baseUrl}/api/contracts/${contract.contractId}/schedules`);
              return {
                ...contract,
                schedules: schedulesResponse.data,
                expanded: false,
              };
            } catch (error) {
              console.error(`스케줄 정보를 가져오는 데 실패했습니다: 계약 ID ${contract.contractId}`, error);
              return {
                ...contract,
                schedules: [],
                expanded: false,
              };
            }
          })
        );
        this.contracts = contractsWithSchedules;
      } catch (err) {
        this.error = "계약 목록을 불러오는 데 실패했습니다.";
        console.error(err);
      } finally {
        this.loading = false;
      }
    },

    async updateContract(contractId, updatedData) {
      this.loading = true;
      this.error = null;
      try {
        const baseUrl = import.meta.env.VITE_API_URL;
        const response = await axios.put(`${baseUrl}/api/contracts/${contractId}`, updatedData);
        const updatedContract = response.data;

        if (!updatedContract.schedules) {
          try {
            const schedulesResponse = await axios.get(`${baseUrl}/api/contracts/${contractId}/schedules`);
            updatedContract.schedules = schedulesResponse.data;
          } catch (error) {
            console.error(`스케줄 정보를 가져오는 데 실패했습니다: 계약 ID ${contractId}`, error);
            updatedContract.schedules = [];
          }
        }

        const index = this.contracts.findIndex((contract) => contract.contractId === contractId);
        if (index !== -1) {
          this.contracts[index] = {
            ...updatedContract,
            schedules: updatedContract.schedules || [],
            expanded: this.contracts[index].expanded,
          };
        }
      } catch (err) {
        this.error = "계약 업데이트에 실패했습니다.";
        console.error(err);
      } finally {
        this.loading = false;
      }
    },

    async addSchedule(contractId, newSchedule) {
      this.loading = true;
      this.error = null;
      try {
        const baseUrl = import.meta.env.VITE_API_URL;
        const response = await axios.post(`${baseUrl}/api/contracts/${contractId}/schedules`, newSchedule);
        const addedSchedule = response.data;

        const contractIndex = this.contracts.findIndex(contract => contract.contractId === contractId);
        if (contractIndex !== -1) {
          this.contracts[contractIndex].schedules.push(addedSchedule);
        }
      } catch (err) {
        this.error = "스케줄 추가에 실패했습니다.";
        console.error(err);
      } finally {
        this.loading = false;
      }
    },

    async editSchedule(contractId, scheduleId, updatedSchedule) {
      this.loading = true;
      this.error = null;
      try {
        const baseUrl = import.meta.env.VITE_API_URL;
        const response = await axios.put(`${baseUrl}/api/contracts/${contractId}/schedules/${scheduleId}`, updatedSchedule);
        const updatedScheduleData = response.data;

        const contractIndex = this.contracts.findIndex(contract => contract.contractId === contractId);
        if (contractIndex !== -1) {
          const scheduleIndex = this.contracts[contractIndex].schedules.findIndex(schedule => schedule.id === scheduleId);
          if (scheduleIndex !== -1) {
            this.contracts[contractIndex].schedules[scheduleIndex] = updatedScheduleData;
          }
        }
      } catch (err) {
        this.error = "스케줄 수정에 실패했습니다.";
        console.error(err);
      } finally {
        this.loading = false;
      }
    },

    async deleteSchedule(contractId, scheduleId) {
      this.loading = true;
      this.error = null;
      try {
        const baseUrl = import.meta.env.VITE_API_URL;
        await axios.delete(`${baseUrl}/api/contracts/${contractId}/schedules/${scheduleId}`);

        const contractIndex = this.contracts.findIndex(contract => contract.contractId === contractId);
        if (contractIndex !== -1) {
          const scheduleIndex = this.contracts[contractIndex].schedules.findIndex(schedule => schedule.id === scheduleId);
          if (scheduleIndex !== -1) {
            this.contracts[contractIndex].schedules.splice(scheduleIndex, 1);
          }
        }
      } catch (err) {
        this.error = "스케줄 삭제에 실패했습니다.";
        console.error(err);
      } finally {
        this.loading = false;
      }
    },
  },
});
