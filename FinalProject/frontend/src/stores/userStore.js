import { defineStore } from "pinia";
export const useUserStore = defineStore("user", {
  state: () => ({
    userId: "",
    roles: [],
    company: { cname: "", companyId: "" }, // 객체로 초기화
    companies: [],
    email: "",
  }),
  actions: {
    setUserId(userId) {
      this.userId = userId;
    },
    setRoles(roles) {
      this.roles = roles;
    },
    setCompany(company) {
      this.company = { cname: company.cname, companyId: company.companyId }; // { cname: '', companyId: '' }
    },
    setCompanies(companies) {
      this.companies = companies;
    },
    setEmail(email) {
      this.email = email;
    },
  },
  persist: true,
});
