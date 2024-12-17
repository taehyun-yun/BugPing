import { defineStore } from "pinia";
export const useUserStore = defineStore('user',{
    state : () => ({
        userId : '',
        roles : [],
        company : '',
        companies : [],
    }),
    actions : {
        setUserId(userId) {
            this.userId = userId;
        },
        setRoles(roles){
            this.roles = roles;
        },
        setCompany(company){
            this.company = company;
        },
        setCompanies(companies){
            this.companies = companies;
        },
    },
    persist : true,
})