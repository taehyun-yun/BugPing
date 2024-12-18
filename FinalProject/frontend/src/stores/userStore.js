import { defineStore } from "pinia";
export const useUserStore = defineStore('user',{
    state : () => ({
        userId : '',
        password : '',
        roles : [],
        company : '',
        companies : [],
        email : '',
    }),
    actions : {
        setUserId(userId) {
            this.userId = userId;
        },
        setPassword(password){
            this.password = password;
        },
        setRoles(roles){
            this.roles = roles;
        },
        setCompany(company) {
            this.company = { cname: company.cname, companyId: company.companyId }; // { cname : '' , companyId : ''}
            //alert(this.company.companyId);
        },
        setCompanies(companies){
            this.companies = companies;
        },
        setEmail(email){
            this.email = email;
        }
    },
    persist : true,
})