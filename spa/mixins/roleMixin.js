export default {
    computed: {

        hasCompanyAdminRole() {
            let user_role = localStorage.getItem("userrole");
            user_role = JSON.parse(user_role);
            if (user_role) {
              if (user_role.roles.includes("COMPANYADMIN")) {
                return true;
              }
            }
            return false;
        },
        hasSuperAdminRole() {
          let user_role = localStorage.getItem("userrole");
          user_role = JSON.parse(user_role);
          if (user_role) {
            if (user_role.roles.includes("SUPERADMIN")) {
              return true;
            }
          }
          return false;
        },
      }
  };
  
