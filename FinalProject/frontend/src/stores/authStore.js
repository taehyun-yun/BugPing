import { defineStore } from "pinia";
export const useAuthStore = defineStore('auth',{
    state : () => ({
        userType : '',
    }),
    actions : {
        setUserType(type) {
            this.userType = type;
        }
    },
})