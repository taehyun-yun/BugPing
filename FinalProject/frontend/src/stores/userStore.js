import { defineStore } from "pinia";
export const useUserStore = defineStore('user',{
    state : () => ({
        userId : '',
        roles : '',
    }),
    actions : {
        setUserId(userId) {
            this.userId = userId;
        },
        setRoles(roles){
            this.roles = roles;
        },
    },
})